package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

public class BuyAction extends Action {
	
	private Product product;
	
	public BuyAction(Product product) {
		this.product = product;
	}

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

	@Override
	public String menuDescription(Actor actor) {
		String plural = product.getCost() != 1 ? "s" : "";
		return actor + " buys a " + product.getItem() + " for " + product.getCost() + " coin" + plural;
	}

}
