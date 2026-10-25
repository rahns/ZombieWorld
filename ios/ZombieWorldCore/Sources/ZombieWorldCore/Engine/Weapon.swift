/// Something an Actor can attack with.
protocol Weapon: AnyObject {
    var damage: Int { get }
    var verb: String { get }
}

/// A weapon that is part of an Actor's body (fists, teeth...).
class IntrinsicWeapon: Weapon {
    let damage: Int
    let verb: String

    init(_ damage: Int, _ verb: String) {
        self.damage = damage
        self.verb = verb
    }
}

/// An Item that can be used as a weapon.
class WeaponItem: Item, Weapon {
    var damage: Int
    let verb: String

    init(_ name: String, _ displayChar: Character, _ damage: Int, _ verb: String) {
        self.damage = damage
        self.verb = verb
        super.init(name, displayChar, portable: true)
    }
}
