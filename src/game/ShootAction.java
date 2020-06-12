package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

/**
 * This is an action class that shoots either a target or group of
 * targets and then decreases the ammunition in the gun
 * @author ariehendrikse
 *
 */
public class ShootAction extends AttackAction {
	
	private Gun gun;
	private String direction;
	private boolean isSingleTarget;
	
	/**
	 * ShootAction constructor
	 * @param target - the target actor
	 * @param gun - the gun object that will be shooting 
	 * @author ariehendrikse
	 *
	 */
	public ShootAction(Actor target,Gun gun) {
		super(target);
		isSingleTarget=true;
		this.gun=gun;
	}
	/**
	 * ShootAction Constructor
	 * @param targets - A collection of actors to shoot at.
	 * @param gun - the gun object that will be shooting 
	 * @param direction - the direction that gun is shooting in (for {@code menuDescription})
	 * @author ariehendrikse
	 *
	 */
	public ShootAction(ArrayList<Actor> targets,Gun gun,String direction) {
		
		super(targets);
		this.gun=gun;
		this.direction=direction;
		this.isSingleTarget=false;

	}
	
	@Override
	public String execute(Actor actor, GameMap map) {
		String result="";
		gun.getAmmo().reduceBulletCount();
		if (targets.size() == 0) {
			result += actor + " shoots at nobody";
		}
		for (Actor t : targets) {
			if (result != "") {
				result += System.lineSeparator();
			}
			if (t!=null) {
				boolean missed = misses(actor, gun);
				if (missed) {
					if (result != "") {
						result += System.lineSeparator();
					}
					result += actor + " misses " + t + ".";
				}
				//choose the target
				else {
					gun.aim(t, false); //Aim but don't increase aim level
					int damage = gun.shootDamage();
					result += actor + " shoots " + t + " for " + damage + " damage";
					t.hurt(damage);
					if (!t.isConscious()) {
						proccessDeath(actor, map, t);
						result += System.lineSeparator() + t + " is killed";
					}
				//Heal actor because it will be a successful attack
				result += healAttacker(actor, gun);
				}
			}
		}
		return result;
	}
	
	
	@Override
	public String menuDescription(Actor actor) {
		if (isSingleTarget) {
			return actor + " shoots at " + target + " " + ((ZombieActor) target).getHealthStatus();
		}
		return actor + " shoots " + this.direction;
	}

}
