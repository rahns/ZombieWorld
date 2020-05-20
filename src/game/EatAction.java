package game;


import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;

public class EatAction extends Action {
	
	Food food;
	int hitPointsRecovered;
	
	public EatAction(Item target) {
		food=(Food) target;
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		actor.removeItemFromInventory(food);
		actor.heal(hitPointsRecovered);
		return actor.toString() + " ate some food and recovered " + hitPointsRecovered + " hit points";
	}

	@Override
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return null;
	}

}
