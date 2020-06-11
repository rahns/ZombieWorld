package game;

import edu.monash.fit2099.engine.Actor;

public class SniperRifle extends Gun {
	private int aimLevel;
	private Actor target;
	
	
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
		int d=this.damage;
		if (this.aimLevel==0) {
			return d;
		}
		if (this.aimLevel==1) {
			return 2*d;
		}
		else if (this.aimLevel>1) {
			return ((ZombieActor) target).getMaxHitpoints();
		}
		else return d;
		
		
	}
	
	@Override
	public void aim(Actor target) {
		if (target==this.target) {
			this.aimLevel+=1;
		}
		else
		{
			this.aimLevel=0;
		}	

		this.target=target;
		
		
		
	}
	

	public void increaseAimLevel() {
		this.aimLevel+=1;
	}
	public void resetAimLevel() {
		this.aimLevel=0;
	}
	
	

}
