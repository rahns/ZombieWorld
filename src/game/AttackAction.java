package game;

import java.util.ArrayList;
import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Weapon;

/**
 * An action for attacking other Actors.
 */
public class AttackAction extends Action {

	/**
	 * The Actor that is to be attacked
	 */
	protected Actor target;
	ArrayList<Actor> targets = new ArrayList<Actor>();
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
		targets.add(target);
	}

	public AttackAction(ArrayList<Actor> targets) {
		this.targets=targets;
	}
	//TODO a lot of repeated code here
	@Override
	public String execute(Actor actor, GameMap map) {
		Weapon weapon = actor.getWeapon();
		
		// refer to the weapon's hit probability for the likeliness of missing
		try {
			if (weapon instanceof HitProbability) {
				if (rand.nextInt(100) > ((HitProbability) weapon).getHitProbability()) {
					return actor + " misses " + target + ".";
				}
			}
			// if not set for the weapon, used actor's default:
			else if (actor instanceof HitProbability) {
				if (rand.nextInt(100) > ((HitProbability) actor).getHitProbability()) {
					return actor + " misses " + target + ".";
				}
			}
			// Default hit probability if not set elsewhere:
			else if (rand.nextBoolean()){
				return actor + " misses " + target + ".";
			}
		} 
		catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		String healResultString = "";
		//Heal actor because it will be a successful attack
		if (weapon instanceof Healing) {
			actor.heal(((Healing) weapon).getHealAmount());
			healResultString += System.lineSeparator() + actor.toString() + " gained " + ((Healing) weapon).getHealAmount() + " hit-points";
		}

		int damage = weapon.damage();
		String result = actor + " " + weapon.verb() + " " + target + " for " + damage + " damage";
		result += healResultString;

		return hurt(damage, actor, map, result);
	}
	
	/**
	 * Allows hurting an actor without specifying an attacking actor or weapon.
	 * Useful for items which can damage actors, such as the turret.
	 * Hurts the actor but also handles the case when it dies, and drop everything.
	 * @param map the map the target is on
	 * @param damage the amount of damage to do
	 * @return a string describing the action
	 */
	public String executeWithoutWeapon(GameMap map, int damage) {
		return hurt(damage, null, map, null);	
	}

	@Override
	public String menuDescription(Actor actor) {
		return actor + " attacks " + target + " " + ((ZombieActor) target).getHealthStatus();
	}
	
	private String hurt(int damage, Actor actor, GameMap map, String result) {
		target.hurt(damage);
		if (result == null) {
			result = "";
		}
		if (!target.isConscious()) {
			boolean shouldRise = false;
			// Check if target was human and killed by a zombie:
			if (actor != null && target.hasCapability(ZombieCapability.ALIVE) && actor.hasCapability(ZombieCapability.UNDEAD)) {
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
			
			if (result != "") {
				result += System.lineSeparator();
			}

			result += target + " is killed";
		}
		return result;
	}
}
