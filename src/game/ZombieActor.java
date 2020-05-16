package game;

import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

/**
 * Base class for Actors in the Zombie World
 * @author ram, ariehendrikse
 *
 */
public abstract class ZombieActor extends Actor implements HitProbability {
	
	private int hitProbability;
		
<<<<<<< HEAD
	

	public ZombieActor(String name, char displayChar, int hitPoints, ZombieCapability team) {
=======
	public ZombieActor(String name, char displayChar, int hitPoints, ZombieCapability team, int hitProbability) {
>>>>>>> refs/remotes/origin/master
		super(name, displayChar, hitPoints);
		
		this.hitProbability = hitProbability;
		addCapability(team);
	}
	
	@Override
	public Actions getAllowableActions(Actor otherActor, String direction, GameMap map) {
		Actions list = super.getAllowableActions(otherActor, direction, map);
		if (otherActor.hasCapability(ZombieCapability.UNDEAD) != this.hasCapability(ZombieCapability.UNDEAD))
			list.add(new AttackAction(this));
		return list;
	}
	
	public int getHitProbability() {
		return hitProbability;
	}

	public void setHitProbability(int hitProbability) {
		this.hitProbability = hitProbability;
	}

}
