package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Menu;

public class ShopAction extends Action implements MenuAction {
	
	private ArrayList<Product> products;
	private String shopName;
	
	public ShopAction(ArrayList<Product> products, String shopName) {
		this.products = products;
		this.shopName = shopName;
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		return null;
	}

	@Override
	public String menuDescription(Actor actor) {
		return "Shop at " + shopName;
	}

	@Override
	public Menu getMenu() {
		return new ShopMenu(products);
	}

}
