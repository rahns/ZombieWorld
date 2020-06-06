package game;

import edu.monash.fit2099.engine.Actor;

public class SniperRifle extends Gun implements HitProbability{
	int aim_level;
	Actor target;

	public SniperRifle() {
		super("Sniper Rifle", 'R', 15, "snipes");
		allowableActions.add(new SniperAction());
		this.aim_level=0;
	}
	@Override
	public int getHitProbability() {
		if (this.aim_level==0) {
			return 75;
		}
		if (this.aim_level==1) {
			return 90;
		}
		return 100;

		
	}
	
	@Override
	public int damage() {
		int d=super.damage();
		if (this.aim_level==0) {
			return d;
		}
		if (this.aim_level==1) {
			return d;
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
		this.aim_level+=1;
	}
	public void resetAimLevel() {
		this.aim_level=0;
	}
	
	

}
