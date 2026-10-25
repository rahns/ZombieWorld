/// Base class for ranged weapons. In melee a gun just whacks.
class Gun: WeaponItem, HitProbability {
    var ammo: AmmunitionCartridge!
    var aimLevel = 0

    init(_ name: String, _ displayChar: Character, meleeDamage: Int) {
        super.init(name, displayChar, meleeDamage, "whacks")
    }

    var hitProbability: Int { 70 }

    func reload(_ ammo: AmmunitionCartridge) {
        self.ammo = ammo
    }

    func aim(_ target: Actor, increaseLevel: Bool) {
        if increaseLevel { aimLevel += 1 }
    }

    func shootDamage() -> Int { fatalError("\(type(of: self)) must override shootDamage") }

    func getActions(_ actor: Actor, _ map: GameMap) -> [Action] {
        fatalError("\(type(of: self)) must override getActions")
    }

    /// Bullet symbol used in the ammo gauge.
    var bulletSymbol: Character { "-" }

    var header: String {
        let count = ammo.bulletCount
        return "Ammo: " + String(repeating: bulletSymbol, count: count) + (count == 0 ? "Empty" : "")
    }

    func resetAimLevel() {
        aimLevel = 0
    }

    /// The reload action for the first cartridge in the actor's inventory, if any.
    func reloadAction(for actor: Actor) -> Action? {
        guard let cartridge = actor.inventory.first(where: { $0 is AmmunitionCartridge }) as? AmmunitionCartridge else {
            return nil
        }
        return ReloadAction(cartridge, self)
    }
}

/// Blasts everything in a cone three squares deep.
final class Shotgun: Gun {
    private var x = 0
    private var y = 0

    init() {
        super.init("shotgun", "S", meleeDamage: 13)
        ammo = AmmunitionCartridge()
        allowableActions.add(UseGunAction(self))
    }

    override func shootDamage() -> Int { 15 }

    override var hitProbability: Int { 75 }

    override var bulletSymbol: Character { "=" }

    override func getActions(_ actor: Actor, _ map: GameMap) -> [Action] {
        var actions: [Action] = []
        if !ammo.isEmpty {
            let directions = ["North", "South", "East", "West", "North-East", "North-West", "South-East", "South-West"]
            let actorLocation = map.locationOf(actor)
            x = actorLocation.x
            y = actorLocation.y
            for direction in directions {
                actions.append(createActionForDirection(map, direction, actor))
            }
        }
        if let reload = reloadAction(for: actor) {
            actions.append(reload)
        }
        actions.append(DoNothingAction())
        return actions
    }

    private func directX(_ direction: String, _ x2: Int, _ y2: Int) -> Int {
        switch direction {
        case "South", "North": return x + x2
        case "East": return x + y2
        case "West": return x - y2
        case "North-East", "South-East": return x + abs(x2)
        case "North-West", "South-West": return x - abs(x2)
        default: return 0
        }
    }

    private func directY(_ direction: String, _ y2: Int, _ x2: Int) -> Int {
        switch direction {
        case "South": return y + y2
        case "North": return y - y2
        case "East", "West": return y + x2
        case "North-East", "North-West": return y - abs(x2)
        case "South-East", "South-West": return y + abs(x2)
        default: return 0
        }
    }

    private func createActionForDirection(_ map: GameMap, _ direction: String, _ actor: Actor) -> ShootAction {
        var locations: [Location] = []
        for y2 in stride(from: 3, to: 0, by: -1) {
            for x2 in -y2..<y2 {
                let x3 = directX(direction, x2, y2)
                let y3 = directY(direction, y2, x2)
                if map.xRange.contains(x3) && map.yRange.contains(y3) {
                    let placeToShoot = map.at(x3, y3)
                    if !locations.contains(placeToShoot) {
                        locations.append(placeToShoot)
                    }
                }
            }
        }
        let targets = locations.compactMap { map.getActorAt($0) }.filter { $0 !== actor }
        return ShootAction(targets: targets, self, direction: direction)
    }
}

/// Shoots any zombie on the map. Aiming for consecutive turns increases accuracy and damage.
final class SniperRifle: Gun, CraftableItem {
    private weak var target: Actor?
    private let craftsIntoItem: Item

    init(craftsInto craftsIntoItem: Item) {
        self.craftsIntoItem = craftsIntoItem
        super.init("Sniper Rifle", "R", meleeDamage: 15)
        allowableActions.add(UseGunAction(self))
        allowableActions.add(CraftAction(self))
        ammo = AmmunitionCartridge()
    }

    override func getActions(_ actor: Actor, _ map: GameMap) -> [Action] {
        var actions: [Action] = []
        if !ammo.isEmpty {
            actions.append(ChooseShootTargetAction(self))
            actions.append(ChooseAimAction(self))
        }
        if let reload = reloadAction(for: actor) {
            actions.append(reload)
        }
        actions.append(DoNothingAction())
        return actions
    }

    override var hitProbability: Int {
        switch aimLevel {
        case 0: return 75
        case 1: return 90
        default: return 100
        }
    }

    override func shootDamage() -> Int {
        switch aimLevel {
        case 0: return damage
        case 1: return 2 * damage
        default: return target?.maxHitPoints ?? damage  // instant kill
        }
    }

    override func aim(_ target: Actor, increaseLevel: Bool) {
        if target === self.target {
            if increaseLevel { aimLevel += 1 }
        } else {
            aimLevel = 0
        }
        self.target = target
    }

    /// All zombies on the map, if the shooter is alive.
    func getTargets(_ actor: Actor, _ map: GameMap) -> [Actor] {
        var targets: [Actor] = []
        for x in map.xRange {
            for y in map.yRange {
                if let a = map.getActorAt(map.at(x, y)),
                   actor.hasCapability(ZombieCapability.alive) && a.hasCapability(ZombieCapability.undead) {
                    targets.append(a)
                }
            }
        }
        return targets
    }

    override var bulletSymbol: Character { ">" }

    func craftsInto() -> Item { craftsIntoItem }
}
