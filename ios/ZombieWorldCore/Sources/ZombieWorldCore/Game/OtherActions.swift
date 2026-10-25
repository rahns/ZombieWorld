/// Does nothing, with a custom description.
final class DoNothingCustomMessageAction: DoNothingAction {
    private let message: String

    init(_ message: String) {
        self.message = message
    }

    override func menuDescription(_ actor: Actor) -> String { "\(actor) \(message)" }

    override var hotkey: String? { "-" }
}

final class ToggleAction: Action {
    private let object: Toggleable
    private let currentState: Bool

    init(_ object: Toggleable, _ currentState: Bool) {
        self.object = object
        self.currentState = currentState
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        object.toggle()
        return menuDescription(actor)
    }

    override func menuDescription(_ actor: Actor) -> String {
        "\(actor) \(object.switchToStateDescription(!currentState))"
    }
}

final class CraftAction: Action {
    private let item: Item
    private let newItem: Item

    init<T: Item & CraftableItem>(_ item: T) {
        self.item = item
        self.newItem = item.craftsInto()
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        actor.addItemToInventory(newItem)
        let here = map.locationOf(actor)
        if here.containsItem(item) {
            here.removeItem(item)
        } else {
            actor.removeItemFromInventory(item)
        }
        return "\(actor) crafted \(item) into \(newItem)."
    }

    override func menuDescription(_ actor: Actor) -> String { "Craft \(item) into \(newItem)" }
}

final class EatAction: Action {
    private let food: Food

    init(_ food: Food) {
        self.food = food
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        let here = map.locationOf(actor)
        if here.containsItem(food) {
            here.removeItem(food)
        } else {
            actor.removeItemFromInventory(food)
        }
        actor.heal(food.healAmount)
        return "\(actor) ate some food and recovered \(food.healAmount) hit points"
    }

    override func menuDescription(_ actor: Actor) -> String { "\(actor) eats food" }
}

final class SowAction: Action {
    private let destination: Location

    init(_ destination: Location) {
        self.destination = destination
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        destination.ground = Crop()
        return "\(actor) sowed a crop"
    }

    override func menuDescription(_ actor: Actor) -> String { "Sow crop" }
}

final class FertiliseAction: Action {
    private let target: Location

    init(_ target: Location) {
        self.target = target
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        guard let crop = target.ground as? Crop else { return nil }
        crop.fertilise()
        return "\(actor) fertilised crop"
    }

    override func menuDescription(_ actor: Actor) -> String { "Fertilise crop" }
}

/// Turns a ripe crop back into dirt.
class HarvestAction: Action {
    let target: Location

    init(_ destination: Location) {
        self.target = destination
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        target.ground = Dirt()
        return "\(actor) harvested a crop"
    }

    override func menuDescription(_ actor: Actor) -> String { "Harvest Crop" }
}

/// Humans (and the player) put the harvested food straight into their inventory.
final class HumanHarvestAction: HarvestAction {
    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        let message = super.execute(actor, map)
        actor.addItemToInventory(Food())
        return message
    }
}

/// Farmers leave the harvested food on the ground for others.
final class FarmerHarvestAction: HarvestAction {
    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        let message = super.execute(actor, map)
        target.addItem(Food())
        return message
    }
}

final class CollectCoinsAction: Action {
    private let coins: [Coin]

    init(_ coins: [Coin]) {
        self.coins = coins
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        guard let wallet = actor as? Wallet else { return nil }
        for coin in coins {
            wallet.addCoinToWallet(coin)
            map.locationOf(actor).removeItem(coin)
        }
        return menuDescription(actor)
    }

    override func menuDescription(_ actor: Actor) -> String {
        "\(actor) collects \(coins.count) coin\(coins.count != 1 ? "s" : "")"
    }
}

final class BuyAction: Action {
    private let product: Product

    init(_ product: Product) {
        self.product = product
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? {
        guard let wallet = actor as? Wallet, wallet.wealth >= product.cost else { return nil }
        wallet.spendCoins(product.cost)
        actor.addItemToInventory(product.makeItem())
        return menuDescription(actor)
    }

    override func menuDescription(_ actor: Actor) -> String {
        "\(actor) buys a \(product.item) for \(product.cost) coin\(product.cost != 1 ? "s" : "")"
    }
}

/// Opens a shop's product menu.
final class ShopAction: Action, MenuAction {
    private let shop: Shop

    init(_ shop: Shop) {
        self.shop = shop
    }

    override func execute(_ actor: Actor, _ map: GameMap) -> String? { nil }

    override func menuDescription(_ actor: Actor) -> String { "Shop at \(shop)" }

    func getAction(_ actor: Actor, _ map: GameMap, _ display: Display) async throws -> Action {
        guard let wallet = actor as? Wallet else { return DoNothingAction() }
        let sub = SubMenu()
        var unaffordable: [String] = []
        for product in shop.getProducts() {
            if wallet.wealth < product.cost {
                unaffordable.append("\(product.item) - Can't afford, costs \(product.cost) coins")
                continue
            }
            sub.addActionToMenu(BuyAction(product), actor)
        }
        sub.addActionToMenu(DoNothingCustomMessageAction("leaves the shop without buying anything"), actor)
        var footer = "\(shop) - you have \(wallet.wealth) coin\(wallet.wealth != 1 ? "s" : "")"
        if !unaffordable.isEmpty {
            footer += "\n" + unaffordable.joined(separator: "\n")
        }
        sub.setFooter(footer)
        return try await sub.readInput(display)
    }
}
