package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;

/**
 * An action for picking up coins from the ground (can do multiple at a time)
 * @author Rahn Stavar
 *
 */
public class CollectCoinsAction extends Action {
	
	private int numberOfCoins;
	
	/**
	 * CollectCoinsAction constructor
	 * @param numberOfCoins the number of coins in the location that coins will be picked up from
	 */
	public CollectCoinsAction(int numberOfCoins) {
		this.numberOfCoins = numberOfCoins;
	}

	/**
	 * Makes the action happen
	 * @param actor the actor picking up the coins
	 * @param map the game map the actor is on
	 * @return a description of what happened
	 */
	@Override
	public String execute(Actor actor, GameMap map) {
		try {
			if (actor instanceof Wallet) {
				ArrayList<Item> items = new ArrayList<>(map.locationOf(actor).getItems());
				for (Item item : items) {
					if (item instanceof Coin) {
						((Wallet) actor).addCoinToWallet((Coin) item);
						map.locationOf(actor).removeItem(item);
					}
				}
			}
			else {
				throw new Exception("Only actors who implement Wallet can collect coins");
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return menuDescription(actor);
	}

	/**
	 * Gets a description of what happened
	 * @param actor the actor collecting the coins
	 * @return a description of what happened
	 */
	@Override
	public String menuDescription(Actor actor) {
		String plural = numberOfCoins != 1 ? "s" : "";
		return actor + " collects " + numberOfCoins + " coin" + plural;
	}
}
