package game;



import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;

import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Menu;

public class ShotgunAction extends Action implements MenuAction{
	
	static int SHOOT_RADIUS = 3;
	private Shotgun gun;
	String direction;

	
	public ShotgunAction(Shotgun shotgun) {
		this.gun=shotgun;
	}
	

	@Override
	public String execute(Actor actor, GameMap map) {
		return null;
	}
	@Override
	public Menu getMenu(GameMap map) {
		return new ShotgunMenu(map, gun);
	}


	@Override
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return "Use shotgun";
	}




}
