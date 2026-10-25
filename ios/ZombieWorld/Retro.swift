import CoreText
import SwiftUI
import UIKit
import ZombieWorldCore

/// The retro look: palette, pixel font and text boxes.
enum Retro {
    static let fontName = "PressStart2P-Regular"

    // Colours from the sprite palette.
    static let night = UIColor(red: 0.15, green: 0.17, blue: 0.27, alpha: 1)
    static let danger = UIColor(red: 0.91, green: 0.27, blue: 0.22, alpha: 1)
    static let gold = UIColor(red: 0.97, green: 0.77, blue: 0.29, alpha: 1)
    static let heal = UIColor(red: 0.39, green: 0.85, blue: 0.42, alpha: 1)

    static let ink = Color(red: 0.15, green: 0.17, blue: 0.27)
    static let paper = Color(red: 0.97, green: 0.95, blue: 0.87)
    static let border = Color(red: 0.25, green: 0.15, blue: 0.19)
    static let panel = Color(red: 0.11, green: 0.12, blue: 0.2)
    static let button = Color(red: 0.24, green: 0.27, blue: 0.4)
    static let buttonPressed = Color(red: 0.32, green: 0.36, blue: 0.52)
    static let red = Color(uiColor: danger)
    static let yellow = Color(uiColor: gold)
    static let green = Color(uiColor: heal)

    /// Registers the bundled pixel font. Call once at launch.
    static func registerFont() {
        let url = Bundle.main.url(forResource: "PressStart2P", withExtension: "ttf")
            ?? Bundle.main.url(forResource: "PressStart2P", withExtension: "ttf", subdirectory: "Fonts")
        guard let url else { return }
        CTFontManagerRegisterFontsForURL(url as CFURL, .process, nil)
    }

    static func icon(for kind: ActionKind) -> String {
        switch kind {
        case .move: return "arrow.up.and.down.and.arrow.left.and.right"
        case .travel: return "tram.fill"
        case .attack: return "burst.fill"
        case .shoot: return "scope"
        case .aim: return "target"
        case .pickUp: return "hand.raised.fill"
        case .drop: return "arrow.down.to.line"
        case .eat: return "fork.knife"
        case .craft: return "hammer.fill"
        case .harvest: return "leaf.fill"
        case .collect: return "dollarsign.circle.fill"
        case .door: return "door.left.hand.open"
        case .shop: return "cart.fill"
        case .buy: return "bag.fill"
        case .gun: return "scope"
        case .reload: return "arrow.triangle.2.circlepath"
        case .setUp: return "wrench.and.screwdriver.fill"
        case .wait: return "hourglass"
        case .quit: return "flag.fill"
        case .other: return "ellipsis.circle.fill"
        }
    }

    static func tint(for kind: ActionKind) -> Color {
        switch kind {
        case .attack, .shoot, .aim, .gun, .reload: return red
        case .pickUp, .collect, .buy, .shop: return yellow
        case .eat, .harvest, .craft, .setUp: return green
        default: return Color(white: 0.85)
        }
    }

    /// Sprites and descriptions for the legend and for inspecting squares.
    static let characters: [LegendEntry] = [
        LegendEntry(sprite: "actor_player", name: "You", symbol: "@"),
        LegendEntry(sprite: "actor_human_0", name: "Human", symbol: "H"),
        LegendEntry(sprite: "actor_farmer", name: "Farmer", symbol: "f"),
        LegendEntry(sprite: "actor_zombie_0", name: "Zombie", symbol: "Z"),
        LegendEntry(sprite: "actor_mambo", name: "Mambo Marie", symbol: "M"),
    ]

