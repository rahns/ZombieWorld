package game;

import java.util.ArrayList;
import java.util.HashMap;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;

public class ShotgunMenu extends SubMenu {
	private Shotgun gun;
	private GameMap map;
	public ShotgunMenu(GameMap map, Shotgun shotgun) {
		this.gun=shotgun;
		this.map=map;
 	}
	public Action showMenu(Actor actor, Actions actions, Display display) {
		
		
		//TODO math on what locations are to be hit, create actions then add them to options.
		Location actor_loc=map.locationOf(actor);
		int x = actor_loc.x();
		int y = actor_loc.y();
		
//		keyToActionMap.put(c, action);
		display.println("1: Shoot north");
		display.println("2: Shoot north-east");
		display.println("3: Shoot east");
		display.println("4: Shoot south-east");
		display.println("5: Shoot south");
		display.println("6: Shoot south-west");
		display.println("7: Shoot west");
		display.println("8: Shoot north-west");
		
		return null;

	}

	}


