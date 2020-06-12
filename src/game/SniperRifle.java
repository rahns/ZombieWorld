package game;

import java.util.ArrayList;
import java.util.Iterator;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.DoNothingAction;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Location;
import edu.monash.fit2099.engine.NumberRange;

public class SniperRifle extends Gun {
	private Actor target;	
	
	public SniperRifle() {
		super("Sniper Rifle", 'R', 15, "snipes");
		allowableActions.add(new UseGunAction(this));
		this.ammo=new AmmunitionCartridge();
	}
	@Override
	public ArrayList<Action> getActions(Actor actor,GameMap map, Gun gun) {
	
	ArrayList<Action> actions = new ArrayList<Action>();
	
	actions.add(new ChooseShootTargetAction(this));
	actions.add(new ChooseAimAction(this));
	
	
	// Check if the actor has ammo and then add a reload action if they do.
	Iterator<Item> iter= actor.getInventory().iterator();
	while (iter.hasNext()) {
		Item item = iter.next();
		if (item instanceof AmmunitionCartridge) {
			actions.add(new ReloadAction((AmmunitionCartridge) item,this));
		}
	}
	actions.add(new DoNothingAction());
	return actions;

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
	
	/**
	 * increases the level of aim the gun
	 */
	public void increaseAimLevel() {
		this.aimLevel+=1;
	}
	
	/**
	 * Gets all the tagerts in the map for the gun to aim at. Only if they arent the same
	 * zombie capability
	 * @param actor - the actor that is holding the gun
	 * @param map - the map to scan for other actors
	 * @param gun - the gun that is aiming
	 * @return array of actors that are possible to target
	 */
	public ArrayList<Actor> getTargets(Actor actor, GameMap map, SniperRifle gun) {
		ArrayList<Actor> targets = new ArrayList<Actor>();
		NumberRange x =map.getXRange();
		NumberRange y = map.getYRange();
		for (int xcoord : x) {
			for (int ycoord : y) {
				Location loc =map.at(xcoord, ycoord);
				if (map.isAnActorAt(loc)){
					Actor a = map.getActorAt(loc);
					if (actor.hasCapability(ZombieCapability.ALIVE) && a.hasCapability(ZombieCapability.UNDEAD)){
						targets.add(a);
					}
					
				}
			}
		}
		System.out.println("HERE "+ targets);

		return targets;
	}
	@Override
	public String getHeader() {
		String output="Ammo: ";
		for (int i =0;i<ammo.getBulletCount();i++) {
			output+=">";
		}
		return output;
	}
	
	

}
