package game;

import java.util.Random;

import edu.monash.fit2099.engine.IntrinsicWeapon;

public class MissableIntrinsicWeapon extends IntrinsicWeapon implements HitProbability {
	protected int hitProbability;
	private boolean hits;
	private Random rand= new Random();
	
	public MissableIntrinsicWeapon(int damage, String verb, int hitProbability) {
		super(damage, verb);
		this.hitProbability=hitProbability;
		if (rand.nextInt(100)<hitProbability) {
			hits=true;
		}
		
	}


	
	@Override
	public int getHitProbability() {
		return hitProbability;
	}
}
