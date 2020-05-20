package game;

public interface Healing {
	
	public static final int DEFAULT_HEAL_AMOUNT = 10;
	
	public default int getHealAmount() {
		return DEFAULT_HEAL_AMOUNT;
	}
}
