package game;

/**
 * An interface for objects that can be switched between 2 states (such as a lever or door)
 * @author Rahn Stavar
 *
 */
public interface Toggleable {
	
	/**
	 * A method to make the Toggleable object change state
	 */
	public void toggle();
	
	/**
	 * A method to get a description of changing the specified state
	 * 
	 * @param stateToSwitchTo the state that the caller needs a description of changing to
	 * 
	 * @return a description of changing the state to the specified state
	 */
	public String switchToStateDescription(boolean stateToSwitchTo);

}
