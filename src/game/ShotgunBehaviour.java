package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Exit;
import edu.monash.fit2099.engine.GameMap;

public class ShotgunBehaviour extends AttackBehaviour {
	
	static int SHOOT_RADIUS = 3;

	public ShotgunBehaviour(ZombieCapability attackableTeam) {
		super(attackableTeam);
	}
	
	/**
	 * Returns a ShootAction that attacks a attackable Actor.
	 * 
	 * Actors are attackable if their ZombieCapability matches the 
	 * "undeadness status" set 
	 * 
	 * Only attacks characters in a cone shape surrounding the player and three square close.
	 * 
	 * @return Action: an AttackAction on an Actor that is undead, and withiin a range of 1
	 */
	@Override
	public Action getAction(Actor actor, GameMap map) {
		// Is there an attackable Actor next to me?
		List<Action> action = new ArrayList<Action>();
		//direction=ShotgunMenu.showMenu()
		
		return null;
	}

}
