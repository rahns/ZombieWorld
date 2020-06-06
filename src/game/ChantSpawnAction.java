package game;

import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

/**
 * An action for spawning new zombies in random locations
 * @author Rahn Stavar
 *
 */
public class ChantSpawnAction extends Action {
	
	private Random rand = new Random();
	private Integer zombieCount;
	private int numberToSpawn;
	private static final int MAX_SPAWN_TRIES = 1000;  // If after this many tries no valid locations for a new zombie were found, give up (map could be full)

	/**
	 * ChantSpawnAction constructor
	 * @param numberToSpawn the number of new zombies to try to spawn
	 */
	public ChantSpawnAction(int numberToSpawn) {
		this.numberToSpawn = numberToSpawn;
	}
	
	/**
	 * A method that makes this action happen
	 * @param actor the actor doing this action
	 * @param map the map the actor is on
	 * @return a description of what happened
	 */
	@Override
	public String execute(Actor actor, GameMap map) {
		zombieCount = 0;
		for (int i = 0; i < numberToSpawn; i++) {
			for (int maxTries = 0; maxTries < MAX_SPAWN_TRIES; maxTries++) {  // Try MAX_SPAWN_TRIES number of random locations per new zombie
				int x = rand.nextInt(map.getXRange().max());
				int y = rand.nextInt(map.getYRange().max());
				Zombie newZombie = new Zombie("A Mambo Minion", map);
				if (map.at(x, y).canActorEnter(newZombie)) {
					map.addActor(newZombie, map.at(x, y));
					zombieCount++;
					break;
				}
			}
		}
		return menuDescription(actor);
	}

	/**
	 * Gets a description of the action
	 * @param actor the actor who is doing this action
	 * @return a description of what happened, including the number of new zombies spawned
	 */
	@Override
	public String menuDescription(Actor actor) {
		Integer number = zombieCount;
		if (number == null) {  // Action hasn't been executed yet, so use the planned number of new zombies in the description
			number = numberToSpawn;
		}
		return actor + " chants and spawns " + number + " new zombies";
	}

}
