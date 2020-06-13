package game;



import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;
/**
 * Action for shotgun that displays the submenu for shotgun.
 * @author ariehendrikse
 *
 */
public class UseGunAction extends Action implements MenuAction{
	
	private Gun gun;


	/**
	 * Constructor for the action
	 * @param gun the gun that is to be used for the action.
	 */
	public UseGunAction(Gun gun) {
		this.gun=gun;
	}
	

	@Override
	public String execute(Actor actor, GameMap map) {
		return null;
	}
	
	@Override
	public String menuDescription(Actor actor) {
		return "Use " +gun;
	}


	@Override
	public Action getAction(Actor actor,GameMap map, Display display) {
		SubMenu sub = new SubMenu();
		Action action;
		for (Action a : gun.getActions(actor,map)) {
			sub.addActionToMenu(a, actor, display, null);
			sub.setFooter(gun.getHeader());
		}
		action = sub.readInput(display);
		if (action instanceof MenuAction) {
			action = ((MenuAction) action).getAction(actor,map, display);
		}
		
		return action;		
	}
}