    static let items: [LegendEntry] = [
        LegendEntry(sprite: "item_food", name: "Food", symbol: "~"),
        LegendEntry(sprite: "item_coin", name: "Coin", symbol: "$"),
        LegendEntry(sprite: "item_ammo", name: "Ammunition", symbol: "a"),
        LegendEntry(sprite: "item_corpse", name: "Corpse", symbol: "%"),
        LegendEntry(sprite: "item_plank", name: "Plank", symbol: ")"),
        LegendEntry(sprite: "item_hoe", name: "Hoe", symbol: "]"),
        LegendEntry(sprite: "item_arm", name: "Zombie arm", symbol: "!"),
        LegendEntry(sprite: "item_leg", name: "Zombie leg", symbol: "L"),
        LegendEntry(sprite: "item_club", name: "Zombie club", symbol: "c"),
        LegendEntry(sprite: "item_mace", name: "Zombie mace", symbol: "m"),
        LegendEntry(sprite: "item_shotgun", name: "Shotgun", symbol: "S"),
        LegendEntry(sprite: "item_sniper", name: "Sniper rifle", symbol: "R"),
        LegendEntry(sprite: "item_turret_packed", name: "Turret (packed)", symbol: "x"),
        LegendEntry(sprite: "item_turret", name: "Turret (set up)", symbol: "X"),
        LegendEntry(sprite: "item_train", name: "Train", symbol: "*"),
    ]

    static let grounds: [LegendEntry] = [
        LegendEntry(sprite: "ground_grass", name: "Grass", symbol: "."),
        LegendEntry(sprite: "ground_wall", name: "Wall", symbol: "#"),
        LegendEntry(sprite: "ground_door_closed", name: "Closed door", symbol: "_"),
        LegendEntry(sprite: "ground_door_open", name: "Open door", symbol: "|"),
        LegendEntry(sprite: "deco_tree_sapling", name: "Sapling", symbol: "+"),
        LegendEntry(sprite: "deco_tree_young", name: "Young tree", symbol: "t"),
        LegendEntry(sprite: "deco_tree_big", name: "Tree", symbol: "T"),
        LegendEntry(sprite: "ground_soil", name: "Sown crop", symbol: "="),
        LegendEntry(sprite: "deco_crop_sprout", name: "Growing crop", symbol: ":"),
        LegendEntry(sprite: "deco_crop_ripe", name: "Ripe crop", symbol: "1"),
        LegendEntry(sprite: "ground_shop_closed", name: "Shop (closed)", symbol: "["),
        LegendEntry(sprite: "ground_shop_open", name: "Shop (open)", symbol: "]"),
    ]

    static func itemName(_ symbol: Character) -> String? { items.first { $0.symbol == symbol }?.name }
    static func groundName(_ symbol: Character) -> String? { grounds.first { $0.symbol == symbol }?.name }
}

struct LegendEntry: Identifiable {
    let sprite: String
    let name: String
    let symbol: Character

    var id: String { sprite + name }
}

extension Font {
    static func pixel(_ size: CGFloat) -> Font {
        .custom(Retro.fontName, fixedSize: size)
    }
}

/// A Pokémon-style text box: cream paper with a thick dark border.
struct RetroBox: ViewModifier {
    var padding: CGFloat = 10

    func body(content: Content) -> some View {
        content
            .padding(padding)
            .foregroundStyle(Retro.ink)
            .background(Retro.paper, in: RoundedRectangle(cornerRadius: 6))
            .overlay(RoundedRectangle(cornerRadius: 6).strokeBorder(Retro.border, lineWidth: 3))
            .overlay(RoundedRectangle(cornerRadius: 3).strokeBorder(Retro.border.opacity(0.35), lineWidth: 1).padding(5))
    }
}

extension View {
    func retroBox(padding: CGFloat = 10) -> some View {
        modifier(RetroBox(padding: padding))
    }
}

/// A sprite from the asset catalog, scaled up without blurring.
struct SpriteImage: View {
    let name: String
    var size: CGFloat = 24

    var body: some View {
        Image(name)
            .interpolation(.none)
            .resizable()
            .frame(width: size, height: size)
    }
}

/// A chunky button like a handheld console's.
struct ConsoleButtonStyle: ButtonStyle {
    var tint: Color = Retro.button

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .background(configuration.isPressed ? Retro.buttonPressed : tint, in: RoundedRectangle(cornerRadius: 8))
            .overlay(RoundedRectangle(cornerRadius: 8).strokeBorder(Color.black.opacity(0.5), lineWidth: 2))
            .offset(y: configuration.isPressed ? 1 : 0)
    }
}

extension MenuOption {
    /// The menu text without the redundant "Player " prefix.
    var shortLabel: String {
        let prefix = "Player "
        guard label.hasPrefix(prefix) else { return label }
        let rest = label.dropFirst(prefix.count)
        return rest.prefix(1).uppercased() + String(rest.dropFirst())
    }
}
