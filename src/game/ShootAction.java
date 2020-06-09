package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Weapon;

public class ShootAction extends AttackAction {
	
	Gun gun;
	ArrayList<Actor> targets;

	public ShootAction(Actor target,Gun gun) {
		super(target);
		this.gun=gun;
		// TODO Auto-generated constructor stub
	}
	
	public ShootAction(ArrayList<Actor> targets,Gun gun) {
		super(targets);
		this.gun=gun;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		//TODO make this all inherit off of attack action but make the weapon the gun.
		// refer to the weapon's hit probability for the likeliness of missing
		try {
			if (gun instanceof HitProbability) {
				if (rand.nextInt(100) > ((HitProbability) gun).getHitProbability()) {
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
		if (gun instanceof Healing) {
			actor.heal(((Healing) gun).getHealAmount());
			healResultString += System.lineSeparator() + actor.toString() + " gained " + ((Healing) gun).getHealAmount() + " hit-points";
		}
		gun.aim(target);
		int damage = gun.shootDamage();
		String result = actor + " shoots " + target + " for " + damage + " damage";
		result += healResultString;

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
			
			result += System.lineSeparator() + target + " is killed";
		}

		return result;
	}
	@Override
	public String menuDescription(Actor actor) {
		return actor + " shoots at " + target + " " + ((ZombieActor) target).getHealthStatus();
	}

}
