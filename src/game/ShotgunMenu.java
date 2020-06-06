package game;

import java.util.ArrayList;
import java.util.HashMap;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;

public class ShotgunMenu extends SubMenu {
	public Action showMenu(Actor actor, Actions actions, Display display) {
		ArrayList<Character> freeChars = new ArrayList<Character>();
		HashMap<Character, Action> keyToActionMap = new HashMap<Character, Action>();

		
		//TODO math on what locations are to be hit
			
		
//		keyToActionMap.put(c, action);
		display.println("1: Shoot north");
		display.println("2: Shoot north-east");
		display.println("3: Shoot east");
		display.println("4: Shoot south-east");
		display.println("5: Shoot south");
		display.println("6: Shoot south-west");
		display.println("7: Shoot west");
		display.println("8: Shoot north-west");
		

		char key;
		do {
			key = display.readChar();
		} while (!keyToActionMap.containsKey(key));

		return keyToActionMap.get(key);
	}

	}


