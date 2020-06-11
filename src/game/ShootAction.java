package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;

public class ShootAction extends AttackAction {
	
	Gun gun;
	String direction;
	boolean single;
	
	public ShootAction(Actor target,Gun gun) {
		super(target);
		targets.add(target);
		single=true;
		this.gun=gun;
		// TODO Auto-generated constructor stub
	}
	
	public ShootAction(ArrayList<Actor> targets,Gun gun,String direction) {
		
		super(targets);
		this.gun=gun;
		this.direction=direction;
		this.single=false;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		String result="";
		gun.getAmmo().reduceBulletCount();
		for (Actor t : targets) {
			System.out.println(t);
			if (t!=null) {
				boolean missed =false;
				if (gun instanceof HitProbability) {
					if (rand.nextInt(100) > ((HitProbability) gun).getHitProbability()) {
						result += System.lineSeparator() + actor + " misses " + t + ".";
						missed = true;
					}
				}
				// if not set for the weapon, used actor's default:
				else if (actor instanceof HitProbability) {
					if (rand.nextInt(100) > ((HitProbability) actor).getHitProbability()) {
						result += System.lineSeparator() + actor + " misses " + t + ".";
						missed = true;
					}
				}
				// Default hit probability if not set elsewhere:
				else if (rand.nextBoolean()){
					result += System.lineSeparator() + actor + " misses " + t + ".";
					missed = true;
				}
				
				
				
				if (!(missed)) {
					gun.aim(t);
					int damage = gun.shootDamage();
					result += actor + " shoots " + t + " for " + damage + " damage";
					t.hurt(damage);
					if (!t.isConscious()) {
						boolean shouldRise = false;
						// Check if target was human and killed by a zombie:
						if (t.hasCapability(ZombieCapability.ALIVE) && actor.hasCapability(ZombieCapability.UNDEAD)) {
							shouldRise = true;
						}
						Corpse corpse = new Corpse(t.toString(), shouldRise, map);
						map.locationOf(t).addItem(corpse);
						
						Actions dropActions = new Actions();
						for (Item item : t.getInventory())
							dropActions.add(item.getDropAction());
						for (Action drop : dropActions)		
							drop.execute(t, map);
						map.removeActor(t);	
						
						result += System.lineSeparator() + t + " is killed";
				}
				//Heal actor because it will be a successful attack
				if (gun instanceof Healing) {
					actor.heal(((Healing) gun).getHealAmount());
					result += System.lineSeparator() + actor.toString() + " gained " + ((Healing) gun).getHealAmount() + " hit-points";
				}
			}
			}
			
		
		
		}
		return result;
	}
	@Override
	public String menuDescription(Actor actor) {
		if (single) {
			return actor + " shoots at " + target + " " + ((ZombieActor) target).getHealthStatus();
		}
		return actor + " shoots " + this.direction;
	}

}
