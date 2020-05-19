package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;

public class FertaliseAction extends Action {
	
	protected Location target;


	public FertaliseAction(Location target) {
		this.target=target;
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		((Crop) target.getGround()).fertalise();
		return "Fertilised crop";
	}

	@Override
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return null;
	}

}
