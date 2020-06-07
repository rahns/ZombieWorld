package game;


import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
import edu.monash.fit2099.engine.NumberRange;

public class ChooseActorMenu extends SubMenu {
	
	//TODO Make this scan all actors in the map and create a menu to select one. Then open menu to aim or shoot
	
	SniperRifle sniper;
	GameMap map;
	public ChooseActorMenu(GameMap map, SniperRifle sniper) {
		this.sniper=sniper;
		this.map=map;
 	}

	public Action showMenu(Actor actor, Actions nothing, Display display) {
		NumberRange x =map.getXRange();
		NumberRange y = map.getYRange();
		for (int xcoord : x) {
			for (int ycoord : y) {
				Location loc =map.at(xcoord, ycoord);
				if (map.isAnActorAt(loc)){
					Actor a = map.getActorAt(loc);
					if (actor.hasCapability(ZombieCapability.ALIVE) && a.hasCapability(ZombieCapability.UNDEAD)){
						addActionToMenu(new SniperAction(sniper,a),actor,display,null);
					}
					
				}
			}
		}
		return readInput(display);
		
	}

}
