/// A snapshot of the map the player is on.
public struct MapFrame: Sendable, Equatable {
    /// One string per map row, top to bottom; one character per square.
    public let rows: [String]
    /// The player's position, if they are on this map.
    public let focusX: Int?
    public let focusY: Int?

    public init(rows: [String], focusX: Int?, focusY: Int?) {
        self.rows = rows
        self.focusX = focusX
        self.focusY = focusY
    }

    public var width: Int { rows.first?.count ?? 0 }
    public var height: Int { rows.count }
}

/// The player's vital statistics.
public struct PlayerStatus: Sendable, Equatable {
    public let name: String
    public let hitPoints: Int
    public let maxHitPoints: Int
    public let coins: Int
}

/// One choice in a menu: press `key` to pick it.
public struct MenuOption: Sendable, Hashable, Identifiable {
    public let key: Character
    public let label: String

    public var id: Character { key }
}

/// Everything a frontend needs to ask the player for their next choice.
public struct Prompt: Sendable {
    /// Messages printed since the previous prompt.
    public let log: [String]
    public let frame: MapFrame?
    public let status: PlayerStatus?
    public let options: [MenuOption]
    /// Extra information shown under a sub-menu (e.g. remaining ammo).
    public let footer: String?
    /// True for nested menus (guns, shop, targets), false for the main turn menu.
    public let isSubmenu: Bool
}

/// The final state of the game.
public struct GameResult: Sendable {
    public let log: [String]
    public let frame: MapFrame?
    public let message: String
}

/// A user interface for the game: a terminal, a SwiftUI view...
public protocol GameFrontend: AnyObject, Sendable {
    /// Shows the prompt and returns the key of the option the player picked.
    /// Throw (e.g. `CancellationError`) to abandon the game.
    func choose(_ prompt: Prompt) async throws -> Character

    /// Called once when the game is over.
    func gameEnded(_ result: GameResult) async
}

/// Collects game output and forwards it to a frontend whenever input is needed.
/// Replaces the console-based `edu.monash.fit2099.engine.Display`.
final class Display {
    let frontend: GameFrontend
    private var pending: [String] = []
    private(set) var frame: MapFrame?
    var status: PlayerStatus?

    init(frontend: GameFrontend) {
        self.frontend = frontend
    }

    func println(_ text: String) {
        pending.append(contentsOf: text.split(separator: "\n", omittingEmptySubsequences: false).map(String.init))
    }

    func show(_ frame: MapFrame) {
        self.frame = frame
    }

    /// Presents the options and waits until the player picks one of them.
    func readChoice(_ options: [MenuOption], footer: String? = nil, isSubmenu: Bool = false) async throws -> Character {
        let log = pending
        pending.removeAll()
        let prompt = Prompt(log: log, frame: frame, status: status, options: options, footer: footer, isSubmenu: isSubmenu)
        return try await frontend.choose(prompt)
    }

    func finish(_ message: String) async {
        println(message)
        let log = pending
        pending.removeAll()
        await frontend.gameEnded(GameResult(log: log, frame: frame, message: message))
    }
}
