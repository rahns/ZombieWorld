/// Base class for anything that can take a turn. Port of `edu.monash.fit2099.engine.Actor`.
class Actor: Capable, Printable, CustomStringConvertible {
    private let capabilities = Capabilities()
    var name: String
    var displayChar: Character
    private(set) var inventory: [Item] = []
    var maxHitPoints: Int
    var hitPoints: Int

    init(_ name: String, _ displayChar: Character, _ hitPoints: Int) {
        self.name = name
        self.displayChar = displayChar
        self.maxHitPoints = hitPoints
        self.hitPoints = hitPoints
    }

    var description: String { name }

    func addItemToInventory(_ item: Item) { inventory.append(item) }

    func removeItemFromInventory(_ item: Item) { inventory.removeFirstIdentical(item) }

    /// Select and return an action to perform on the current turn.
    /// Throws only if the game was cancelled while waiting for input.
    func playTurn(_ actions: Actions, _ lastAction: Action, _ map: GameMap, _ display: Display) async throws -> Action {
        fatalError("\(type(of: self)) must override playTurn")
    }

    /// Actions that `otherActor` can perform on this actor.
    func getAllowableActions(_ otherActor: Actor, _ direction: String, _ map: GameMap) -> Actions {
        Actions()
    }

    func isConscious() -> Bool { hitPoints > 0 }

    func heal(_ points: Int) {
        hitPoints = min(hitPoints + points, maxHitPoints)
    }

    func hurt(_ points: Int) {
        hitPoints -= points
    }

    func getWeapon() -> Weapon {
        for item in inventory {
            if let weapon = item.asWeapon() { return weapon }
        }
        return getIntrinsicWeapon()
    }

    func getIntrinsicWeapon() -> IntrinsicWeapon {
        IntrinsicWeapon(5, "punches")
    }

    func hasCapability(_ capability: AnyHashable) -> Bool {
        inventory.contains { $0.hasCapability(capability) } || capabilities.hasCapability(capability)
    }

    func addCapability(_ capability: AnyHashable) { capabilities.addCapability(capability) }
    func removeCapability(_ capability: AnyHashable) { capabilities.removeCapability(capability) }
}
