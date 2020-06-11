package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Location;

public class ShotgunMenu extends SubMenu {
	private Shotgun gun;
	private GameMap map;
	Actor actor;
	int x;
	int y;
	
	
	
	public ShotgunMenu(GameMap map, Shotgun shotgun) {
		this.gun=shotgun;
		this.map=map;
		
 	}
	public Action showMenu(Actor actor, Actions actions, Display display) {
		
		String ammo_amount="";
		for (int i = 0;i< gun.getAmmo().getBulletCount();i++) {
			ammo_amount+="=";
		}
		
		display.println("Ammo: " +ammo_amount);
		//South
		String[] directions= {"North","South","East","West","North-East","North-West","South-East","South-West"};
		Location actor_loc=map.locationOf(actor);
		this.x = actor_loc.x();
		this.y = actor_loc.y();
		this.actor=actor;
		if (!(gun.getAmmo().isEmpty())) {
			for (String direction : directions) {
				this.addActionToMenu(createActionForDirection( direction), actor,display, null);
			}
		}
		
		
		Iterator<Item> iter= actor.getInventory().iterator();
		while (iter.hasNext()) {
			Item item = iter.next();
			if (item instanceof AmmunitionCartridge) {
				addActionToMenu(new ReloadAction((AmmunitionCartridge) item,gun), actor, display, null);
			}
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
		if (direction=="North-East") {
			return x+Math.abs(x2);
		}
		if (direction=="North-West") {
			return x-Math.abs(x2);
		}
		if (direction=="South-East") {
			return x+Math.abs(x2);
		}
		if (direction=="South-West") {
			return x-Math.abs(x2);
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
		if (direction=="North-East") {
			return y-Math.abs(x2);
		}
		if (direction=="North-West") {
			return y-Math.abs(x2);
		}
		if (direction=="South-East") {
			return y+Math.abs(x2);
		}
		if (direction=="South-West") {
			return y+Math.abs(x2);
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
					Location placeToShoot=map.at(x3,y3);
					if (!(locations.contains(placeToShoot))) {
						locations.add(placeToShoot);
					}
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
				if (a!=actor) {
					System.out.println(a+" here");
					targets.add(a);
				}
				
			}
			
		}
		return targets;
	}
	
	

	}


