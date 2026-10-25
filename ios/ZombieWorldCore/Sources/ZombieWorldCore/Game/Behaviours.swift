/// Attacks an adjacent actor on the given team.
struct AttackBehaviour: Behaviour {
    let attackableTeam: ZombieCapability

    init(_ attackableTeam: ZombieCapability) {
        self.attackableTeam = attackableTeam
    }

    func getAction(_ actor: Actor, _ map: GameMap) -> Action? {
        for exit in Dice.shuffled(map.locationOf(actor).exits) {
            if let other = exit.destination.actor, other.hasCapability(attackableTeam) {
                return AttackAction(other)
            }
        }
        return nil
    }
}

struct CollectCoinsBehaviour: Behaviour {
    func getAction(_ actor: Actor, _ map: GameMap) -> Action? {
        let coins = map.locationOf(actor).items.compactMap { $0 as? Coin }
        return coins.isEmpty ? nil : CollectCoinsAction(coins)
    }
}

/// Eats food from the inventory, or picks up food from the ground.
struct EatBehaviour: Behaviour {
    func getAction(_ actor: Actor, _ map: GameMap) -> Action? {
        if let food = actor.inventory.first(where: { $0 is Food }) as? Food {
            return EatAction(food)
        }
        if let food = map.locationOf(actor).items.first(where: { $0 is Food }) {
            return PickUpItemAction(food)
        }
        return nil
    }
}

/// Harvests a ripe crop here or next door.
class HarvestBehaviour: Behaviour {
    func getAction(_ actor: Actor, _ map: GameMap) -> Action? {
        firstHarvestable(actor, map).map(HumanHarvestAction.init)
    }

    func firstHarvestable(_ actor: Actor, _ map: GameMap) -> Location? {
        let here = map.locationOf(actor)
        if let crop = here.ground as? Crop, crop.isRipe() {
            return here
        }
        for exit in here.exits {
            if let crop = exit.destination.ground as? Crop, crop.isRipe() {
                return exit.destination
            }
        }
        return nil
    }
}

/// Farmers sow crops on adjacent dirt, fertilise the crop they stand on and harvest ripe crops.
final class FarmBehaviour: HarvestBehaviour {
    private static let sowProbability = 33

    override func getAction(_ actor: Actor, _ map: GameMap) -> Action? {
        let here = map.locationOf(actor)
        if Dice.int(0..<100) < FarmBehaviour.sowProbability {
            for exit in here.exits where exit.destination.ground is Dirt {
                return SowAction(exit.destination)
            }
        }
        if let crop = here.ground as? Crop {
            if let toHarvest = firstHarvestable(actor, map) {
                return FarmerHarvestAction(toHarvest)
            } else if !crop.beenFertilised {
                return FertiliseAction(here)
            }
        }
        return nil
    }
}

/// Moves towards a target actor.
struct FollowBehaviour: Behaviour {
    let target: Actor

    func getAction(_ actor: Actor, _ map: GameMap) -> Action? {
        guard map.contains(target), map.contains(actor) else { return nil }
        let here = map.locationOf(actor)
        let there = map.locationOf(target)
        let currentDistance = distance(here, there)
        for exit in here.exits where exit.destination.canActorEnter(actor) {
            if distance(exit.destination, there) < currentDistance {
                return MoveActorAction(exit.destination, exit.name)
            }
        }
        return nil
    }

    private func distance(_ a: Location, _ b: Location) -> Int {
        abs(a.x - b.x) + abs(a.y - b.y)
    }
}

/// Breadth-first search for the nearest thing matching a type within `maxRange` steps, then takes
/// one step towards it. The Java version took a `Class<?>`; here a predicate does the matching.
final class HuntBehaviour: Behaviour {
    private let targetName: String
    private let isTarget: (AnyObject) -> Bool
    private let maxRange: Int

    init<T>(_ type: T.Type, name: String? = nil, range: Int) {
        self.targetName = name ?? String(describing: type)
        self.isTarget = { $0 is T }
        self.maxRange = range
    }

    /// Searches outwards one step at a time. Each search path only remembers its first step,
    /// which is all we need to move. (Same results as the Java version's list of whole paths.)
    private func hunt(_ actor: Actor, _ here: Location) -> Action? {
        let map = here.map
        let width = map.xRange.count
        var visited = [Bool](repeating: false, count: width * map.yRange.count)
        var layer: [(location: Location, firstStep: Location?)] = [(here, nil)]
        for _ in 0..<maxRange {
            var nextLayer: [(location: Location, firstStep: Location?)] = []
            for (location, firstStep) in layer {
                for exit in location.exits {
                    let destination = exit.destination
                    let index = destination.y * width + destination.x
                    if visited[index] || !destination.ground.canActorEnter(actor) {
                        continue
                    }
                    visited[index] = true
                    nextLayer.append((destination, firstStep ?? destination))
                }
            }
            layer = nextLayer
            if let found = layer.first(where: { containsTarget($0.location) }), let step = found.firstStep {
                return step.getMoveAction(actor, "towards a \(targetName)", nil)
            }
        }
        return nil
    }

    private func containsTarget(_ here: Location) -> Bool {
        if let actor = here.actor, isTarget(actor) { return true }
        if here.items.contains(where: { isTarget($0) }) { return true }
        return here.ground.map { isTarget($0) } ?? false
    }

    func getAction(_ actor: Actor, _ map: GameMap) -> Action? {
        if let zombie = actor as? Zombie, !zombie.canMove() { return nil }
        return hunt(actor, map.locationOf(actor))
    }
}

struct PickupWeaponBehaviour: Behaviour {
    func getAction(_ actor: Actor, _ map: GameMap) -> Action? {
        map.locationOf(actor).items.first { $0 is WeaponItem }.map(PickUpItemAction.init)
    }
}

/// Moves to a random adjacent square.
struct WanderBehaviour: Behaviour {
    func getAction(_ actor: Actor, _ map: GameMap) -> Action? {
        if let zombie = actor as? Zombie, !zombie.canMove() { return nil }
        let actions = map.locationOf(actor).exits.compactMap { exit -> Action? in
            exit.destination.canActorEnter(actor) ? exit.destination.getMoveAction(actor, "around", exit.hotKey) : nil
        }
        return Dice.element(of: actions)
    }
}
