package game;


import edu.monash.fit2099.engine.Action;

import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.DoNothingAction;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Weapon;

/**
 * Class representing an ordinary human.
 * 
 * 
 * @author ram
 *
 */
public class Human extends ZombieActor {
	/**
	 * The default constructor creates default Humans
	 * 
	 * @param name the human's display name
	 */
	public Human(String name) {
		super(name, 'H', 50, ZombieCapability.ALIVE, 70);
		//TODO possibly make humans run until they are out of options then attack
		//TODO add runBehaviour for humans. Will calculate if the area has a total distance away from zombies that is good 
		behaviours.add(new EatBehaviour());
		behaviours.add(new PickupWeaponBehaviour());
		behaviours.add(new AttackBehaviour(ZombieCapability.UNDEAD));
		behaviours.add(new HarvestBehaviour());
		behaviours.add(new HuntBehaviour(Food.class, 6));
		behaviours.add(new HuntBehaviour(Weapon.class, 6)); // Reduced to 6 so they don't all run for the plank
		behaviours.add(new WanderBehaviour());
	}
	
	/**
	 * The protected constructor can be used to create subtypes
	 * of Human, such as the Player
	 * 
	 * @param name the human's display name
	 * @param displayChar character that will represent the Human in the map 
	 * @param hitPoints amount of damage that the Human can take before it dies
	 */
	protected Human(String name, char displayChar, int hitPoints) {
		super(name, displayChar, hitPoints, ZombieCapability.ALIVE, 70);
		}
	
	@Override
	public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
		// FIXME humans are pretty dumb, maybe they should at least run away from zombies?
		for (Behaviour behaviour : behaviours) {
			Action action = behaviour.getAction(this, map);
			if (action != null) {
				return action;
			}
		}
		return new DoNothingAction();
	}
	
	public void addBehaviour(Behaviour behaviour) {
		this.behaviours.add(behaviour);
	}
}
