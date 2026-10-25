/// Base class for the actors in this game: they have a team, an accuracy and a list of behaviours.
class ZombieActor: Actor, HitProbability {
    let hitProbability: Int
    var behaviours: [Behaviour] = []

    init(_ name: String, _ displayChar: Character, _ hitPoints: Int, team: ZombieCapability, hitProbability: Int) {
        self.hitProbability = hitProbability
        super.init(name, displayChar, hitPoints)
        addCapability(team)
    }

    /// Actors on opposite teams can attack each other.
    override func getAllowableActions(_ otherActor: Actor, _ direction: String, _ map: GameMap) -> Actions {
        let list = super.getAllowableActions(otherActor, direction, map)
        if otherActor.hasCapability(ZombieCapability.undead) != hasCapability(ZombieCapability.undead) {
            list.add(AttackAction(self))
        }
        return list
    }

    override func hurt(_ points: Int) {
        hitPoints -= points
        for case let rifle as SniperRifle in inventory {
            rifle.resetAimLevel()
        }
    }

    var healthStatus: String { "(\(hitPoints)/\(maxHitPoints))" }
}

class Human: ZombieActor {
    init(_ name: String) {
        super.init(name, "H", 50, team: .alive, hitProbability: 70)
        behaviours.append(EatBehaviour())
        behaviours.append(AttackBehaviour(.undead))
        behaviours.append(HarvestBehaviour())
        behaviours.append(PickupWeaponBehaviour())
        behaviours.append(HuntBehaviour(Food.self, range: 6))
        behaviours.append(HuntBehaviour(Weapon.self, name: "Weapon", range: 4))  // only 4 so they don't all run for the plank
        behaviours.append(WanderBehaviour())
    }

    /// For subclasses that set up their own behaviours.
    init(_ name: String, _ displayChar: Character, _ hitPoints: Int) {
        super.init(name, displayChar, hitPoints, team: .alive, hitProbability: 100)
    }

    override func playTurn(_ actions: Actions, _ lastAction: Action, _ map: GameMap, _ display: Display) async throws -> Action {
        for behaviour in behaviours {
            if let action = behaviour.getAction(self, map) { return action }
        }
        return DoNothingAction()
    }
}

final class Farmer: Human {
    override init(_ name: String) {
        super.init(name, "f", 100)
        behaviours.append(PickupWeaponBehaviour())
        behaviours.append(AttackBehaviour(.undead))
        behaviours.append(FarmBehaviour())
        behaviours.append(HuntBehaviour(Crop.self, range: 10))
        behaviours.append(WanderBehaviour())
        addItemToInventory(Hoe())
    }
}

/// The human player, who chooses actions from a menu and carries coins.
final class Player: Human, Wallet {
    private let mainMenu = Menu()
    private var wallet: [Coin] = []

    override init(_ name: String, _ displayChar: Character, _ hitPoints: Int) {
        super.init(name, displayChar, hitPoints)
    }

    var wealth: Int { wallet.count }

    func addCoinToWallet(_ coin: Coin) { wallet.append(coin) }

    func spendCoins(_ cost: Int) {
        guard cost <= wealth else { return }
        wallet.removeLast(cost)
    }

    override func playTurn(_ actions: Actions, _ lastAction: Action, _ map: GameMap, _ display: Display) async throws -> Action {
        display.status = PlayerStatus(name: name, hitPoints: hitPoints, maxHitPoints: maxHitPoints, coins: wealth)
        actions.add(HarvestBehaviour().getAction(self, map))
        actions.add(CollectCoinsBehaviour().getAction(self, map))
        actions.add(EndLifeAction())

        if let next = lastAction.nextAction { return next }

        let action = try await mainMenu.showMenu(self, actions, display)
        if let menuAction = action as? MenuAction {
            return try await menuAction.getAction(self, map, display)
        }
        return action
    }
}

final class Zombie: ZombieActor {
    private let map: GameMap
    private var canMoveThisTurn = false
    private var limbs: [Limb] = [Arm(), Arm(), Leg(), Leg()]
    private static let punchProbabilityPerArm = 25
    private static let speech = ["groaaaaan", "braaaains", "buurp", "grrrr", "gargle gargle", "akhgrjbrdrd"]

