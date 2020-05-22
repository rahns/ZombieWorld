package game;

/**
 * Interface for anything that defines its chance of hitting or missing when attacking.
 * @author ariehendrikse
 *
 */
public interface HitProbability {

	/**
	 * Getter for the chance of hitting in percent.
	 * @return int between 0 and 100 that is the percentage chance of hitting
	 */
	int getHitProbability();
	
}
