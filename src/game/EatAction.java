package game;


import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;

public class EatAction extends Action {
	
	Food food;
	
	public EatAction(Item target) {
		food=(Food) target;
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		actor.removeItemFromInventory(food);
		actor.heal(food.getHealAmount());
		return actor.toString() + " ate some food and recovered " + food.getHealAmount() + " hit points";
	}

	@Override
	public String menuDescription(Actor actor) {
		return actor.toString() + " eats food";
	}

}
