package game;

import java.util.ArrayList;
import java.util.HashMap;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.Menu;

public class ShopMenu extends Menu {
	
	private ArrayList<Product> products;
	private ArrayList<Character> freeChars = new ArrayList<Character>();
	private HashMap<Character, Action> keyToActionMap = new HashMap<Character, Action>();
	
	public ShopMenu(ArrayList<Product> products) {
		this.products = products;
	}
	
	@Override
	public Action showMenu(Actor actor, Actions actions, Display display) {
		try {
			if (actor instanceof Wallet) {
				for (char i = 'a'; i <= 'z'; i++)
					freeChars.add(i);
				
				display.println("\nProducts: ");
				for (Product product : products) {
					if (((Wallet) actor).getWealth() < product.getCost()) {
						display.println(product.getItem() + " - Can't afford, Costs: " + product.getCost() + " coins");
						continue;
					}
				Action action = new BuyAction(product);
				addActionToMenu(action, actor, display);
				}
				
				// Add quit option
				Action quitAction = new DoNothingCustomMessageAction("leaves the shop without buying anything");
				addActionToMenu(quitAction, actor, display);

				// Display menu
				char key;
				do {
					key = display.readChar();
				} while (!keyToActionMap.containsKey(key));

				return keyToActionMap.get(key);
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
	
	private void addActionToMenu(Action action, Actor actor, Display display) {
		char c = freeChars.get(0);
		freeChars.remove(Character.valueOf(c));
		keyToActionMap.put(c, action);
		display.println(c + ": " + action.menuDescription(actor));
	}

}
