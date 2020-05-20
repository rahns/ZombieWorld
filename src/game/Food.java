package game;

import edu.monash.fit2099.engine.Item;

public class Food extends Item implements Healing {
	
	private static final int HIT_POINTS_RECOVERED = 10;

	public Food() {
		super("Food", '~', true);
		allowableActions.add(new EatAction(this));
	}
	
	/**
	 * A getter for this food's hitpoints recovered when eaten
	 * @return the number of hitpoints recovered when this food is eaten
	 */
	public int getHealAmount() {
		return HIT_POINTS_RECOVERED;
	}

}
