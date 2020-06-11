package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;

public class ChooseAimAction extends Action implements MenuAction{
	Actor target;
	SniperRifle gun;



	public ChooseAimAction( SniperRifle sniperRifle) {

		this.gun=sniperRifle;
	}

	
	
	@Override
	public Action getMenu(Actor actor,GameMap map, Display display) {
		SubMenu sub = new SubMenu(display);
		Action action = null;
		for (Actor a : ((SniperRifle) gun).getTargets(actor,map,gun)) {
			sub.addActionToMenu(new AimerAction(a,gun),actor,display,null);
		}
		action = sub.readInput(display);
		if (action instanceof MenuAction) {
			action = ((MenuAction) action).getMenu(actor,map, display);
		}
		
		return action;	
	}
	@Override
	public String menuDescription(Actor actor) {
		return "Aim";
	}



	@Override
	public String execute(Actor actor, GameMap map) {
		// TODO Auto-generated method stub
		return null;
	}

}
