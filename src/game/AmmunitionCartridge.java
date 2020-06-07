package game;

import edu.monash.fit2099.engine.Item;

public class AmmunitionCartridge extends Item {
	
	private int bulletCount;
	private static int STARTING_COUNT = 5;

	public AmmunitionCartridge() {
		super("ammunition cartridge", 'a', true);
		this.bulletCount=STARTING_COUNT;
	}
	


	public void reduceBulletCount() {
		if (!(this.isEmpty())){
			this.bulletCount-=1;
		}
		
	}
	
	public int getBulletCount() {
		return this.bulletCount;
	}
	
	public boolean isEmpty() {
		return bulletCount==0;
	}

}
