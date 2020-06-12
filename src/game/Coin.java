package game;

import edu.monash.fit2099.engine.DropItemAction;
import edu.monash.fit2099.engine.Item;

/**
 * A class representing a unit of the game's currency
 * @author Rahn Stavar
 *
 */
public class Coin extends Item {

	/**
	 * Coin constructor
	 */
	public Coin() {
		// Coin is set to not portable as we don't want to be able to pick it up with PickUpItemAction
		// It needs its own pick up action to add to the player's wallet and allow picking up many coins in one turn
		super("coin", '$', false);
	}
	
	/**
	 * Gets an action to drop this coin
	 * @return a new DropItemAction for dropping this coin
	 */
	@Override
	public DropItemAction getDropAction() {  // Allows coins to be dropped despite not being portable
		return new DropItemAction(this);
	}

}
