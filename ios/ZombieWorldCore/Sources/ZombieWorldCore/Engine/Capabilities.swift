/// Something that can have capabilities (any Hashable value, usually an enum case).
protocol Capable: AnyObject {
    func hasCapability(_ capability: AnyHashable) -> Bool
    func addCapability(_ capability: AnyHashable)
    func removeCapability(_ capability: AnyHashable)
}

/// A collection of capabilities. Port of `edu.monash.fit2099.engine.Capabilities`.
final class Capabilities: Capable {
    private var capabilitySet = Set<AnyHashable>()

    func hasCapability(_ capability: AnyHashable) -> Bool { capabilitySet.contains(capability) }
    func addCapability(_ capability: AnyHashable) { capabilitySet.insert(capability) }
    func removeCapability(_ capability: AnyHashable) { capabilitySet.remove(capability) }
}

/// Anything that can be drawn on the map.
protocol Printable: AnyObject {
    var displayChar: Character { get }
}

extension Array where Element: AnyObject {
    /// Removes the first element that is the same instance as `object` (Java's `List.remove(Object)` for identity-equal objects).
    mutating func removeFirstIdentical(_ object: AnyObject) {
        if let index = firstIndex(where: { $0 === object }) {
            remove(at: index)
        }
    }

    func containsIdentical(_ object: AnyObject) -> Bool {
        contains { $0 === object }
    }
}
