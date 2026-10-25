/// A rectangular grid of Locations. Port of `edu.monash.fit2099.engine.GameMap`.
final class GameMap {
    private(set) var widths = NumberRange(0, 0)
    private(set) var heights = NumberRange(0, 0)
    private var map: [[Location]] = []  // map[x][y]; 0,0 is the top left
    var actorLocations: ActorLocations!
    /// Set by the World when the map is added; lets game objects print messages.
    weak var display: Display?
    let groundFactory: GroundFactory

    init(_ groundFactory: GroundFactory, _ groundChar: Character, width: Int, height: Int) {
        precondition(width > 0 && height > 0)
        self.groundFactory = groundFactory
        initMap(width: width, height: height)
        for x in widths {
            for y in heights {
                at(x, y).ground = groundFactory.newGround(groundChar)
            }
        }
    }

    init(_ groundFactory: GroundFactory, _ lines: [String]) {
        self.groundFactory = groundFactory
        let rows = lines.map(Array.init)
        initMap(width: rows[0].count, height: rows.count)
        for x in widths {
            for y in heights {
                at(x, y).ground = groundFactory.newGround(rows[y][x])
            }
        }
    }

    private func initMap(width: Int, height: Int) {
        widths = NumberRange(0, width)
        heights = NumberRange(0, height)
        map = (0..<width).map { x in (0..<height).map { y in Location(map: self, x: x, y: y) } }

        for x in widths {
            for y in heights {
                let here = at(x, y)
                addExitFromHere(here, x, y - 1, "North", "8")
                addExitFromHere(here, x + 1, y - 1, "North-East", "9")
                addExitFromHere(here, x + 1, y, "East", "6")
                addExitFromHere(here, x + 1, y + 1, "South-East", "3")
                addExitFromHere(here, x, y + 1, "South", "2")
                addExitFromHere(here, x - 1, y + 1, "South-West", "1")
                addExitFromHere(here, x - 1, y, "West", "4")
                addExitFromHere(here, x - 1, y - 1, "North-West", "7")
            }
        }
    }

    private func addExitFromHere(_ here: Location, _ x: Int, _ y: Int, _ name: String, _ hotKey: String) {
        if widths.contains(x) && heights.contains(y) {
            here.addExit(Exit(name: name, destination: at(x, y), hotKey: hotKey))
        }
    }

    /// Sends a snapshot of this map to the display.
    func draw(_ display: Display, focus: Location?) {
        let rows = heights.map { y in String(widths.map { x in at(x, y).displayChar }) }
        display.show(MapFrame(rows: rows, focusX: focus?.x, focusY: focus?.y))
    }

    func at(_ x: Int, _ y: Int) -> Location { map[x][y] }

    func add(_ groundChar: Character, _ xs: NumberRange, _ ys: NumberRange) {
        for x in xs {
            for y in ys {
                at(x, y).ground = groundFactory.newGround(groundChar)
            }
        }
    }

    var xRange: NumberRange { widths }
    var yRange: NumberRange { heights }

    /// Called once per turn so that Locations and Items can experience the passage of time.
    func tick() {
        for actor in actorLocations where contains(actor) {
            guard let location = actorLocations.locationOf(actor) else { continue }
            for item in actor.inventory {  // a copy, in case the item wants to leave
                item.tick(at: location, carriedBy: actor)
            }
        }
        for y in heights {
            for x in widths {
                at(x, y).tick()
            }
        }
    }

    func getActorAt(_ location: Location) -> Actor? { actorLocations.getActorAt(location) }

    func addActor(_ actor: Actor, _ location: Location) { actorLocations.add(actor, location) }

    func removeActor(_ actor: Actor) { actorLocations.remove(actor) }

    func moveActor(_ actor: Actor, _ newLocation: Location) { actorLocations.move(actor, newLocation) }

    /// The location of the actor. Crashes if the actor is not in the world, like the Java version
    /// would with a NullPointerException.
    func locationOf(_ actor: Actor) -> Location {
        guard let location = actorLocations.locationOf(actor) else {
            fatalError("\(actor) is not in the world")
        }
        return location
    }

    func isAnActorAt(_ location: Location) -> Bool { actorLocations.isAnActorAt(location) }

    func contains(_ actor: Actor) -> Bool {
        actorLocations.locationOf(actor)?.map === self
    }
}
