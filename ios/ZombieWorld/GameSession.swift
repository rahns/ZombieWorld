import SwiftUI
import ZombieWorldCore

/// One line of the message log.
struct LogEntry: Identifiable, Equatable {
    let id: Int
    let turn: Int
    let text: String
}

/// Runs the game engine in the background and exposes its state to SwiftUI.
/// The engine suspends whenever it needs a decision from the player; `choose(_:)` resumes it.
@MainActor
final class GameSession: ObservableObject {
    @Published private(set) var frame: MapFrame?
    @Published private(set) var status: PlayerStatus?
    @Published private(set) var options: [MenuOption] = []
    @Published private(set) var footer: String?
    @Published private(set) var isSubmenu = false
    @Published private(set) var log: [LogEntry] = []
    @Published private(set) var turn = 0
    @Published private(set) var result: GameResult?
    /// True while a saved game is being replayed in the background.
    @Published private(set) var isRestoring = false
    /// A one-off message for the player, such as a save that couldn't be restored.
    @Published private(set) var notice: String?
    private(set) var hasStarted = false

    private static let maxLogEntries = 400
    private var nextLogID = 0
    /// Increases with every new game.
    private(set) var generation = 0
    private var continuation: CheckedContinuation<Character, Error>?
    private var task: Task<Void, Never>?
    private var saved: SavedGame?

    var isWaitingForPlayer: Bool { continuation != nil }

    /// Continues the saved game if there is one, otherwise starts a new game.
    func start() {
        if let saved = SaveStore.load(), let seed = UInt64(saved.seed) {
            run(seed: seed, saved: saved)
        } else {
            newGame()
        }
    }

    /// Abandons any game in progress and starts a new one.
    func newGame() {
        let seed = UInt64.random(in: .min ... .max)
        let game = SavedGame(version: ZombieWorldGame.saveFormatVersion, seed: String(seed), choices: "")
        SaveStore.save(game)
        run(seed: seed, saved: game)
    }

    private func run(seed: UInt64, saved game: SavedGame) {
        hasStarted = true
        saved = game
        generation += 1
        continuation?.resume(throwing: CancellationError())
        continuation = nil
        task?.cancel()

        frame = nil
        status = nil
        options = []
        footer = nil
        isSubmenu = false
        log = []
        turn = 0
        result = nil
        isRestoring = !game.choices.isEmpty

        let generation = generation
        let bridge = FrontendBridge(session: self, generation: generation)
        let frontend: GameFrontend
        if game.choices.isEmpty {
            frontend = bridge
        } else {
            frontend = ReplayingFrontend(choices: Array(game.choices), live: bridge) { [weak self] turns in
                await self?.restored(turns: turns, generation: generation)
            }
        }
        task = Task.detached(priority: .userInitiated) { [weak self] in
            do {
                try await ZombieWorldGame.run(frontend: frontend, seed: seed)
            } catch is ReplayDivergedError {
                await self?.restoreFailed(generation: generation)
            } catch {
                // Cancelled because another game started.
            }
        }
    }

    private func restored(turns: Int, generation: Int) {
        guard generation == self.generation else { return }
        turn = turns
        isRestoring = false
    }

    private func restoreFailed(generation: Int) {
        guard generation == self.generation else { return }
        newGame()
        notice = "Your saved game couldn't be restored, so a new game has started."
    }

    func clearNotice() {
        notice = nil
    }

    /// Picks a menu option and lets the engine carry on.
    func choose(_ key: Character) {
        guard let continuation, options.contains(where: { $0.key == key }) else { return }
        self.continuation = nil
        if var game = saved {
            game.choices.append(key)
            saved = game
            SaveStore.save(game)
        }
        options = []
        continuation.resume(returning: key)
    }

    fileprivate func present(_ prompt: Prompt, generation: Int) async throws -> Character {
        guard generation == self.generation else { throw CancellationError() }
        if !prompt.isSubmenu { turn += 1 }
        appendLog(prompt.log)
        if let frame = prompt.frame { self.frame = frame }
        status = prompt.status
        footer = prompt.footer
        isSubmenu = prompt.isSubmenu
        options = prompt.options
        return try await withCheckedThrowingContinuation { continuation in
            self.continuation = continuation
        }
    }

    fileprivate func finish(_ result: GameResult, generation: Int) {
        guard generation == self.generation else { return }
        appendLog(result.log)
        if let frame = result.frame { self.frame = frame }
        options = []
        self.result = result
        isRestoring = false
        saved = nil
        SaveStore.delete()
    }

    private func appendLog(_ lines: [String]) {
        for line in lines where !line.trimmingCharacters(in: .whitespaces).isEmpty {
            log.append(LogEntry(id: nextLogID, turn: turn, text: line))
            nextLogID += 1
        }
        if log.count > Self.maxLogEntries {
            log.removeFirst(log.count - Self.maxLogEntries)
        }
    }
}

/// Connects one game run to the session. A new game gets a new bridge, so prompts from an
/// abandoned game are ignored.
@MainActor
private final class FrontendBridge: GameFrontend {
    private weak var session: GameSession?
    private let generation: Int

    init(session: GameSession, generation: Int) {
        self.session = session
        self.generation = generation
    }

    func choose(_ prompt: Prompt) async throws -> Character {
        guard let session else { throw CancellationError() }
        return try await session.present(prompt, generation: generation)
    }

    func gameEnded(_ result: GameResult) async {
        session?.finish(result, generation: generation)
    }
}
