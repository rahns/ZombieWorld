package game;

public interface Healing {
	public default int getHealAmount() {
		int healAmount=10;
		return healAmount;
	}
}
