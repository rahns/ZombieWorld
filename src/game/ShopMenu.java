package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;

public class ShopMenu extends SubMenu {
	
	private ArrayList<Product> products;
	
	public ShopMenu(ArrayList<Product> products) {
		this.products = products;
	}
	
	@Override
	public Action showMenu(Actor actor, Actions actions, Display display) {
		try {
			if (actor instanceof Wallet) {
				super.showMenu(actor, actions, display);
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
