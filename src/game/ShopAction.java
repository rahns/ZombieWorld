package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Menu;

/**
 * An action for shopping at a shop
 * @author Rahn Stavar
 *
 */
public class ShopAction extends Action implements MenuAction {
	
	private ArrayList<Product> products;
	private String shopName;
	
	/**
	 * ShopAction Constructor
	 * @param products a list of the products available at the shop
	 * @param shopName the name of the shop
	 */
	public ShopAction(ArrayList<Product> products, String shopName) {
		this.products = products;
		this.shopName = shopName;
	}

	/**
	 * Does nothing, as this action launches a sub-menu.
	 * This method is present to comply with the Action interface
	 */
	@Override
	public String execute(Actor actor, GameMap map) {
		return null;
	}

	/**
	 * Gets a description of the action
	 * @param actor the actor who will shop
	 * @return a description of the shop action
	 */
	@Override
	public String menuDescription(Actor actor) {
		return "Shop at " + shopName;
	}

	/**
	 * Gets the sub menu of options at the shop
	 * @param map the map that the shop is on
	 * @return the sub-menu of options at the shop
	 */
	@Override
	public Menu getMenu(GameMap map) {
		return new ShopMenu(products);
	}

}
