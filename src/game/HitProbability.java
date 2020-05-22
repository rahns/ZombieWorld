/**
 * 
 */
package game;

/**
 * Interface for anything that has a chance of hitting or missing.
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
