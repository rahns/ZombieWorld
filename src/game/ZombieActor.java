package game;

import java.util.ArrayList;
import java.util.List;

import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

/**
 * Base class for Actors in the Zombie World
 * @author ram
 *
 */
public abstract class ZombieActor extends Actor {
	
	protected List<Limb> limbs;
	
	
	public ZombieActor(String name, char displayChar, int hitPoints, ZombieCapability team) {
		super(name, displayChar, hitPoints);
		
		limbs = new ArrayList<>();
		limbs.add(new Arm());
		limbs.add(new Arm());
		limbs.add(new Leg());
		limbs.add(new Leg());
		
		addCapability(team);
	}
	
	@Override
	public Actions getAllowableActions(Actor otherActor, String direction, GameMap map) {
		Actions list = super.getAllowableActions(otherActor, direction, map);
		if (otherActor.hasCapability(ZombieCapability.UNDEAD) != this.hasCapability(ZombieCapability.UNDEAD))
			list.add(new AttackAction(this));
		return list;
	}
	
	protected int getLimbCount(String nameOfLimb) {
		int tally = 0;
		for (Limb aLimb: limbs) {
			if (aLimb.toString() == nameOfLimb) {
				tally += 1;
			}
		}
		return tally;
	}
}
