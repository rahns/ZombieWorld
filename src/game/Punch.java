package game;

import edu.monash.fit2099.engine.IntrinsicWeapon;

/**
 * Class for a punch weapon.
 * 
 * @author ariehendrikse
 */
public class Punch extends IntrinsicWeapon implements HitProbability {

	/**
	 * Constructor
	 */
	public Punch() {
		super(5, "punches");
	}

	@Override
	public int getHitProbability() {

		return 60;
	}

}
