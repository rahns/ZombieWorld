package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;

/**
 * A menu for selecting what item to buy at the shop
 * @author Rahn Stavar
 *
 */
public class ShopMenu extends SubMenu {
	
	private ArrayList<Product> products;
	
	/**
	 * ShopMenu Constructor
	 * @param products a list of the products available to buy
	 */
	public ShopMenu(ArrayList<Product> products) {
		this.products = products;
	}
	
	/**
	 * Shows the menu of things to buy
	 * @param actor the actor who will select an option
	 * @param actions a list of actions - not used
	 * @param display the display to show the menu on
	 * @return a BuyAction to buy the chosen product
	 */
	@Override
	public Action showMenu(Actor actor, Actions actions, Display display) {
		try {
			if (actor instanceof Wallet) {
				super.constructFreeCharsList();
				display.println("\nProducts: ");
				for (Product product : products) {
					if (((Wallet) actor).getWealth() < product.getCost()) {
						display.println(product.getItem() + " - Can't afford, Costs: " + product.getCost() + " coins");
						continue;
					}
				Action action = new BuyAction(product);
				addActionToMenu(action, actor, display, null);
				}
				
				// Add quit option
				Action quitAction = new DoNothingCustomMessageAction("leaves the shop without buying anything");
				addActionToMenu(quitAction, actor, display, null);

				return readInput(display);
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
