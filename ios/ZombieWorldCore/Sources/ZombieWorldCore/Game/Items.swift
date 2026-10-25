class PortableItem: Item {
    init(_ name: String, _ displayChar: Character) {
        super.init(name, displayChar, portable: true)
    }
}

final class Plank: WeaponItem {
    init() { super.init("plank", ")", 40, "whacks") }
}

final class Hoe: WeaponItem {
    init() { super.init("hoe", "]", 35, "hoes down") }
}

final class ZombieClub: WeaponItem {
    init() { super.init("zombie club", "c", 20, "clubbed") }
}

final class ZombieMace: WeaponItem {
    init() { super.init("zombie mace", "m", 25, "struck") }
}

/// A zombie's limb. While carried it can be crafted into a weapon.
class Limb: WeaponItem, CraftableItem {
    init(_ name: String, _ displayChar: Character, _ damage: Int) {
        super.init(name, displayChar, damage, "whacked")
    }

    func craftsInto() -> Item { fatalError("\(type(of: self)) must override craftsInto") }

    override func tick(at currentLocation: Location, carriedBy actor: Actor) {
        if allowableActions.contains(where: { $0 is CraftAction }) { return }
        allowableActions.add(CraftAction(self))
    }

    override func tick(at currentLocation: Location) {
        if let craft = allowableActions.first(where: { $0 is CraftAction }) {
            allowableActions.remove(craft)
        }
    }
}

final class Arm: Limb {
    init() { super.init("arm", "!", 15) }

    override func craftsInto() -> Item { ZombieClub() }
}

final class Leg: Limb {
    init() { super.init("leg", "L", 17) }

    override func craftsInto() -> Item { ZombieMace() }
}

final class Food: Item, Healing {
    init() {
        super.init("food", "~", portable: true)
        allowableActions.add(EatAction(self))
    }

    var healAmount: Int { 10 }
}

final class Coin: Item {
    init() { super.init("coin", "$", portable: false) }

    /// Coins can be dropped (e.g. when their owner dies) despite not being portable.
    override func getDropAction() -> Action? { DropItemAction(self) }
}

final class AmmunitionCartridge: Item {
    private(set) var bulletCount = 5

    init() { super.init("ammunition cartridge", "a", portable: true) }

    func reduceBulletCount() {
        if !isEmpty { bulletCount -= 1 }
    }

    var isEmpty: Bool { bulletCount == 0 }
}

/// A non-portable item that carries actors to another location (the train).
final class Vehicle: Item {
    init(_ name: String, _ displayChar: Character, destination: Location, destinationName: String) {
        super.init(name, displayChar, portable: false)
        allowableActions.add(MoveActorAction(destination, "into the \(name) and rides to \(destinationName)"))
    }
}

/// Something for sale in a shop. The Java version sold the same item instance on every purchase;
/// here each purchase makes a fresh item.
struct Product {
    let item: Item
    let cost: Int
    private let make: () -> Item

    init(cost: Int, _ make: @escaping () -> Item) {
        self.item = make()
        self.cost = cost
        self.make = make
    }

    func makeItem() -> Item { make() }
}

/// A dead actor. Humans killed by zombies rise again 5-10 turns later.
final class Corpse: PortableItem {
    private let shouldRise: Bool
    private let map: GameMap
    private var riseIn = Dice.int(5...10)
    private let oldName: String

    init(_ name: String, shouldRise: Bool, map: GameMap) {
        self.shouldRise = shouldRise
        self.map = map
        self.oldName = name
        super.init("dead \(name)", "%")
    }

    override func tick(at currentLocation: Location) {
        countDown(currentLocation, nil)
    }

    override func tick(at currentLocation: Location, carriedBy actor: Actor) {
        countDown(currentLocation, actor)
    }

    private func countDown(_ currentLocation: Location, _ actor: Actor?) {
        guard shouldRise else { return }
        if riseIn == 0 {
            riseFromTheDead(currentLocation, actor)
        } else {
            riseIn -= 1
        }
    }

    private func riseFromTheDead(_ currentLocation: Location, _ actor: Actor?) {
        let newZombie = Zombie("Zombie \(oldName)", map)
        let success: Bool
        if currentLocation.canActorEnter(newZombie) {
            currentLocation.addActor(newZombie)
            success = true
        } else {
            success = addAtValidAdjacentLocation(currentLocation, newZombie)
        }

        if success {
            if let actor {
                actor.removeItemFromInventory(self)
            } else {
                currentLocation.removeItem(self)
            }
            map.display?.println("\(oldName) rose from the dead as a zombie")
        }
    }

    private func addAtValidAdjacentLocation(_ centreLocation: Location, _ newZombie: Actor) -> Bool {
        let validExits = centreLocation.exits.filter { $0.destination.canActorEnter(newZombie) }
        guard let exit = Dice.element(of: validExits) else { return false }
        exit.destination.addActor(newZombie)
        return true
    }
}

/// An automatic turret that shoots the nearest zombie every turn once set up on the ground.
final class Turret: Item {
    private var isSetUp = false
    private var setUpAction: SetUpTurretAction!
    private weak var currentMap: GameMap?
    private static let damage = 5
    private static let hitChance = 60

    init() {
        super.init("turret", "x", portable: true)
        setUpAction = SetUpTurretAction(self)
        allowableActions.add(setUpAction)
    }

    /// Called only when the item is on the ground.
    override func tick(at currentLocation: Location) {
        if isSetUp {
            shoot(findNearestTarget(currentLocation, ZombieCapability.undead))
        }
    }

    /// Called only when the item is in an inventory.
    override func tick(at currentLocation: Location, carriedBy actor: Actor) {
        isSetUp = false
        displayChar = "x"
        if !allowableActions.contains(where: { $0 === setUpAction }) {
            allowableActions.add(setUpAction)
        }
    }

    func setUp(_ map: GameMap) {
        isSetUp = true
        allowableActions.remove(setUpAction)
        displayChar = "X"
        currentMap = map
    }

    /// Breadth-first search for the closest actor on `targetTeam`.
    private func findNearestTarget(_ start: Location, _ targetTeam: ZombieCapability) -> Actor? {
        var queue = [start]
        var head = 0
        var visited: Set<Location> = [start]
        while head < queue.count {
            let current = queue[head]
            head += 1
            if let actor = current.actor, actor.hasCapability(targetTeam) {
                return actor
            }
            for exit in current.exits where !visited.contains(exit.destination) {
                queue.append(exit.destination)
                visited.insert(exit.destination)
            }
        }
        return nil
    }

    private func shoot(_ targetActor: Actor?) {
        guard let targetActor, let currentMap, let display = currentMap.display else { return }
        if Dice.int(0..<100) > Turret.hitChance {
            display.println("A turret misses \(targetActor)")
        } else {
            display.println("A turret shoots \(targetActor) for \(Turret.damage) damage")
            let result = AttackAction(targetActor).executeAsItem(currentMap, Turret.damage)
            if !result.isEmpty { display.println(result) }
        }
    }
}
