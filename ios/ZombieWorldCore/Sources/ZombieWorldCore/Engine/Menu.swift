/// The main turn menu. Port of `edu.monash.fit2099.engine.Menu`.
class Menu {
    func showMenu(_ actor: Actor, _ actions: Actions, _ display: Display, map: GameMap? = nil) async throws -> Action {
        var freeChars = Array("abcdefghijklmnopqrstuvwxyz")
        var keyToAction: [Character: Action] = [:]
        var options: [MenuOption] = []

        // Actions with hotkeys first, otherwise keeping their order.
        let sortedActions = actions.sorted { a, b in a.hotkey != nil && b.hotkey == nil }
        for action in sortedActions {
            let c: Character
            if let hotKey = action.hotkey, let first = hotKey.first {
                c = first
            } else {
                guard let free = freeChars.first else { break }  // we've run out of characters to pick from
                c = free
            }
            freeChars.removeAll { $0 == c }
            // A later action with the same key wins, as in the Java HashMap; hide the unreachable one.
            options.removeAll { $0.key == c }
            keyToAction[c] = action
            options.append(action.menuOption(key: c, actor: actor, map: map))
        }

        while true {
            let key = try await display.readChoice(options)
            if let action = keyToAction[key] { return action }
        }
    }
}

/// A nested menu. Port of the game's `SubMenu`.
final class SubMenu {
    private var freeChars: [Character]?
    private var keyToAction: [Character: Action] = [:]
    private var options: [MenuOption] = []
    private var footer = ""
    private let map: GameMap?

    init(map: GameMap? = nil) {
        self.map = map
    }

    private func constructFreeCharsList() {
        freeChars = Array("abcdefghijklmnopqrstuvwxyz123456789")
    }

    func readInput(_ display: Display) async throws -> Action {
        // The Java version waited forever on an empty menu (e.g. aiming with no targets in sight).
        if options.isEmpty { return DoNothingCustomMessageAction("finds nothing to do") }
        while true {
            let key = try await display.readChoice(options, footer: footer.isEmpty ? nil : footer, isSubmenu: true)
            if let action = keyToAction[key] { return action }
        }
    }

    func addActionToMenu(_ action: Action, _ actor: Actor, hotkey: Character? = nil) {
        if freeChars == nil && hotkey == nil {  // auto-assigned hotkeys will be needed
            constructFreeCharsList()
        }
        guard let c = hotkey ?? freeChars?.first else { return }
        freeChars?.removeAll { $0 == c }
        options.removeAll { $0.key == c }
        keyToAction[c] = action
        options.append(action.menuOption(key: c, actor: actor, map: map))
    }

    func setFooter(_ footer: String) {
        self.footer = footer
    }
}
