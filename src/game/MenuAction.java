package game;


import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;
/**
 * Interface for actions that open menus to select another action
 * @author ariehendrikse
 *
 */
public interface MenuAction {

	/**
	 * A method to get an action from a MenuAction
	 * @param actor the actor doing the action
	 * @param map the map it is taking place on
	 * @param display the display to show the menu on
	 * @return an action
	 */
	public Action getAction(Actor actor, GameMap map, Display display);

}
