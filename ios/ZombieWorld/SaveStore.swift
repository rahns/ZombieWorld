import Foundation
import ZombieWorldCore

/// A game in progress: replaying `choices` on a game started from `seed` restores it exactly.
struct SavedGame: Codable {
    var version: Int
    /// The UInt64 seed, as text so no JSON reader can round it.
    var seed: String
    /// Every key the player pressed, in order.
    var choices: String
}

/// Keeps the current game on disk. It's rewritten (atomically) after every choice, so even a
/// force quit loses nothing.
enum SaveStore {
    private static var url: URL {
        let folder = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask)[0]
        return folder.appendingPathComponent("savegame.json")
    }

    static func load() -> SavedGame? {
        guard let data = try? Data(contentsOf: url),
              let game = try? JSONDecoder().decode(SavedGame.self, from: data),
              game.version == ZombieWorldGame.saveFormatVersion,
              UInt64(game.seed) != nil else { return nil }
        return game
    }

    static func save(_ game: SavedGame) {
        do {
            try FileManager.default.createDirectory(at: url.deletingLastPathComponent(), withIntermediateDirectories: true)
            try JSONEncoder().encode(game).write(to: url, options: .atomic)
        } catch {
            // Saving is best effort: the game carries on regardless.
        }
    }

    static func delete() {
        try? FileManager.default.removeItem(at: url)
    }
}
