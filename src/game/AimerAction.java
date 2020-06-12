package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
/**
 * An action that will aim at a target with a selected gun.
 * @author ariehendrikse
 *
 */
public class AimerAction extends Action {

	private Actor target;
	private SniperRifle gun;
	/**
	 * 
	 * @param target - the target to aim at
	 * @param gun - the gun which will aim
	 */
	public AimerAction(Actor target, SniperRifle gun) {
		this.gun=gun;
		this.target=target;
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		this.gun.aim(target);
		return actor+" aimed at " + target;
	}

	@Override
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return actor+" aims at " +target;
	}

}
