/// Base class for anything that can be picked up, dropped or lie on the ground.
/// Port of `edu.monash.fit2099.engine.Item`.
class Item: Capable, Printable, CustomStringConvertible {
    var portable: Bool
    var name: String
    var displayChar: Character
    let allowableActions = Actions()
    private let capabilities = Capabilities()

    init(_ name: String, _ displayChar: Character, portable: Bool) {
        self.name = name
        self.displayChar = displayChar
        self.portable = portable
    }

    /// Called once per turn while the item is in an actor's inventory.
    func tick(at currentLocation: Location, carriedBy actor: Actor) {}

    /// Called once per turn while the item is on the ground.
    func tick(at currentLocation: Location) {}

    var description: String { name }

    func getPickUpAction() -> Action? {
        portable ? PickUpItemAction(self) : nil
    }

    func getDropAction() -> Action? {
        portable ? DropItemAction(self) : nil
    }

    func getAllowableActions() -> [Action] { allowableActions.list }

    func asWeapon() -> Weapon? { self as? Weapon }

    func hasCapability(_ capability: AnyHashable) -> Bool { capabilities.hasCapability(capability) }
    func addCapability(_ capability: AnyHashable) { capabilities.addCapability(capability) }
    func removeCapability(_ capability: AnyHashable) { capabilities.removeCapability(capability) }
}
