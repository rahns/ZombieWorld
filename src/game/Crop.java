package game;

import edu.monash.fit2099.engine.Ground;
import edu.monash.fit2099.engine.Location;

public class Crop extends Ground {
	private int age = 0;
	private int fertaliserAmount = 10;

	public Crop() {
		super('=');
	}
	
	@Override
	public void tick(Location location) {
		super.tick(location);

		age++;
		if (age == 10)
			displayChar = ':';
		if (isRipe())
			displayChar = '8';
	}
	
	public boolean isRipe() {
		return age>=20;
	}
	
	public void fertalise() {
		age+=fertaliserAmount;
	}

}
