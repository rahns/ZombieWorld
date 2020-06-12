package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

/**
 * An action for picking up coins from the ground (can do multiple at a time)
 * @author Rahn Stavar
 *
 */
public class CollectCoinsAction extends Action {
	
	private ArrayList<Coin> coins = new ArrayList<>();
	
	/**
	 * CollectCoinsAction constructor
	 * @param coins the coins to pickup
	 */
	public CollectCoinsAction(ArrayList<Coin> coins) {
		this.coins = coins;
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
				for (Coin coin : coins) {
					((Wallet) actor).addCoinToWallet(coin);
					map.locationOf(actor).removeItem(coin);
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
		String plural = coins.size() != 1 ? "s" : "";
		return actor + " collects " + coins.size() + " coin" + plural;
	}
}
