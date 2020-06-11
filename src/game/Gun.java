package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.WeaponItem;
/**
 * Weapon item that can attack at range.
 * @author ariehendrikse
 */


public abstract class Gun extends WeaponItem implements HitProbability {
	
	protected AmmunitionCartridge ammo;
	
	public Gun(String name, char displayChar, int melee_damage, String verb) {
		super(name, displayChar, melee_damage, "whacks");
	}

	@Override
	public int getHitProbability() {
		return 100;
	}
	/**
	 * Reloads the gun with an {@code AmmunitionCartridge}
	 * @param ammo - the ammunition
	 */
	public void reload(AmmunitionCartridge ammo) {
		this.ammo=ammo;
	}
	
	/**
	 * Reloads the gun with an {@code AmmunitionCartridge}
	 * @param ammo - the ammunition
	 */
	public AmmunitionCartridge getAmmo() {
		return this.ammo;
	}
	/**
	 * Aims at a target
	 * 
	 * @param target
	 */
	public void aim(Actor target) {
		
	}
	
	/**
	 * Gets the damage the gun creates when shooting
	 * @return - value that will be used to hurt other actor 
	 */
	public abstract int shootDamage();

	public ArrayList<Action> getActions(Actor actor, GameMap map, Gun gun) {
		return null;
	}

	


}
