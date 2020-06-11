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
	int x;
	int y;
	
	
	
	public ShotgunMenu(GameMap map, Shotgun shotgun) {
		this.gun=shotgun;
		this.map=map;
		
 	}
	public Action showMenu(Actor actor, Actions actions, Display display) {
		
		
		//TODO math on what locations are to be hit, create actions then add them to options.
		//South
		String[] directions= {"North","South","East","West"};
		Location actor_loc=map.locationOf(actor);
		this.x = actor_loc.x();
		this.y = actor_loc.y();
		
		for (String direction : directions) {
			this.addActionToMenu(createActionForDirection( direction), actor,display, null);
		}
		
		
		
		return readInput(display);

	}
	
	private int directX(String direction,int x2,int y2) {
		if (direction=="South") {
			return x+x2;
		}
		if (direction=="North") {
			return x+x2;
		}
		if (direction=="East") {
			return x+y2;
		}
		if (direction=="West") {
			return x-y2;
		}
		return 0;
		
	}
	private int directY(String direction,int y2,int x2) {
		if (direction=="South") {
			return y+y2;
		}
		if (direction=="North") {
			return y-y2;
		}
		if (direction=="East") {
			return y+x2;
		}
		if (direction=="West") {
			return y+x2;
		}
		return 0;
		
	}
	
	private ShootAction createActionForDirection(String direction){
		ArrayList<Location> locations =  new ArrayList<Location>();
		for (int y2 = 3; y2>0;y2--) {
			
			for (int x2 = -y2; x2<y2;x2++) {
				int x3= directX(direction,x2,y2);
				int y3= directY(direction,y2,x2);
				if (map.getXRange().contains(x3) && map.getYRange().contains(y3) ) {
					locations.add(map.at(x3,y3));
				}
				
			}
		}
		ArrayList<Actor> targets = getAllActors(locations);
		return new ShootAction(targets,gun,direction);
		
		
	}
	
	private ArrayList<Actor> getAllActors(ArrayList<Location> locations) {
		ArrayList<Actor> targets =  new ArrayList<Actor>();
		for (Location loc : locations) {
			if (map.isAnActorAt(loc)) {
				Actor a=map.getActorAt(loc);
				System.out.println(a+" here");
				targets.add(a);
			}
			
		}
		return targets;
	}
	
	

	}


