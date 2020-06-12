package game;


import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;
/**
 * Interface when an action has a sub menu.
 * @author ariehendrikse
 *
 */
public interface MenuAction {

	Action getAction(Actor actor, GameMap map, Display display);

}
