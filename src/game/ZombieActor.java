package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;

/**
 * Base class for Actors in the Zombie World
 * @author ram, ariehendrikse
 *
 */
public abstract class ZombieActor extends Actor implements HitProbability {
	
	private final int hitProbability;
	ArrayList<Behaviour> behaviours = new ArrayList<>();
		
	
	public ZombieActor(String name, char displayChar, int hitPoints, ZombieCapability team, int hitProbability) {

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
	@Override
	public int getHitProbability() {
		return hitProbability;
	}
	@Override
	public void hurt(int points) {
		hitPoints -= points;
		for(Item item : this.inventory) {
			if (item instanceof SniperRifle) {
				((SniperRifle) item).resetAimLevel();
			}
		}
	}
	/**
	 * Getter for the actors health status
	 * @return String : The health status in the format (HitPoints/TmaxHitpoints)
	 */
	public String getHealthStatus() {
		return "("+this.hitPoints+"/"+maxHitPoints + ")";
	}
	
	/**
	 * A getter for the actor's maximum hit points.
	 * @return
	 */
	public int getMaxHitpoints() {
		return this.maxHitPoints;
	}

}