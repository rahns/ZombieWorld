package game;

import java.util.ArrayList;
import java.util.HashMap;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.Menu;

/**
 * An abstract class for sub-menus that branch off the main menu
 * @author Rahn Stavar
 *
 */
public abstract class SubMenu extends Menu {
	
	private ArrayList<Character> freeChars = new ArrayList<Character>();
	private HashMap<Character, Action> keyToActionMap = new HashMap<Character, Action>();
	
	/**
	 * A method to initialise the free characters list for assigning hot-keys
	 */
	protected void constructFreeCharsList() {
		for (char i = 'a'; i <= 'z'; i++)
			freeChars.add(i);
	}
	
	/**
	 * A method for reading the chosen option from the display
	 * @param display the display to read from
	 * @return the action that was chosen
	 */
	protected Action readInput(Display display) {
		char key;
		do {
			key = display.readChar();
		} while (!keyToActionMap.containsKey(key));

		return keyToActionMap.get(key);
	}
	
	/**
	 * A method for adding an action to the menu
	 * @param action the action to add
	 * @param actor the actor who will be choosing a menu option
	 * @param display the display to show the option on
	 * @param hotkey the hot-key for this option - null to have it auto assigned
	 */
	protected void addActionToMenu(Action action, Actor actor, Display display, Character hotkey) {
		char c = (char) (hotkey != null ? hotkey : freeChars.get(0));
		if (freeChars.contains(Character.valueOf(c))){
			freeChars.remove(Character.valueOf(c));
		}
		keyToActionMap.put(c, action);
		display.println(c + ": " + action.menuDescription(actor));
	}

}
