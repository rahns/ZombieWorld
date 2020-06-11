package game;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.WeaponItem;

public abstract class Gun extends WeaponItem implements HitProbability {
	
	protected AmmunitionCartridge ammo;
	
	public Gun(String name, char displayChar, int melee_damage, String verb) {
		super(name, displayChar, melee_damage, "whacks");
	}

	@Override
	public int getHitProbability() {
		return 100;
	}
	
	public void reload(AmmunitionCartridge ammo) {
		this.ammo=ammo;
	}

	public AmmunitionCartridge getAmmo() {
		return this.ammo;
	}

	public void aim(Actor target) {
		
	}

	public abstract int shootDamage();
	
	public abstract ShootAction shoot();
	

}
