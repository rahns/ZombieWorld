package game;

import edu.monash.fit2099.engine.Item;
/**
 * Cartridge of ammunition that holds a number of bullets for any gun.
 * @author ariehendrikse
 *
 */
public class AmmunitionCartridge extends Item {
	
	private int bulletCount;
	private static int STARTING_COUNT = 5;
	/**
	 * Constructor for an ammunition cartidge
	 */
	public AmmunitionCartridge() {
		super("ammunition cartridge", 'a', true);
		this.bulletCount=STARTING_COUNT;
	}
	

	/**
	 * reduces the bullets in the gun by one
	 */
	public void reduceBulletCount() {
		if (!(this.isEmpty())){
			this.bulletCount-=1;
		}
		
	}
	/**
	 * Gets the amount of bullets in the gun 
	 * @return int - number 
	 */
	public int getBulletCount() {
		return this.bulletCount;
	}
	/**
	 * Checks if the cartridge has nay bullets left
	 * @return boolean - true if the gun has bullets false if not.
	 */
	public boolean isEmpty() {
		return bulletCount==0;
	}

}
