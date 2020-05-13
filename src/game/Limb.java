package game;

public abstract class Limb extends CraftableWeapon{

	public Limb(String name, char displayChar, int damage) {
		super(name, displayChar, damage, "whacked");
	}
}
