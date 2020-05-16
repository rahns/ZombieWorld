package game;

import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Weapon;

/**
 * Special Action for attacking other Actors.
 */
public class AttackAction extends Action {

	/**
	 * The Actor that is to be attacked
	 */
	protected Actor target;
	/**
	 * Random number generator
	 */
	protected Random rand = new Random();

	/**
	 * Constructor.
	 * 
	 * @param target the Actor to attack
	 */
	public AttackAction(Actor target) {
		this.target = target;
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		Weapon weapon = actor.getWeapon();
		
		// refer to the weapon's hit probability for the likeliness of missing
		if (weapon instanceof HitProbability) {
			HitProbability tempWeapon = (HitProbability) weapon;
			if (rand.nextInt(100) < tempWeapon.getHitProbability()) {
				return actor + " misses " + target + ".";
			}
		}
		else if (actor instanceof ZombieActor) {
			ZombieActor tempActor = (ZombieActor) actor;
			if (rand.nextInt(100) < tempActor.getHitProbability()) {
				return actor + " misses " + target + ".";
			}
		}
		// Default probability if not set elsewhere:
		else if (rand.nextBoolean()){
			return actor + " misses " + target + ".";
		}

		int damage = weapon.damage();
		String result = actor + " " + weapon.verb() + " " + target + " for " + damage + " damage.";

		target.hurt(damage);
		if (!target.isConscious()) {
			boolean shouldRise = false;
			// Check if target was human and killed by a zombie:
			if (target.hasCapability(ZombieCapability.ALIVE) && actor.hasCapability(ZombieCapability.UNDEAD)) {
				shouldRise = true;
			}
			Corpse corpse = new Corpse(target.toString(), shouldRise, map);
			map.locationOf(target).addItem(corpse);
			
			Actions dropActions = new Actions();
			for (Item item : target.getInventory())
				dropActions.add(item.getDropAction());
			for (Action drop : dropActions)		
				drop.execute(target, map);
			map.removeActor(target);	
			
			result += System.lineSeparator() + target + " is killed.";
		}

		return result;
	}

	@Override
	public String menuDescription(Actor actor) {
		return actor + " attacks " + target;
	}
}
