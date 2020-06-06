package game;

import java.util.ArrayList;
import java.util.HashMap;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.Menu;

public class SniperMenu extends Menu {
	public Action showMenu(Actor actor, Actions actions, Display display) {
		ArrayList<Character> freeChars = new ArrayList<Character>();
		HashMap<Character, Action> keyToActionMap = new HashMap<Character, Action>();

		
		//TODO math on what locations are to be hit
			
		
		//keyToActionMap.put('1', new AimAction(actor));
		display.println("1: Aim");
		display.println("2: Shoot");

		

		char key;
		do {
			key = display.readChar();
		} while (!keyToActionMap.containsKey(key));

		return keyToActionMap.get(key);
	}

}
