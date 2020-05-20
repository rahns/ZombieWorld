package game;


public class Bite extends MissableIntrinsicWeapon implements Healing{
	
	private int healAmount=5;

	public Bite() {
		super(10, "bites", 20);
	}
	
	public int gethealAmount() {
		return healAmount;
	}
}
