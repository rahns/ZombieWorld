import ZombieWorldCore

#if canImport(Glibc)
import Glibc
#elseif canImport(Darwin)
import Darwin
#endif

/// Plays ZombieWorld in the terminal, like the original Java version.
final class TerminalFrontend: GameFrontend, @unchecked Sendable {
    func choose(_ prompt: Prompt) async throws -> Character {
        prompt.log.forEach { print($0) }
        if !prompt.isSubmenu {
            prompt.frame?.rows.forEach { print($0) }
            if let status = prompt.status {
                print("Current player health: \(status.hitPoints)/\(status.maxHitPoints)")
                print("Current player wealth: \(status.coins) coin\(status.coins != 1 ? "s" : "")")
            }
        }
        for option in prompt.options {
            print("\(option.key): \(option.label)")
        }
        if let footer = prompt.footer { print(footer) }

        while true {
            guard let line = readLine() else { throw CancellationError() }
            if let key = line.first(where: { !$0.isWhitespace }), prompt.options.contains(where: { $0.key == key }) {
                return key
            }
        }
    }

    func gameEnded(_ result: GameResult) async {
        result.log.forEach { print($0) }
    }
}

try? await ZombieWorldGame.run(frontend: TerminalFrontend())
