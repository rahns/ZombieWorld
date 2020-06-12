package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.DoNothingAction;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Location;
import edu.monash.fit2099.engine.NumberRange;

/**
 * A class representing a sniper rifle
 * @author ariehendrikse
 *
 */
public class SniperRifle extends Gun {
	private Actor target;	
	
	/**
	 * SniperRifle constructor
	 */
	public SniperRifle() {
		super("Sniper Rifle", 'R', 15, "snipes");
		allowableActions.add(new UseGunAction(this));
		this.ammo=new AmmunitionCartridge();
	}
	@Override
	public ArrayList<Action> getActions(Actor actor,GameMap map) {
	
		ArrayList<Action> actions = new ArrayList<Action>();
		
		if (ammo.getBulletCount() != 0) {
			actions.add(new ChooseShootTargetAction(this));
			actions.add(new ChooseAimAction(this));
		}

		// Check if the actor has ammo and then add a reload action if they do.
		for (Item item : actor.getInventory()) {
			if (item instanceof AmmunitionCartridge) {
				actions.add(new ReloadAction((AmmunitionCartridge) item,this));
				break;
			}
		}
		actions.add(new DoNothingAction());
		return actions;

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
	public void aim(Actor target, boolean increaseLevel) {
		if (target==this.target) {
			if (increaseLevel) {
				this.aimLevel+=1;
			}
		}
		else
		{
			this.aimLevel=0;
		}	

		this.target=target;
	}
	
	/**
	 * Gets all the targets in the map for the gun to aim at. Only if they arent the same
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
		return targets;
	}
	
	/**
	 * Gets a header to show above its actions in a menu. Shows the ammo count
	 * @return a string to be shown as a header to its actions menu
	 */
	@Override
	public String getHeader() {
		String output="Ammo: ";
		for (int i =0;i<ammo.getBulletCount();i++) {
			output+=">";
		}
		if (ammo.getBulletCount() == 0) {
			output += "Empty";
		}
		return output;
	}
	
	

}
