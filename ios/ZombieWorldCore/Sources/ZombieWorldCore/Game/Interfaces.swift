/// Which team an actor is on.
enum ZombieCapability: Hashable {
    case undead
    case alive
}

/// Something that heals its user when it hits.
protocol Healing: AnyObject {
    var healAmount: Int { get }
}

let defaultHealAmount = 10

/// Something with a percentage chance of hitting.
protocol HitProbability: AnyObject {
    var hitProbability: Int { get }
}

/// An actor that can carry coins.
protocol Wallet: AnyObject {
    var wealth: Int { get }
    func spendCoins(_ cost: Int)
    func addCoinToWallet(_ coin: Coin)
}

/// An Action that opens another menu to decide what to actually do.
protocol MenuAction: AnyObject {
    func getAction(_ actor: Actor, _ map: GameMap, _ display: Display) async throws -> Action
}

/// An item that can be crafted into another item.
protocol CraftableItem: AnyObject {
    func craftsInto() -> Item
}

/// Something that can be switched between two states (e.g. a door).
protocol Toggleable: AnyObject {
    func toggle()
    func switchToStateDescription(_ stateToSwitchTo: Bool) -> String
}

/// A way for an NPC to decide what to do. Returns nil if the behaviour doesn't apply right now.
protocol Behaviour {
    func getAction(_ actor: Actor, _ map: GameMap) -> Action?
}
