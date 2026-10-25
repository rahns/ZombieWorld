/// A small, fast, seedable random number generator (xoshiro256**). Every random decision in a
/// game comes from one of these, so a game can be replayed exactly from its seed and the
/// player's choices. The algorithms are written out here rather than taken from the standard
/// library, so results never change between Swift versions.
final class SeededRandom: @unchecked Sendable {
    private var s0: UInt64, s1: UInt64, s2: UInt64, s3: UInt64

    init(seed: UInt64) {
        // SplitMix64 to spread the seed over the state.
        var x = seed
        func splitMix() -> UInt64 {
            x &+= 0x9E37_79B9_7F4A_7C15
            var z = x
            z = (z ^ (z >> 30)) &* 0xBF58_476D_1CE4_E5B9
            z = (z ^ (z >> 27)) &* 0x94D0_49BB_1331_11EB
            return z ^ (z >> 31)
        }
        s0 = splitMix()
        s1 = splitMix()
        s2 = splitMix()
        s3 = splitMix()
    }

    func next() -> UInt64 {
        let result = ((s1 &* 5) << 7 | (s1 &* 5) >> 57) &* 9
        let t = s1 << 17
        s2 ^= s0
        s3 ^= s1
        s1 ^= s2
        s0 ^= s3
        s2 ^= t
        s3 = s3 << 45 | s3 >> 19
        return result
    }

    /// A uniformly distributed number in 0..<bound (Lemire's method, without bias).
    func below(_ bound: Int) -> Int {
        precondition(bound > 0, "bound must be positive")
        let n = UInt64(bound)
        let threshold = (0 &- n) % n
        while true {
            let product = next().multipliedFullWidth(by: n)
            if product.low >= threshold { return Int(product.high) }
        }
    }
}

/// Random numbers for the game, from the generator bound to the running game's task.
enum Dice {
    @TaskLocal static var source = SeededRandom(seed: UInt64.random(in: .min ... .max))

    static func int(_ range: Range<Int>) -> Int {
        range.lowerBound + source.below(range.count)
    }

    static func int(_ range: ClosedRange<Int>) -> Int {
        range.lowerBound + source.below(range.count)
    }

    static func bool() -> Bool {
        source.next() & 1 == 1
    }

    static func element<T>(of array: [T]) -> T? {
        array.isEmpty ? nil : array[source.below(array.count)]
    }

    /// Fisher-Yates shuffle.
    static func shuffled<T>(_ array: [T]) -> [T] {
        var result = array
        var i = result.count - 1
        while i > 0 {
            result.swapAt(i, source.below(i + 1))
            i -= 1
        }
        return result
    }
}
