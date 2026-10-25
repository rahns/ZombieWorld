/// Thrown when a recorded game can't be replayed (the game changed since it was saved).
public struct ReplayDivergedError: Error {}

/// Restores a saved game: answers the engine's prompts with recorded choices, without showing
/// anything, then hands over to the real frontend once the recording runs out.
public final class ReplayingFrontend: GameFrontend, @unchecked Sendable {
    private let choices: [Character]
    private let live: GameFrontend
    private let onCaughtUp: @Sendable (_ turns: Int) async -> Void
    private var index = 0
    private var turns = 0
    private var log: [String] = []
    private var caughtUp = false
    private static let keptLogLines = 300

    /// - Parameters:
    ///   - choices: the keys the player pressed, in order.
    ///   - live: the frontend to use once the replay is done.
    ///   - onCaughtUp: called once, before the first live prompt, with the number of turns replayed.
    public init(choices: [Character], live: GameFrontend, onCaughtUp: @escaping @Sendable (_ turns: Int) async -> Void) {
        self.choices = choices
        self.live = live
        self.onCaughtUp = onCaughtUp
    }

    public func choose(_ prompt: Prompt) async throws -> Character {
        guard index < choices.count else {
            return try await live.choose(catchUp(prompt))
        }
        log.append(contentsOf: prompt.log)
        if log.count > Self.keptLogLines { log.removeFirst(log.count - Self.keptLogLines) }
        if !prompt.isSubmenu { turns += 1 }

        let key = choices[index]
        index += 1
        guard prompt.options.contains(where: { $0.key == key }) else { throw ReplayDivergedError() }
        return key
    }

    public func gameEnded(_ result: GameResult) async {
        if caughtUp {
            await live.gameEnded(result)
        } else {
            caughtUp = true
            await onCaughtUp(turns)
            await live.gameEnded(result.prepending(log: log))
        }
    }

    /// The first live prompt carries the messages from the end of the replay.
    private func catchUp(_ prompt: Prompt) async -> Prompt {
        guard !caughtUp else { return prompt }
        caughtUp = true
        await onCaughtUp(turns)
        return prompt.prepending(log: log)
    }
}
