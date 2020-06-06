package game;

import java.util.ArrayList;
import java.util.HashMap;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Menu;

public class ChooseActorMenu extends Menu {
	
	//TODO Make this scan all actors in the map and create a menu to select one. Then open menu to aim or shoot
	
	SniperRifle sniper;
	GameMap map;
	public ChooseActorMenu(GameMap map) {
		this.map=map; 
 	}

	public Action showMenu(Actor actor, Actions actions, Display display) {
		ArrayList<Character> freeChars = new ArrayList<Character>();
		HashMap<Character, Action> keyToActionMap = new HashMap<Character, Action>();

		for (char i = 'a'; i <= 'z'; i++)
			freeChars.add(i);
		//TODO loop through all actors on map and add sniper action for them
		// Show with the actions with hotkeys first;
		for (Action action : actions) {
			String hotKey = action.hotkey();
			char c;
			if (hotKey == null || hotKey == "") {
				if (freeChars.isEmpty())
					break; // we've run out of characters to pick from.
				c = freeChars.get(0);
			} else {
				c = hotKey.charAt(0);
			}
			freeChars.remove(Character.valueOf(c));
			keyToActionMap.put(c, action);
			display.println(c + ": " + action.menuDescription(actor));
		}

		char key;
		do {
			key = display.readChar();
		} while (!keyToActionMap.containsKey(key));

		return keyToActionMap.get(key);
	}
}
