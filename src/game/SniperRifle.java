package game;

import edu.monash.fit2099.engine.Actor;

public class SniperRifle extends Gun implements HitProbability{
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
		System.out.println(this.aimLevel);
		int d=this.damage;
		if (this.aimLevel==1) {
			return d;
		}
		if (this.aimLevel==2) {
			return 2*d;
		}
		else if (this.aimLevel>2) {
			return d*1000;
		}
		else return d;
		
		
	}
	@Override
	public void aim(Actor target,String currHealth) {
		if (!(shooterHealth.matches(currHealth))) {
			System.out.println("reset due to non matching health");
			aimLevel=0;
		}
		else {
			if (target==this.target) {
				this.aimLevel+=1;
			}
			else
			{
				System.out.println("reset due to non matching target");
				this.aimLevel=0;
			}
		this.shooterHealth=currHealth;
		this.target=target;

		}
		
		
		
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
