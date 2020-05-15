package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Location;
import edu.monash.fit2099.engine.WeaponItem;

/**
 * Represents a Zombie's limb and can be used as a weapon.
 * Can be crafted into a better weapon when in an actor's inventory.
 * 
 * @author Rahn Stavar
 *
 */
public abstract class Limb extends WeaponItem implements CraftableItem{

	/**
	 * Limb constructor.
	 * 
	 * @param name the name of the limb
	 * @param displayChar the character used to display the limb on a map
	 * @param damage the amount of damage done to an actor when used as a weapon
	 */
	public Limb(String name, char displayChar, int damage) {
		super(name, displayChar, damage, "whacked");
	}
	
	private void addCraftAction() {
		allowableActions.add(new CraftAction(this));
	}
	
    /**
     * Inform a carried Limb of the passage of time.
     * 
     * This method is called once per turn, if the Item is being carried.
     * @param currentLocation The location of the actor carrying this Item.
     * @param actor The actor carrying this Item.
     */
	@Override
	// This tick method runs every turn where the item is in an inventory
	public void tick(Location currentLocation, Actor actor) {
		// The item is being carried, add craft as allowable action if not already:
		for (Action action : allowableActions) {
			if (action instanceof CraftAction) {
				// Already in allowable actions, so do nothing:
				return;
			}
		}
		// Not already in allowable actions so add it:
		addCraftAction();
	}
	
    /**
     * Inform a Limb on the ground of the passage of time. 
     * 
     * This method is called once per turn, if the item rests upon the ground.
     * @param currentLocation The location of the ground on which the limb lies.
     */
	@Override
	// This tick method runs every turn where the item is on the ground
	public void tick(Location currentLocation) {
		// The item is on the ground, remove craft as allowable action if it is one
		for (Action action : allowableActions) {
			if (action instanceof CraftAction) {
				allowableActions.remove(action);
				return;
			}
		}
	}
}
