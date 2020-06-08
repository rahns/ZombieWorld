package game;

import edu.monash.fit2099.engine.Actor;

public class SniperRifle extends Gun {
	private int aimLevel;
	private Actor target;
	private String shooterHealth="";
	public SniperRifle() {
		super("Sniper Rifle", 'R', 15, "snipes");
		allowableActions.add(new ChooseActorAction(this));
		this.aimLevel=0;
		this.ammo=new AmmunitionCartridge();
	}
	@Override
	public int getHitProbability() {
		if (this.aimLevel==1) {
			return 75;
		}
		if (this.aimLevel==2) {
			return 90;
		}
		return 100;
		
	}
	
	@Override
	public int shootDamage() {
		System.out.println(this.aimLevel + "is the aim level");
		int d=this.damage;
		if (this.aimLevel==0) {
			return d;
		}
		if (this.aimLevel==1) {
			return 2*d;
		}
		else if (this.aimLevel>1) {
			return 10000;
		}
		else return d;
		
		
	}
	public void aim(Actor target) {
		if (target==this.target) {
			this.aimLevel+=1;
		}
		else
		{
			System.out.println("reset due to non matching target");
			this.aimLevel=0;
		}	

		this.target=target;
		
		
		
	}
	public ShootAction shoot() {
		ShootAction shoot = new ShootAction(target,this);
		ammo.reduceBulletCount();
		return shoot;
	}
	public void increaseAimLevel() {
		this.aimLevel+=1;
	}
	public void resetAimLevel() {
		this.aimLevel=0;
	}
	
	

}
