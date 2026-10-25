/// An action that does nothing. Port of `edu.monash.fit2099.engine.DoNothingAction`.
class DoNothingAction: Action {
    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        menuDescription(actor)
    }

    override func menuDescription(_ actor: Actor) -> String {
        "\(actor) does nothing"
    }

    override var hotkey: String? { "5" }
}

/// Drops an item from the actor's inventory onto the ground.
class DropItemAction: Action {
    let item: Item

    init(_ item: Item) {
        self.item = item
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        actor.removeItemFromInventory(item)
        map.locationOf(actor).addItem(item)
        return menuDescription(actor)
    }

    override func menuDescription(_ actor: Actor) -> String {
        "\(actor) drops the \(item)"
    }
}

/// Picks an item up off the ground.
class PickUpItemAction: Action {
    let item: Item

    init(_ item: Item) {
        self.item = item
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        map.locationOf(actor).removeItem(item)
        actor.addItemToInventory(item)
        return menuDescription(actor)
    }

    override func menuDescription(_ actor: Actor) -> String {
        "\(actor) picks up the \(item)"
    }
}

/// Moves an actor to a location (possibly on another map).
class MoveActorAction: Action {
    let moveToLocation: Location
    let direction: String
    private let hotKey: String?

    init(_ moveToLocation: Location, _ direction: String, _ hotKey: String? = nil) {
        self.moveToLocation = moveToLocation
        self.direction = direction
        self.hotKey = hotKey
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        // The Java engine threw here (crashing the game) if the destination was occupied, which
        // could happen with the train or Mambo Marie's teleport.
        if moveToLocation.containsAnActor() {
            return "\(actor) can't move \(direction) - the way is blocked"
        }
        map.moveActor(actor, moveToLocation)
        return menuDescription(actor)
    }

    override func menuDescription(_ actor: Actor) -> String {
        "\(actor) moves \(direction)"
    }

    override var hotkey: String? { hotKey }
}
