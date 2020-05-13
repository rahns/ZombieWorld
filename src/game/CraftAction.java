package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;

public class CraftAction extends Action{
	
	private CraftableWeapon item; 
	private Item newItem;
	
	public CraftAction(CraftableWeapon item) {
		this.item = item;
		this.newItem = item.craftsInto();
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		actor.addItemToInventory(newItem);
		actor.removeItemFromInventory(item);
		return actor.toString() + " crafted " + item.toString() + " into " + newItem.toString() + ".";
	}

	@Override
	public String menuDescription(Actor actor) {
		return "Craft " + item.toString() + " into " + newItem.toString();
	}

}
