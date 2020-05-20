package game;


public class Bite extends MissableIntrinsicWeapon implements Healing {
	
	private static final int HEAL_AMOUNT = 5;

	public Bite() {
		super(10, "bites", 20);
	}
	
	public int gethealAmount() {
		return HEAL_AMOUNT;
	}
}
