package game;

import java.util.ArrayList;
import java.util.List;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Menu;

/**
 * Class representing the Player.
 */
public class Player extends Human {

	private Menu menu = new Menu();
	private ArrayList<Coin> wallet = new ArrayList<>();
	
	/**
	 * Constructor.
	 *
	 * @param name        Name to call the player in the UI
	 * @param displayChar Character to represent the player in the UI
	 * @param hitPoints   Player's starting number of hitpoints
	 */
	public Player(String name, char displayChar, int hitPoints) {
		super(name, displayChar, hitPoints);
	}
	
	/**
	 * Gets the number of coins in the player's wallet
	 * @return
	 */
	public int getWealth() {
		return wallet.size();
	}
	
	public void spendCoins(int cost) {
		try {
			if (cost > getWealth()) {
				throw new Exception(this + " does not have enough coins");
			}
			for (int i = 0; i < cost; i++) {
				wallet.remove(wallet.size() - 1);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
		spendCoins(10);
		// Also consider harvest actions:
		actions.add(new HarvestBehaviour().getAction(this, map));
		
		// Handle multi-turn Actions
		if (lastAction.getNextAction() != null)
			return lastAction.getNextAction();
		display.println("Current player health: " + hitPoints + "/" + maxHitPoints);
		
		
		Action action= menu.showMenu(this, actions, display);
		
		while (action instanceof MenuAction) {
			menu=((MenuAction) action).getMenu();
			action=menu.showMenu(this, actions, display);
		}
		
		return action;
	}
}
