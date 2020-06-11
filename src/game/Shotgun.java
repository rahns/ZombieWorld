package game;

import java.util.ArrayList;
import java.util.Iterator;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.DoNothingAction;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Location;

/**
 * Gun that shoots in cone shapes from the chosen direction. Hitting multiple actors
 * in the chosen area.
 * @author ariehendrikse
 *
 */
public class Shotgun extends Gun implements HitProbability {

	private int x;
	private int y;
		
	public Shotgun() {
		super("shotgun", 'S', 13, "blasts");
		this.ammo= new AmmunitionCartridge();
		allowableActions.add(new GunAction(this));
	}

	@Override
	public int shootDamage() {
		return 15;
	}
	public String getMenuHeader() {
		String ammo_amount="";
		for (int i = 0;i< this.getAmmo().getBulletCount();i++) {
			ammo_amount+="=";
		}
		
		return "Ammo: " +ammo_amount;
	}
	@Override
	public ArrayList<Action> getActions(Actor actor,GameMap map, Gun gun) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		
		//South
		String[] directions= {"North","South","East","West","North-East","North-West","South-East","South-West"};
		Location actor_loc=map.locationOf(actor);
		this.x = actor_loc.x();
		this.y = actor_loc.y();
		if (!(this.getAmmo().isEmpty())) {
			for (String direction : directions) {
				actions.add(createActionForDirection(map, direction,actor));
			}
		}
		
		
		Iterator<Item> iter= actor.getInventory().iterator();
		while (iter.hasNext()) {
			Item item = iter.next();
			if (item instanceof AmmunitionCartridge) {
				actions.add(new ReloadAction((AmmunitionCartridge) item,this));
			}
		}
		actions.add(new DoNothingAction());
		return actions;

	}
	/**
	 * Gets the shotgun x coordinate based on the direction.
	 * @param direction
	 * @param x2 - the width of the shot.
	 * @param y2 - the depth of the shot.
	 * @return int - the x coordinate for the location
	 */
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
	/**
	 * Gets the shotgun y coordinate based on the direction.
	 * @param direction
	 * @param x2 - the width of the shot.
	 * @param y2 - the depth of the shot.
	 * @return int - the y coordinate for the location
	 */
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
	/**Scans the range in a cone shape with length 3 when N,E,S,W and adds the actors.
	 * If two compass directions are passed it will return the actions for the top right hand square.
	 * 
	 * @param map- the current game map
	 * @param direction - the direction with which the shot will be.
	 * @param actor - the actor that is shooting.
	 * @return a {@code ShootAction} that shoots at all the actors in the range
	 */
	private ShootAction createActionForDirection(GameMap map,String direction,Actor actor){
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
		ArrayList<Actor> targets = getAllActors(locations,map,actor);
		return new ShootAction(targets,this,direction);
		
		
	}
	
	private ArrayList<Actor> getAllActors(ArrayList<Location> locations, GameMap map, Actor actor) {
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
	
	public String getHeader() {
		String output="Ammo: ";
		for (int i =0;i<ammo.getBulletCount();i++) {
			output+="=";
		}
		return output;
	}

	
	

}





