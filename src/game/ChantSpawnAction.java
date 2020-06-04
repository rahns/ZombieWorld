package game;

import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

public class ChantSpawnAction extends Action {
	
	private Random rand = new Random();
	private int zombieCount = 0;
	private static final int MAX_SPAWN_TRIES = 1000;  // If after this many tries no valid locations for a new zombie were found, give up (map could be full)

	@Override
	public String execute(Actor actor, GameMap map) {
		for (int i = 0; i < 5; i++) {
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

	@Override
	public String menuDescription(Actor actor) {
		return actor + " chanted and spawned " + zombieCount + " new Zombies";
	}

}
