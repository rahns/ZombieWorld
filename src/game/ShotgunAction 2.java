package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.ActorLocations;
import edu.monash.fit2099.engine.Exit;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
import edu.monash.fit2099.engine.Menu;

public class ShotgunAction extends Action implements menuAction{
	
	static int SHOOT_RADIUS = 3;

	
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
	public String execute(Actor actor, GameMap map) {
		return null;
	}
	
	public Menu getMenu() {
		return new ShotgunMenu();
	}


	@Override
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return "Use shotgun";
	}


}
