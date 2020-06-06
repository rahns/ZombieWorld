package game;

import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
import edu.monash.fit2099.engine.MoveActorAction;

/**
 * A class representing the special game character Mambo Marie
 * 
 * @author Rahn Stavar
 */
public class MamboMarie extends ZombieActor {
	
	private Location mambosVoid;  // The location Mambo Marie goes to when not on the mainMap
	private Location appearLocation;  // The location Mambo Marie spawns at on the mainMap
	private Random rand = new Random();
	private int turnsSinceAppearing;

	/**
	 * Mambo Marie Constructor
	 * @param mambosVoid
	 * @param appearLoaction
	 */
	public MamboMarie(Location mambosVoid, Location appearLoaction) {
		super("Mambo Marie", 'M', 100, ZombieCapability.UNDEAD, 70);
		this.mambosVoid = mambosVoid;
		this.appearLocation = appearLoaction;
	}

	/**
	 * Decides the action Mambo Marie should take on any given turn
	 *
	 * @param map the map Mambo Marie is on
	 * 
	 * Only the map parameter is used. The other parameters make this method comply with the required interface.
	 * 
	 * @return the action Mambo Marie should take this turn
	 */
	@Override
	public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
		if (mambosVoid.getActor() == this) {
			if (rand.nextInt(100) < 5) {
				turnsSinceAppearing = 0;
				return new MoveActorAction(appearLocation, "from her void to the compound");
				}
			else {
				return new DoNothingCustomMessageAction("sits in her void");
			}
		}
		else {
			turnsSinceAppearing++;
			if (turnsSinceAppearing == 30) {
				return new MoveActorAction(mambosVoid, "back into her void");
			}
			else if (turnsSinceAppearing % 10 == 0) {  // Every 10 turns, chant
				return new ChantSpawnAction(5);
			}
			else {  // Wander if not chanting
				return new WanderBehaviour().getAction(this, map);
			}
		}		
	}

}
