package game;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.DropItemAction;
import edu.monash.fit2099.engine.GameMap;
/**
 * Sets up a turret. Tells it is being placed and to start shooting.
 * In other words, turns the turret on.
 * @author Rahn Stavar
 *
 */
public class SetUpTurretAction extends DropItemAction {
	
	private Turret turret;
	/**
	 * Constructor.
	 * @param turret - the turret to be setup
	 */
	public SetUpTurretAction(Turret turret) {
		super(turret);
		this.turret = turret;
	}
	
	/**
	 * Set up the turret
	 * @param actor the actor setting up the turret
	 * @param map the map the action is happening on
	 */
	@Override
	public String execute(Actor actor, GameMap map) {
		if (!map.locationOf(actor).getItems().contains(turret)) {  // If the turret isn't already on the ground, drop it
			super.execute(actor, map);
		}
		turret.setUp(map);
		return actor + " set up a turret";
	}
	
	@Override
	public String menuDescription(Actor actor) {
		return actor + " sets up the turret here";
	}

}