    init(_ name: String, _ gameMap: GameMap) {
        map = gameMap
        super.init(name, "Z", 50, team: .undead, hitProbability: 50)
        addItemToInventory(Coin())  // dropped when the zombie is killed
        behaviours.append(PickupWeaponBehaviour())
        behaviours.append(AttackBehaviour(.alive))
        behaviours.append(HuntBehaviour(Human.self, range: 11))
        behaviours.append(WanderBehaviour())
    }

    /// Zombies with fewer arms tend to drop what they're holding.
    override func getWeapon() -> Weapon {
        for item in inventory {
            guard let weapon = item.asWeapon() else { continue }
            let arms = limbCount(Arm.self)
            if arms == 0 || (arms == 1 && Bool.random()) {
                removeItemFromInventory(item)
                dropItem(item)
                map.display?.println("\(name) dropped the \(item) it was holding")
                break
            }
            return weapon
        }
        return getIntrinsicWeapon()
    }

    /// 50%, 25% and 0% chance of punching with 2, 1 and 0 arms; otherwise bites.
    override func getIntrinsicWeapon() -> IntrinsicWeapon {
        let punchChance = Zombie.punchProbabilityPerArm * limbCount(Arm.self)
        return Int.random(in: 0..<100) < punchChance ? Punch() : Bite()
    }

    override func playTurn(_ actions: Actions, _ lastAction: Action, _ map: GameMap, _ display: Display) async throws -> Action {
        if Int.random(in: 0..<100) > 90 {
            display.println("\(name) says '\(Zombie.speech.randomElement()!)'")
        }
        for behaviour in behaviours {
            if let action = behaviour.getAction(self, map) {
                if action is MoveActorAction {
                    canMoveThisTurn = false
                }
                return action
            }
        }
        canMoveThisTurn = true
        return DoNothingAction()
    }

    override func hurt(_ points: Int) {
        super.hurt(points)
        if Int.random(in: 0..<100) < 25 {
            knockOffLimb()
        }
    }

    private func knockOffLimb() {
        guard !limbs.isEmpty else { return }
        let selected = Int.random(in: 0..<limbs.count)
        map.display?.println("One of \(name)'s \(limbs[selected])s flung off.")
        dropItem(limbs[selected])
        limbs.remove(at: selected)
    }

    private func dropItem(_ item: Item) {
        guard let exit = map.locationOf(self).exits.randomElement() else { return }
        exit.destination.addItem(item)
    }

    private func limbCount<T: Limb>(_ type: T.Type) -> Int {
        limbs.filter { $0 is T }.count
    }

    /// Zombies with one leg can only move every other turn, and can't move at all with none.
    func canMove() -> Bool {
        let legs = limbCount(Leg.self)
        return legs == 2 || (legs == 1 && canMoveThisTurn)
    }
}

final class Punch: IntrinsicWeapon, HitProbability {
    init() { super.init(5, "punches") }

    var hitProbability: Int { 60 }
}

final class Bite: IntrinsicWeapon, Healing, HitProbability {
    init() { super.init(10, "bites") }

    /// The Java version misspelled this method (`gethealAmount`), so bites healed the default 10.
    var healAmount: Int { 5 }

    var hitProbability: Int { 20 }
}

/// The voodoo priestess. She appears from her void now and then, chants up new zombies every
/// 10 turns and returns to the void after 30 turns.
final class MamboMarie: ZombieActor {
    private let mambosVoid: Location
    private let appearLocation: Location
    private var turnsSinceAppearing = 0

    init(void mambosVoid: Location, appearsAt appearLocation: Location) {
        self.mambosVoid = mambosVoid
        self.appearLocation = appearLocation
        super.init("Mambo Marie", "M", 200, team: .undead, hitProbability: 70)
        for _ in 0..<15 {  // dropped when she is killed
            addItemToInventory(Coin())
        }
    }

    override func playTurn(_ actions: Actions, _ lastAction: Action, _ map: GameMap, _ display: Display) async throws -> Action {
        if mambosVoid.actor === self {
            if Int.random(in: 0..<100) < 5 {
                turnsSinceAppearing = 0
                return MoveActorAction(appearLocation, "from her void to the compound")
            }
            return DoNothingCustomMessageAction("sits in her void")
        }
        turnsSinceAppearing += 1
        if turnsSinceAppearing == 30 {
            return MoveActorAction(mambosVoid, "back into her void")
        } else if turnsSinceAppearing % 10 == 0 {
            return ChantSpawnAction(5)
        }
        return WanderBehaviour().getAction(self, map) ?? DoNothingAction()
    }
}
