package game;

import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.DoNothingAction;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.IntrinsicWeapon;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.PickUpItemAction;
import edu.monash.fit2099.engine.Weapon;
import edu.monash.fit2099.engine.Location;

/**
 * A Zombie.
 * 
 * This Zombie is pretty boring.  It needs to be made more interesting.
 * 
 * @author ram
 *
 */
public class Zombie extends ZombieActor {
	private Behaviour[] behaviours = {
			new AttackBehaviour(ZombieCapability.ALIVE),
			new HuntBehaviour(Human.class, 10),
			new WanderBehaviour()
	};
	protected GameMap map;
	protected Random rand = new Random();
	// private boolean canMoveThisTurn = true;

	public Zombie(String name, GameMap gameMap) {
		super(name, 'Z', 100, ZombieCapability.UNDEAD);
		map = gameMap;
	}
	
	@Override
	public Weapon getWeapon() {
		for (Item item : inventory) {
			if (item.asWeapon() != null)
				// May drop the weapon when using it (depending on arm count). If dropped, just use intrinsic weapon:
				if (getArmCount() == 0 || (getArmCount() == 1 && rand.nextBoolean())) {
					removeItemFromInventory(item);
					dropItem(item);
					System.out.println(this.name + " dropped its weapon: " + item.toString());
					break;
				}
				return item.asWeapon();
		}
		return getIntrinsicWeapon();
	}

	@Override
	public IntrinsicWeapon getIntrinsicWeapon() {
		
		int punchChance = 25 * getArmCount(); // 50, 25 and 0 chance of punching if there are 2, 1, and 0 arms respectively
		if (rand.nextInt(100) < punchChance) {
			return new IntrinsicWeapon(10, "punches");
		}
		else {
			// return bite attack here
			return new IntrinsicWeapon(10, "punches"); // this is only here to make the program work until bite is added.
		}
	}
	
	private int getArmCount() {
		int armTally = 0;
		for (Limb aLimb: limbs) {
			if (aLimb.toString() == "arm") {
				armTally += 1;
			}
		}
		return armTally;
	}

	/**
	 * If a Zombie can attack, it will.  If not, it will chase any human within 10 spaces.  
	 * If no humans are close enough it will wander randomly.
	 * 
	 * @param actions list of possible Actions
	 * @param lastAction previous Action, if it was a multiturn action
	 * @param map the map where the current Zombie is
	 * @param display the Display where the Zombie's utterances will be displayed
	 */
	@Override
	public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
		// Execute any PickUpItemActions here
		
		// Then:
		for (Behaviour behaviour : behaviours) {
			Action action = behaviour.getAction(this, map);
			//TODO Zombies need to be able to pick up weapons as an action.
			// This will be in the actions collection
			if (action != null)
				return action;
		}
		return new DoNothingAction();	
	}
	
	@Override
	public void hurt(int points) {
		hitPoints -= points;
		// 25% chance of dropping a limb:
		if (rand.nextInt(100) < 25) {
			knockOffLimb();
		}
	}
	
	private void knockOffLimb() {
		if (limbs.size() != 0){
			int selected;
			// nextInt doesn't work with an input of 0, so when limbs.size() is 1, the selection is hard-coded to equal 0
			if (limbs.size() != 1) {
				selected = rand.nextInt(limbs.size()-1);
			}
			else {
				selected = 0;
			}
			System.out.println("One of " + this.name + "'s " + limbs.get(selected).toString() + "s flung off.");
			dropItem(limbs.get(selected));
			limbs.remove(selected);
		}
	}
	
	private void dropItem(Item item) {
		Location dropLocation = map.locationOf(this);
		int selectedExit = rand.nextInt(dropLocation.getExits().size()-1);
		dropLocation.getExits().get(selectedExit).getDestination().addItem(item);
	}
}
