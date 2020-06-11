package game;


public class Shotgun extends Gun implements HitProbability {

	public Shotgun() {
		super("shotgun", 'S', 13, "blasts");
		allowableActions.add(new ShotgunAction(this));
	}

	@Override
	public int shootDamage() {
		// TODO Auto-generated method stub
		return 15;
	}

	@Override
	public ShootAction shoot() {
		return null;
	}

}
