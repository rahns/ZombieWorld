/// Attacks an adjacent actor with whatever weapon the attacker is holding.
class AttackAction: Action {
    let target: Actor?
    let targets: [Actor]

    init(_ target: Actor) {
        self.target = target
        self.targets = [target]
    }

    init(targets: [Actor]) {
        self.target = nil
        self.targets = targets
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        guard let target else { return nil }
        let weapon = actor.getWeapon()
        if misses(actor, weapon) {
            return "\(actor) misses \(target)"
        }
        let healResult = healAttacker(actor, weapon)
        let damage = weapon.damage
        let result = "\(actor) \(weapon.verb) \(target) for \(damage) damage" + healResult
        return hurt(target, damage, actor, map, result)
    }

    func healAttacker(_ actor: Actor, _ weapon: Weapon) -> String {
        guard let healing = weapon as? Healing else { return "" }
        actor.heal(healing.healAmount)
        return "\n\(actor) gained \(healing.healAmount) hit-points"
    }

    /// Weapon accuracy wins over actor accuracy; otherwise it's a coin flip.
    func misses(_ actor: Actor, _ weapon: Weapon) -> Bool {
        if let weapon = weapon as? HitProbability {
            return Dice.int(0..<100) > weapon.hitProbability
        }
        if let actor = actor as? HitProbability {
            return Dice.int(0..<100) > actor.hitProbability
        }
        return Dice.bool()
    }

    /// Damage dealt by something that isn't an actor (a turret).
    func executeAsItem(_ map: GameMap, _ damage: Int) -> String {
        guard let target else { return "" }
        return hurt(target, damage, nil, map, nil)
    }

    override func menuDescription(_ actor: Actor) -> String {
        "\(actor) attacks \(target.map(String.init(describing:)) ?? "nobody") \(healthStatus(of: target))"
    }

    override var menuKind: ActionKind { .attack }

    override func menuTarget(for actor: Actor, in map: GameMap) -> Location? {
        target.flatMap { map.actorLocations.locationOf($0) }
    }

    func healthStatus(of actor: Actor?) -> String {
        (actor as? ZombieActor)?.healthStatus ?? ""
    }

    private func hurt(_ target: Actor, _ damage: Int, _ actor: Actor?, _ map: GameMap, _ result: String?) -> String {
        target.hurt(damage)
        var result = result ?? ""
        if !target.isConscious() {
            processDeath(actor, map, target)
            if !result.isEmpty { result += "\n" }
            result += "\(target) is killed"
        }
        return result
    }

    /// Leaves a corpse, drops the target's items and removes it from the world. Humans killed by
    /// zombies will rise again.
    func processDeath(_ actor: Actor?, _ map: GameMap, _ target: Actor) {
        let shouldRise = actor != nil
            && target.hasCapability(ZombieCapability.alive)
            && actor!.hasCapability(ZombieCapability.undead)
        map.display?.noteDeath(target)
        let corpse = Corpse(target.name, shouldRise: shouldRise, map: map)
        map.locationOf(target).addItem(corpse)

        let dropActions = Actions()
        for item in target.inventory {
            dropActions.add(item.getDropAction())
        }
        for drop in dropActions {
            _ = drop.execute(target, map)
        }
        map.removeActor(target)
    }
}

/// Fires a gun at one target (sniper) or everything in a direction (shotgun).
final class ShootAction: AttackAction {
    private let gun: Gun
    private let direction: String?

    init(_ target: Actor, _ gun: Gun) {
        self.gun = gun
        self.direction = nil
        super.init(target)
    }

    init(targets: [Actor], _ gun: Gun, direction: String) {
        self.gun = gun
        self.direction = direction
        super.init(targets: targets)
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        var lines: [String] = []
        gun.ammo.reduceBulletCount()
        if targets.isEmpty {
            lines.append("\(actor) shoots at nobody")
        }
        for t in targets {
            if misses(actor, gun) {
                lines.append("\(actor) misses \(t).")
                continue
            }
            gun.aim(t, increaseLevel: false)  // aim but don't increase aim level
            let damage = gun.shootDamage()
            lines.append("\(actor) shoots \(t) for \(damage) damage")
            t.hurt(damage)
            if !t.isConscious() {
                processDeath(actor, map, t)
                lines.append("\(t) is killed")
            }
            let heal = healAttacker(actor, gun)
            if !heal.isEmpty { lines.append(String(heal.dropFirst())) }
        }
        return lines.joined(separator: "\n")
    }

    override func menuDescription(_ actor: Actor) -> String {
        if let direction {
            return "\(actor) shoots \(direction)"
        }
        return "\(actor) shoots at \(target.map(String.init(describing:)) ?? "nobody") \(healthStatus(of: target))"
    }

    override var menuKind: ActionKind { .shoot }
}

/// Opens the menu for a gun.
final class UseGunAction: Action, MenuAction {
    private let gun: Gun

