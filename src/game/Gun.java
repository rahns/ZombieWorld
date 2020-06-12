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
	protected int aimLevel = 0;
	
	public Gun(String name, char displayChar, int meleeDamage, String verb) {
		super(name, displayChar, meleeDamage, "whacks");
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
		this.aimLevel+=1;
	}
	
	/**
	 * Gets the damage the gun creates when shooting
	 * @return - value that will be used to hurt other actor 
	 */
	public abstract int shootDamage();

	/**
	 * Gets a list of actions this gun can currently perform
	 * @param actor the actor using the gun
	 * @param map the map the gun is on
	 * @return a list of actions
	 */
	public abstract ArrayList<Action> getActions(Actor actor, GameMap map);
	
	/**
	 * Gets the ammmo amount and turns it into a pretty header for display.
	 * @return string of the ammo status
	 */
	public String getHeader() {
		String output="Ammo: ";
		for (int i =0;i<ammo.getBulletCount();i++) {
			output+="-";
		}
		return output;
	}
	/**
	 * Resets the aim to 0
	 */
	public void resetAimLevel() {
		this.aimLevel =0;
	}


	


}
