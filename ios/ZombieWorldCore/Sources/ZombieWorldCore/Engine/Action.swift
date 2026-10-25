/// Base class for Actions. These represent things that actors can do.
/// Port of `edu.monash.fit2099.engine.Action`.
class Action {
    init() {}

    /// Perform the Action.
    /// - Returns: a description of what happened that can be displayed to the user, or nil for nothing.
    func execute(_ actor: Actor, _ map: GameMap) -> String? {
        fatalError("\(type(of: self)) must override execute")
    }

    /// The text we put on the menu.
    func menuDescription(_ actor: Actor) -> String {
        fatalError("\(type(of: self)) must override menuDescription")
    }

    /// The key used in the menu to trigger this Action, or nil to have it assigned for you.
    var hotkey: String? { nil }

    /// Allows Actions to take more than one turn. nil means the Action completes in one turn.
    var nextAction: Action? { nil }
}

/// An ordered collection of Actions. Port of `edu.monash.fit2099.engine.Actions`.
final class Actions: Sequence {
    private var actions: [Action] = []

    init() {}

    init(_ action: Action) {
        add(action)
    }

    func add(_ other: Actions) {
        for action in other { add(action) }
    }

    func add(_ list: [Action]) {
        for action in list { add(action) }
    }

    @discardableResult
    func add(_ action: Action?) -> Bool {
        if let action { actions.append(action) }
        return true
    }

    /// Stable sort, matching Java's `Collections.sort`.
    func sorted(_ areInIncreasingOrder: (Action, Action) -> Bool) -> [Action] {
        actions.enumerated()
            .sorted { lhs, rhs in
                if areInIncreasingOrder(lhs.element, rhs.element) { return true }
                if areInIncreasingOrder(rhs.element, lhs.element) { return false }
                return lhs.offset < rhs.offset
            }
            .map(\.element)
    }

    func clear() { actions.removeAll() }

    var count: Int { actions.count }

    func remove(_ action: Action) { actions.removeFirstIdentical(action) }

    subscript(i: Int) -> Action { actions[i] }

    var list: [Action] { actions }

    func makeIterator() -> IndexingIterator<[Action]> { actions.makeIterator() }
}
