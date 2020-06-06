package game;

import edu.monash.fit2099.engine.Actor;

public class SniperRifle extends Gun implements HitProbability{
	private int aimLevel;
	private Actor target;

	public SniperRifle() {
		super("Sniper Rifle", 'R', 15, "snipes");
		allowableActions.add(new SniperAction());
		this.aimLevel=0;
	}
	@Override
	public int getHitProbability() {
		if (this.aimLevel==0) {
			return 75;
		}
		if (this.aimLevel==1) {
			return 90;
		}
		return 100;
		
	}
	
	@Override
	public int damage() {
		int d=super.damage();
		if (this.aimLevel==0) {
			return d;
		}
		if (this.aimLevel==1) {
			return 2*d;
		}
		return d*1000;
		
	}
	public void aim(Actor target) {
		this.target=target;
	}
	public ShootAction shoot() {
		ShootAction shoot = new ShootAction(target,this);
		return shoot;
	}
	public void increaseAimLevel() {
		this.aimLevel+=1;
	}
	public void resetAimLevel() {
		this.aimLevel=0;
	}
	
	

}
