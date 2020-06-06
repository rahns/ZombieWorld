package game;

import java.util.ArrayList;
import java.util.HashMap;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.Menu;

public abstract class SubMenu extends Menu {
	
	private ArrayList<Character> freeChars = new ArrayList<Character>();
	private HashMap<Character, Action> keyToActionMap = new HashMap<Character, Action>();
	
	@Override
	public Action showMenu(Actor actor, Actions actions, Display display) {
		for (char i = 'a'; i <= 'z'; i++)
			freeChars.add(i);
		return null;
	}
	
	protected Action readInput(Display display) {
		char key;
		do {
			key = display.readChar();
		} while (!keyToActionMap.containsKey(key));

		return keyToActionMap.get(key);
	}
	
	protected void addActionToMenu(Action action, Actor actor, Display display, Character hotkey) {
		char c = (char) (hotkey != null ? hotkey : freeChars.get(0));
		freeChars.remove(Character.valueOf(c));
		keyToActionMap.put(c, action);
		display.println(c + ": " + action.menuDescription(actor));
	}

}
