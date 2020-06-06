package game;

import edu.monash.fit2099.engine.WeaponItem;

public class Gun extends WeaponItem implements HitProbability {
	
	AmmunitionCartridge ammo;

	public Gun(String name, char displayChar, int damage, String verb) {
		super(name, displayChar, damage, "shoots");
	}

	@Override
	public int getHitProbability() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void reload(AmmunitionCartridge ammo) {
		this.ammo=ammo;
	}

	public AmmunitionCartridge getAmmo() {
		return this.ammo;
	}
	

}
