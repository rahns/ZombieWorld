final class Dirt: Ground {
    init() { super.init(".") }
}

final class Fence: Ground {
    init() { super.init("#") }

    override func canActorEnter(_ actor: Actor) -> Bool { false }

    override func blocksThrownObjects() -> Bool { true }
}

final class Tree: Ground {
    private var age = 0

    init() { super.init("+") }

    override func tick(_ location: Location) {
        super.tick(location)
        age += 1
        if age == 10 { displayChar = "t" }
        if age == 20 { displayChar = "T" }
    }
}

final class Door: Ground, Toggleable {
    private var isOpen = false

    init() { super.init("_") }

    override func allowableActions(_ actor: Actor, _ location: Location, _ direction: String) -> Actions {
        Actions(ToggleAction(self, isOpen))
    }

    func toggle() {
        isOpen.toggle()
        displayChar = isOpen ? "|" : "_"
    }

    override func canActorEnter(_ actor: Actor) -> Bool { isOpen }

    func switchToStateDescription(_ stateToSwitchTo: Bool) -> String {
        stateToSwitchTo ? "opens the door" : "closes the door"
    }
}

/// A crop that ripens over time and can be fertilised and harvested.
final class Crop: Ground {
    private var age = 0
    private(set) var beenFertilised = false
    private static let fertiliserAmount = 10

    init() { super.init("=") }

    override func tick(_ location: Location) {
        super.tick(location)
        age += 1
        if age == 10 { displayChar = ":" }
        if isRipe() { displayChar = "1" }
    }

    func isRipe() -> Bool { age >= 20 }

    func fertilise() {
        age += Crop.fertiliserAmount
        beenFertilised = true
    }
}

/// A shop that unlocks after a number of turns.
final class Shop: Ground, CustomStringConvertible {
    private let products: [Product]
    private let name: String
    private var age = 0
    private let shopActions = Actions()
    private let unlockAge: Int

    init(_ name: String, _ products: [Product], unlockAge: Int) {
        self.products = products
        self.name = name
        self.unlockAge = unlockAge
        super.init("[")
    }

    override func canActorEnter(_ actor: Actor) -> Bool { false }

    override func allowableActions(_ actor: Actor, _ location: Location, _ direction: String) -> Actions {
        shopActions
    }

    override func tick(_ location: Location) {
        super.tick(location)
        if age < unlockAge {
            shopActions.clear()
            shopActions.add(DoNothingCustomMessageAction("stares at the closed \(name) store. This shop will unlock in \(unlockAge - age) turn(s)"))
            age += 1
        } else if age == unlockAge {
            shopActions.clear()
            shopActions.add(ShopAction(self))
            displayChar = "]"
            age += 1
        }
    }

    func getProducts() -> [Product] { products }

    var description: String { name }
}
