package game;

public class Shotgun extends Gun implements HitProbability {

	public Shotgun(String name, char displayChar, int damage, String verb) {
		super(name, displayChar, damage, verb);
		allowableActions.add(new ShotgunAction());
	}

}
