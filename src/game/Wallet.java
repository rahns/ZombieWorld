package game;

/**
 * An interface for objects in the game that can collect and spend coins
 * @author Rahn Stavar
 *
 */
public interface Wallet {

	/**
	 * A getter for the object's current number of coins
	 * @return the number of coins in the wallet
	 */
	public int getWealth();
	
	/**
	 * A method to spend an object's coins
	 * @param cost the number of coins to spend
	 */
	public void spendCoins(int cost);
	
	/**
	 * A method for giving a coin to the object
	 * @param coin a coins
	 */
	public void addCoinToWallet(Coin coin);
	
}
