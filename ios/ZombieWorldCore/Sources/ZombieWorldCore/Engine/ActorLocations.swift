/// Bidirectional mapping between Actors and Locations across all maps.
/// Port of `edu.monash.fit2099.engine.ActorLocations`.
final class ActorLocations: Sequence {
    private var actorToLocation: [ObjectIdentifier: Location] = [:]
    /// Insertion order, so turn order is stable between rounds.
    private var order: [Actor] = []
    private(set) var player: Actor?

    func setPlayer(_ player: Actor) { self.player = player }

    func add(_ actor: Actor, _ location: Location) {
        precondition(actorToLocation[ObjectIdentifier(actor)] == nil, "\(actor) is already in the world")
        precondition(location.occupant == nil, "There is already an actor at (\(location.x), \(location.y))")
        actorToLocation[ObjectIdentifier(actor)] = location
        location.occupant = actor
        order.append(actor)
    }

    func remove(_ actor: Actor) {
        guard let location = actorToLocation.removeValue(forKey: ObjectIdentifier(actor)) else { return }
        location.occupant = nil
        order.removeFirstIdentical(actor)
    }

    func move(_ actor: Actor, _ newLocation: Location) {
        precondition(!isAnActorAt(newLocation), "Can't move to another actor")
        actorToLocation[ObjectIdentifier(actor)]?.occupant = nil
        actorToLocation[ObjectIdentifier(actor)] = newLocation
        newLocation.occupant = actor
    }

    func contains(_ actor: Actor) -> Bool { actorToLocation[ObjectIdentifier(actor)] != nil }

    func isAnActorAt(_ location: Location) -> Bool { location.occupant != nil }

    func getActorAt(_ location: Location) -> Actor? { location.occupant }

    func locationOf(_ actor: Actor) -> Location? { actorToLocation[ObjectIdentifier(actor)] }

    /// Iterates over a snapshot of the actors, player first. Actors removed during iteration are
    /// skipped and actors added during iteration are not visited.
    struct Iterator: IteratorProtocol {
        let owner: ActorLocations
        var remaining: [Actor]

        mutating func next() -> Actor? {
            while !remaining.isEmpty {
                let actor = remaining.removeFirst()
                if owner.contains(actor) { return actor }
            }
            return nil
        }
    }

    func makeIterator() -> Iterator {
        var actors = order
        if let player, actors.containsIdentical(player) {
            actors.removeFirstIdentical(player)
            actors.insert(player, at: 0)
        }
        return Iterator(owner: self, remaining: actors)
    }
}
