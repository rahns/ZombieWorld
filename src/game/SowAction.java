package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;

public class SowAction extends Action {

	private Location destination;

	public SowAction(Location destination) {
		this.destination=destination;
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		destination.setGround(new Crop());
		return actor.toString() + " sowed a crop";
	}

	@Override
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return null;
	}

}
