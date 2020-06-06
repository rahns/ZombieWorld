package game;

public interface Wallet {

	public int getWealth();
	
	public void spendCoins(int cost);
	
	public void addCoinToWallet(Coin coin);
	
}
