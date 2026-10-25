import SpriteKit
import ZombieWorldCore

/// Maps the engine's map characters to sprite names in the asset catalog.
enum Sprites {
    private static var cache: [String: SKTexture] = [:]

    /// A pixel-perfect texture for a sprite. Only use from the main thread.
    static func texture(_ name: String) -> SKTexture {
        if let texture = cache[name] { return texture }
        let texture = SKTexture(imageNamed: name)
        texture.filteringMode = .nearest
        cache[name] = texture
        return texture
    }

    /// The tile for a ground character, plus anything drawn on top of it (trees, crops).
    static func ground(_ symbol: Character, x: Int, y: Int) -> (base: String, decoration: String?) {
        switch symbol {
        case "#": return ("ground_wall", nil)
        case "_": return ("ground_door_closed", nil)
        case "|": return ("ground_door_open", nil)
        case "+": return (grass(x, y), "deco_tree_sapling")
        case "t": return (grass(x, y), "deco_tree_young")
        case "T": return (grass(x, y), "deco_tree_big")
        case "=": return ("ground_soil", nil)
        case ":": return ("ground_soil", "deco_crop_sprout")
        case "1": return ("ground_soil", "deco_crop_ripe")
        case "[": return ("ground_shop_closed", nil)
        case "]": return ("ground_shop_open", nil)
        default: return (grass(x, y), nil)
        }
    }

    /// Mostly plain grass, with the odd tuft or flower, always the same for a given square.
    private static func grass(_ x: Int, _ y: Int) -> String {
        var h = UInt32(truncatingIfNeeded: x &* 73_856_093 ^ y &* 19_349_663)
        h ^= h >> 13
        h = h &* 0x5BD1_E995
        h ^= h >> 15
        switch h % 100 {
        case 0..<6: return "ground_grass_flowers"
        case 6..<20: return "ground_grass_tufts"
        default: return "ground_grass"
        }
    }

    static func item(_ symbol: Character) -> String? {
        switch symbol {
        case "~": return "item_food"
        case "%": return "item_corpse"
        case "$": return "item_coin"
        case "a": return "item_ammo"
        case ")": return "item_plank"
        case "]": return "item_hoe"
        case "!": return "item_arm"
        case "L": return "item_leg"
        case "c": return "item_club"
        case "m": return "item_mace"
        case "S": return "item_shotgun"
        case "R": return "item_sniper"
        case "x": return "item_turret_packed"
        case "X": return "item_turret"
        case "*": return "item_train"
        default: return nil
        }
    }

    static func actor(_ actor: ActorSnapshot) -> String {
        switch actor.symbol {
        case "@": return "actor_player"
        case "f": return "actor_farmer"
        case "Z": return "actor_zombie_\(actor.id % 2)"
        case "M": return "actor_mambo"
        default: return "actor_human_\(actor.id % 7)"
        }
    }
}