    init(_ gun: Gun) {
        self.gun = gun
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? { nil }

    override func menuDescription(_ actor: Actor) -> String { "Use \(gun)" }

    override var menuKind: ActionKind { .gun }

    func getAction(_ actor: Actor, _ map: GameMap, _ display: Display) async throws -> Action {
        let sub = SubMenu(map: map)
        for a in gun.getActions(actor, map) {
            sub.addActionToMenu(a, actor)
        }
        sub.setFooter(gun.header)
        let action = try await sub.readInput(display)
        if let menuAction = action as? MenuAction {
            return try await menuAction.getAction(actor, map, display)
        }
        return action
    }
}

final class ChooseShootTargetAction: Action, MenuAction {
    private let gun: SniperRifle

    init(_ gun: SniperRifle) {
        self.gun = gun
    }

    func getAction(_ actor: Actor, _ map: GameMap, _ display: Display) async throws -> Action {
        let sub = SubMenu(map: map)
        for a in gun.getTargets(actor, map) {
            sub.addActionToMenu(ShootAction(a, gun), actor)
        }
        return try await sub.readInput(display)
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? { nil }

    override func menuDescription(_ actor: Actor) -> String { "Shoot" }

    override var menuKind: ActionKind { .shoot }
}

final class ChooseAimAction: Action, MenuAction {
    private let gun: SniperRifle

    init(_ gun: SniperRifle) {
        self.gun = gun
    }

    func getAction(_ actor: Actor, _ map: GameMap, _ display: Display) async throws -> Action {
        let sub = SubMenu(map: map)
        for a in gun.getTargets(actor, map) {
            sub.addActionToMenu(AimerAction(a, gun), actor)
        }
        return try await sub.readInput(display)
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? { nil }

    override func menuDescription(_ actor: Actor) -> String { "Aim" }

    override var menuKind: ActionKind { .aim }
}

final class AimerAction: Action {
    private let target: Actor
    private let gun: SniperRifle

    init(_ target: Actor, _ gun: SniperRifle) {
        self.target = target
        self.gun = gun
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        gun.aim(target, increaseLevel: true)
        return "\(actor) aimed at \(target)"
    }

    override func menuDescription(_ actor: Actor) -> String { "\(actor) aims at \(target)" }

    override var menuKind: ActionKind { .aim }

    override func menuTarget(for actor: Actor, in map: GameMap) -> Location? {
        map.actorLocations.locationOf(target)
    }
}

final class ReloadAction: Action {
    private let ammo: AmmunitionCartridge
    private let gun: Gun

    init(_ ammo: AmmunitionCartridge, _ gun: Gun) {
        self.ammo = ammo
        self.gun = gun
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        gun.reload(ammo)
        actor.removeItemFromInventory(ammo)
        return "\(actor) reloads \(gun) with \(ammo.bulletCount) bullets"
    }

    override func menuDescription(_ actor: Actor) -> String { "Reload with \(ammo.bulletCount) bullets" }

    override var menuKind: ActionKind { .reload }
}

final class SetUpTurretAction: DropItemAction {
    private let turret: Turret

    init(_ turret: Turret) {
        self.turret = turret
        super.init(turret)
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        if !map.locationOf(actor).containsItem(turret) {  // drop it first if it isn't already on the ground
            _ = super.execute(actor, map)
        }
        turret.setUp(map)
        return "\(actor) set up a turret"
    }

    override func menuDescription(_ actor: Actor) -> String { "\(actor) sets up the turret here" }

    override var menuKind: ActionKind { .setUp }
}

/// Mambo Marie's chant, which summons new zombies at random places on the map.
final class ChantSpawnAction: Action {
    private var zombieCount: Int?
    private let numberToSpawn: Int
    private static let maxSpawnTries = 1000  // give up if the map is full

    init(_ numberToSpawn: Int) {
        self.numberToSpawn = numberToSpawn
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        var count = 0
        for _ in 0..<numberToSpawn {
            for _ in 0..<ChantSpawnAction.maxSpawnTries {
                let x = Dice.int(0..<max(1, map.xRange.max))
                let y = Dice.int(0..<max(1, map.yRange.max))
                let newZombie = Zombie("A Mambo Minion", map)
                if map.at(x, y).canActorEnter(newZombie) {
                    map.addActor(newZombie, map.at(x, y))
                    count += 1
                    break
                }
            }
        }
        zombieCount = count
        return menuDescription(actor)
    }

    override func menuDescription(_ actor: Actor) -> String {
        "\(actor) chants and spawns \(zombieCount ?? numberToSpawn) new zombies"
    }
}

/// Removes the player from the game.
final class EndLifeAction: Action {
    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        map.removeActor(actor)
        return "\(actor) took the easy way out"
    }

    override func menuDescription(_ actor: Actor) -> String { "Quit game" }

    override var menuKind: ActionKind { .quit }

    override var hotkey: String? { "+" }
}
