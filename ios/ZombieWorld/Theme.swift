import SwiftUI

/// Colours and descriptions for the characters drawn on the map.
enum Theme {
    static let background = Color(red: 0.06, green: 0.07, blue: 0.06)
    static let panel = Color(red: 0.11, green: 0.12, blue: 0.11)
    static let accent = Color(red: 0.55, green: 0.85, blue: 0.35)

    static func color(for symbol: Character) -> Color {
        switch symbol {
        case "@": return .yellow
        case "Z": return Color(red: 0.45, green: 0.95, blue: 0.3)
        case "M": return Color(red: 0.85, green: 0.4, blue: 1.0)
        case "H": return Color(red: 0.45, green: 0.8, blue: 1.0)
        case "f": return Color(red: 0.95, green: 0.7, blue: 0.4)
        case "#": return Color(white: 0.55)
        case "_", "|": return Color(red: 0.75, green: 0.5, blue: 0.3)
        case "+", "t", "T": return Color(red: 0.2, green: 0.6, blue: 0.25)
        case "=", ":", "1": return Color(red: 0.8, green: 0.8, blue: 0.3)
        case "$": return Color(red: 1.0, green: 0.84, blue: 0.0)
        case "~": return Color(red: 1.0, green: 0.55, blue: 0.55)
        case "%": return Color(red: 0.8, green: 0.25, blue: 0.25)
        case "a": return Color(red: 0.9, green: 0.75, blue: 0.5)
        case "S", "R", ")", "]", "c", "m", "!", "L": return Color(red: 0.95, green: 0.95, blue: 0.95)
        case "x", "X": return Color(red: 0.4, green: 0.9, blue: 0.9)
        case "*": return Color(red: 1.0, green: 0.45, blue: 0.2)
        case "[": return Color(white: 0.5)
        case ".": return Color(white: 0.22)
        default: return Color(white: 0.85)
        }
    }

    static let legend: [LegendEntry] = [
        LegendEntry(symbol: "@", meaning: "You"),
        LegendEntry(symbol: "H", meaning: "Human"),
        LegendEntry(symbol: "f", meaning: "Farmer"),
        LegendEntry(symbol: "Z", meaning: "Zombie"),
        LegendEntry(symbol: "M", meaning: "Mambo Marie, the voodoo priestess"),
        LegendEntry(symbol: "#", meaning: "Fence"),
        LegendEntry(symbol: "_ |", meaning: "Door (closed / open)"),
        LegendEntry(symbol: "+ t T", meaning: "Tree (growing)"),
        LegendEntry(symbol: "= : 1", meaning: "Crop (sown / growing / ripe)"),
        LegendEntry(symbol: "~", meaning: "Food"),
        LegendEntry(symbol: "%", meaning: "Corpse"),
        LegendEntry(symbol: "$", meaning: "Coin"),
        LegendEntry(symbol: "a", meaning: "Ammunition cartridge"),
        LegendEntry(symbol: ")", meaning: "Plank"),
        LegendEntry(symbol: "]", meaning: "Hoe, or an open shop"),
        LegendEntry(symbol: "[", meaning: "Shop (closed)"),
        LegendEntry(symbol: "! L", meaning: "Zombie arm / leg"),
        LegendEntry(symbol: "c m", meaning: "Zombie club / mace"),
        LegendEntry(symbol: "S R", meaning: "Shotgun / sniper rifle"),
        LegendEntry(symbol: "x X", meaning: "Turret (packed / set up)"),
        LegendEntry(symbol: "*", meaning: "Train"),
    ]
}

struct LegendEntry: Identifiable {
    let symbol: String
    let meaning: String

    var id: String { symbol }
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
