package game;

public class Shotgun extends Gun implements HitProbability {

	public Shotgun() {
		super("shotgun", 's', 13, "blasts");
		allowableActions.add(new ShotgunAction(this));
	}

}
