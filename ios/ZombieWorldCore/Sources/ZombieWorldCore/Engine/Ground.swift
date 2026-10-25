/// Base class for the terrain of a Location. Port of `edu.monash.fit2099.engine.Ground`.
class Ground: Capable, Printable {
    private let capabilities = Capabilities()
    var displayChar: Character

    init(_ displayChar: Character) {
        self.displayChar = displayChar
    }

    /// Actions an adjacent actor can perform on this ground.
    func allowableActions(_ actor: Actor, _ location: Location, _ direction: String) -> Actions {
        Actions()
    }

    func canActorEnter(_ actor: Actor) -> Bool { true }

    /// Called once per turn so the ground can experience the passage of time.
    func tick(_ location: Location) {}

    func blocksThrownObjects() -> Bool { false }

    func hasCapability(_ capability: AnyHashable) -> Bool { capabilities.hasCapability(capability) }
    func addCapability(_ capability: AnyHashable) { capabilities.addCapability(capability) }
    func removeCapability(_ capability: AnyHashable) { capabilities.removeCapability(capability) }
}

protocol GroundFactory {
    func newGround(_ displayChar: Character) -> Ground
}

/// Creates Ground from map characters. The Java version used reflection; here each ground type
/// is registered with a constructor closure.
struct FancyGroundFactory: GroundFactory {
    private var makers: [Character: () -> Ground] = [:]

    init(_ groundTypes: (() -> Ground)...) {
        for make in groundTypes {
            makers[make().displayChar] = make
        }
    }

    func newGround(_ displayChar: Character) -> Ground {
        guard let make = makers[displayChar] else {
            fatalError("No ground registered for '\(displayChar)'")
        }
        return make()
    }
}
