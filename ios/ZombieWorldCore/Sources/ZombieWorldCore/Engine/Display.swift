/// A square on a particular map.
public struct TilePoint: Sendable, Hashable {
    /// Which map: 0 is the compound, 1 is the town.
    public let map: Int
    public let x: Int
    public let y: Int

    public init(map: Int, x: Int, y: Int) {
        self.map = map
        self.x = x
        self.y = y
    }
}

/// An actor as drawn on the map.
public struct ActorSnapshot: Sendable, Equatable, Identifiable {
    /// Stable for the whole game, so frontends can animate actors between frames.
    public let id: Int
    public let symbol: Character
    public let name: String
    public let x: Int
    public let y: Int
    public let hitPoints: Int
    public let maxHitPoints: Int
}

/// A snapshot of the map the player is on.
public struct MapFrame: Sendable, Equatable {
    /// One string per map row, top to bottom; one character per square (the topmost thing there).
    public let rows: [String]
    /// The player's position, if they are on this map.
    public let focusX: Int?
    public let focusY: Int?
    /// Which map this is: 0 is the compound, 1 is the town.
    public let mapIndex: Int
    /// The ground character of every square, laid out like `rows`.
    public let ground: [String]
    /// The topmost item on every square, or a space, laid out like `rows`.
    public let items: [String]
    public let actors: [ActorSnapshot]
    /// Ids of actors killed since the previous frame. Actors can also leave a map without dying
    /// (riding the train, Mambo Marie returning to her void).
    public let deaths: [Int]

    public init(rows: [String], focusX: Int?, focusY: Int?, mapIndex: Int = 0,
                ground: [String] = [], items: [String] = [], actors: [ActorSnapshot] = [], deaths: [Int] = []) {
        self.rows = rows
        self.focusX = focusX
        self.focusY = focusY
        self.mapIndex = mapIndex
        self.ground = ground
        self.items = items
        self.actors = actors
        self.deaths = deaths
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

/// What kind of thing a menu option does, so frontends can pick icons and handle taps.
public enum ActionKind: String, Sendable {
    case move, travel, attack, shoot, aim, pickUp, drop, eat, craft, harvest, collect
    case door, shop, buy, gun, reload, setUp, wait, quit, other
}

/// One choice in a menu: press `key` to pick it.
public struct MenuOption: Sendable, Hashable, Identifiable {
    public let key: Character
    public let label: String
    public let kind: ActionKind
    /// The square this option acts on, if any (where you move to, who you attack...).
    public let target: TilePoint?

    public init(key: Character, label: String, kind: ActionKind = .other, target: TilePoint? = nil) {
        self.key = key
        self.label = label
        self.kind = kind
        self.target = target
    }

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

    /// The same prompt with earlier messages in front of its log.
    public func prepending(log earlier: [String]) -> Prompt {
        Prompt(log: earlier + log, frame: frame, status: status, options: options, footer: footer, isSubmenu: isSubmenu)
    }
}

/// The final state of the game.
public struct GameResult: Sendable {
    public let log: [String]
    public let frame: MapFrame?
    public let message: String

    /// The same result with earlier messages in front of its log.
    public func prepending(log earlier: [String]) -> GameResult {
        GameResult(log: earlier + log, frame: frame, message: message)
    }
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
    /// Keeps every actor that has been drawn alive, so object identities (and ids) are never reused.
    private var actorIDs: [ObjectIdentifier: (actor: Actor, id: Int)] = [:]

    init(frontend: GameFrontend) {
        self.frontend = frontend
    }

    private var deaths: [Int] = []

    /// Records that an actor was killed, so frontends can tell dying from leaving the map.
    func noteDeath(_ actor: Actor) {
        deaths.append(id(of: actor))
    }

    /// The deaths since the last call.
    func takeDeaths() -> [Int] {
        defer { deaths.removeAll() }
        return deaths
    }

    /// A stable id for an actor, for the whole game.
    func id(of actor: Actor) -> Int {
        if let entry = actorIDs[ObjectIdentifier(actor)] { return entry.id }
        let id = actorIDs.count + 1
        actorIDs[ObjectIdentifier(actor)] = (actor, id)
        return id
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
