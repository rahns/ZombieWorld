package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Location;
import edu.monash.fit2099.engine.WeaponItem;

public abstract class Limb extends WeaponItem implements CraftableItem{

	public Limb(String name, char displayChar, int damage) {
		super(name, displayChar, damage, "whacked");
	}
	
	private void addCraftAction() {
		allowableActions.add(new CraftAction(this));
	}
	
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
