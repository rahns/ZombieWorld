package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;

/**
 * An action for shopping at a shop
 * @author Rahn Stavar
 *
 */
public class ShopAction extends Action implements MenuAction {
	
	private Shop shop;
	
	/**
	 * ShopAction Constructor
	 * @param shop the shop to buy from
	 */
	public ShopAction(Shop shop) {
		this.shop = shop;
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
		return "Shop at " + shop;
	}

	/**
	 * Gets the sub menu of options at the shop
	 * @param actor the actor using the shop
	 * @param map the map that the shop is on (not needed, present to comply with interface)
	 * @param display the display to show the sub menu on
	 * @return the action chosen on the sub-menu
	 */
	@Override
	public Action getAction(Actor actor, GameMap map, Display display) {
		SubMenu sub = new SubMenu();
		try {
			if (actor instanceof Wallet) {
				display.println("\nProducts: ");
				for (Product product : shop.getProducts()) {
					if (((Wallet) actor).getWealth() < product.getCost()) {
						display.println(product.getItem() + " - Can't afford, Costs: " + product.getCost() + " coins");
						continue;
					}
					sub.addActionToMenu(new BuyAction(product), actor, display, null);
				}
				
				// Add quit option
				Action quitAction = new DoNothingCustomMessageAction("leaves the shop without buying anything");
				sub.addActionToMenu(quitAction, actor, display, null);

				return sub.readInput(display);
			}
			else {
				throw new Exception("Only actors who implement Wallet can buy things");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
