package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
/**
 * Class for the action that fertilises a crop
 */ 
public class FertiliseAction extends Action {
	
	private Location target;

	/**
	 * Constructor
	 * @param target : location of crop to be fertilised
	 */
	public FertiliseAction(Location target) {
		this.target=target;
	}
	
	@Override
	//TODO idk if we have to document overides
	public String execute(Actor actor, GameMap map) {
		((Crop) target.getGround()).fertalise();
		return actor.toString() + " fertilised crop";
	}

	@Override
	public String menuDescription(Actor actor) {
		return "Fertilse crop";
	}

}
