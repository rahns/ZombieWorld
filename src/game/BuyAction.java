package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

/**
 * An action for buying an item with coins
 * @author Rahn Stavar
 *
 */
public class BuyAction extends Action {
	
	private Product product;
	
	/**
	 * BuyAction Constructor
	 * @param product the product to be bought
	 */
	public BuyAction(Product product) {
		this.product = product;
	}

	/**
	 * Complete the transaction
	 * @param actor the actor doing the action
	 * @param map the map the actor is on
	 * @return a description of the transaction
	 */
	@Override
	public String execute(Actor actor, GameMap map) {
		try {
			if (actor instanceof Wallet) {
				if (((Wallet) actor).getWealth() < product.getCost()) {
					return null;
				}
				((Wallet) actor).spendCoins(product.getCost());
				actor.addItemToInventory(product.getItem());
			}
			else {
				throw new Exception("Only actors who implement Wallet can buy things");
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return menuDescription(actor);
	}

	/**
	 * Gets a description of the transaction
	 * @param actor the actor making the purchase
	 * @return a description of the transaction
	 */
	@Override
	public String menuDescription(Actor actor) {
		String plural = product.getCost() != 1 ? "s" : "";
		return actor + " buys a " + product.getItem() + " for " + product.getCost() + " coin" + plural;
	}

}
