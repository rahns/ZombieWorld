package game;



import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Menu;
/**
 * Action for shotgun that displays the submenu for shotgun.
 * @author ariehendrikse
 *
 */
public class GunAction extends Action implements MenuAction{
	
	static int SHOOT_RADIUS = 3;
	private Gun gun;


	/**
	 * Constructor for the action
	 * @param shotgun - the shotgun that is to be used for the action.
	 */
	public GunAction(Gun gun) {
		this.gun=gun;

	}
	

	@Override
	public String execute(Actor actor, GameMap map) {
		return null;
	}
	


	@Override
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return "Use " +gun;
	}


	@Override
	public Action getAction(Actor actor,GameMap map, Display display) {
		SubMenu sub = new SubMenu();
		Action action = null;
		for (Action a : gun.getActions(actor,map,gun)) {
			sub.addActionToMenu(a, actor, display, null);
		}
		action = sub.readInput(display);
		if (action instanceof MenuAction) {
			action = ((MenuAction) action).getAction(actor,map, display);
		}
		
		return action;		
	}
}
