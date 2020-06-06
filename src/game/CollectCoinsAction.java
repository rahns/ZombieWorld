package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;

public class CollectCoinsAction extends Action {
	
	private int numberOfCoins;
	
	public CollectCoinsAction(int numberOfCoins) {
		this.numberOfCoins = numberOfCoins;
	}

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

	@Override
	public String menuDescription(Actor actor) {
		String plural = numberOfCoins != 1 ? "s" : "";
		return actor + " collects " + numberOfCoins + " coin" + plural;
	}
}
