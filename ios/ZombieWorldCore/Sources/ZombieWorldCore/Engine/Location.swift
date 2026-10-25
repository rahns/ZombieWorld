/// One square of a GameMap. Port of `edu.monash.fit2099.engine.Location`.
final class Location: Printable, Hashable {
    unowned let map: GameMap
    let x: Int
    let y: Int
    private(set) var items: [Item] = []
    var ground: Ground!
    private(set) var exits: [Exit] = []

    init(map: GameMap, x: Int, y: Int) {
        self.map = map
        self.x = x
        self.y = y
    }

    func addItem(_ item: Item) { items.append(item) }

    func removeItem(_ item: Item) { items.removeFirstIdentical(item) }

    func containsItem(_ item: Item) -> Bool { items.containsIdentical(item) }

    /// Called once per turn so that the ground and items here can experience the passage of time.
    func tick() {
        ground.tick(self)
        for item in items {  // iterates a copy, in case an item wants to leave
            item.tick(at: self)
        }
    }

    func containsAnActor() -> Bool { map.isAnActorAt(self) }

    var actor: Actor? { map.getActorAt(self) }

    func addActor(_ actor: Actor) { map.addActor(actor, self) }

    func getMoveAction(_ actor: Actor, _ direction: String, _ hotKey: String?) -> Action? {
        canActorEnter(actor) ? MoveActorAction(self, direction, hotKey) : nil
    }

    func canActorEnter(_ actor: Actor) -> Bool {
        !map.isAnActorAt(self) && ground.canActorEnter(actor)
    }

    var displayChar: Character {
        if let actor { return actor.displayChar }
        if let item = items.last { return item.displayChar }
        return ground.displayChar
    }

    func addExit(_ exit: Exit) { exits.append(exit) }

    static func == (lhs: Location, rhs: Location) -> Bool {
        lhs.map === rhs.map && lhs.x == rhs.x && lhs.y == rhs.y
    }

    func hash(into hasher: inout Hasher) {
        hasher.combine(ObjectIdentifier(map))
        hasher.combine(x)
        hasher.combine(y)
    }
}

/// A route from one Location to another.
struct Exit {
    let name: String
    let destination: Location
    let hotKey: String?
}

/// A run of consecutive integers. Port of `edu.monash.fit2099.engine.NumberRange`.
struct NumberRange: Sequence {
    let start: Int
    let count: Int

    init(_ start: Int, _ count: Int) {
        self.start = start
        self.count = count
    }

    var min: Int { start }
    var max: Int { start + count - 1 }

    func contains(_ i: Int) -> Bool { i >= start && i < start + count }

    func makeIterator() -> Range<Int>.Iterator { (start..<start + count).makeIterator() }
}
