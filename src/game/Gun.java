package game;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.WeaponItem;

public class Gun extends WeaponItem implements HitProbability {
	
	protected AmmunitionCartridge ammo;
	protected int melee_damage=12;

	public Gun(String name, char displayChar, int damage, String verb) {
		super(name, displayChar, damage, "melees");
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
		// TODO Auto-generated method stub
		
	}
	@Override
	public int damage() {
		return melee_damage;
	}

	public int shootDamage() {
		return melee_damage;
	}
	

}
