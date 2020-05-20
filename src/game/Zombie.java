package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.DoNothingAction;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.IntrinsicWeapon;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Weapon;
import edu.monash.fit2099.engine.Location;
import edu.monash.fit2099.engine.MoveActorAction;

/**
 * A Zombie.
 * 
 * This Zombie is pretty boring.  It needs to be made more interesting.
 * 
 * @author ram
 *
 */
public class Zombie extends ZombieActor {

	private GameMap map;
	private Random rand = new Random();
	private boolean canMoveThisTurn = false;
	private List<Limb> limbs;
	private static final int PUNCH_PROBABILITTY_CONSTANT = 25;

	public Zombie(String name, GameMap gameMap) {
		super(name, 'Z', 100, ZombieCapability.UNDEAD, 50);
		
		behaviours.add(new AttackBehaviour(ZombieCapability.ALIVE));
		behaviours.add(new HuntBehaviour(Human.class, 15));
		behaviours.add(new WanderBehaviour());
		
		limbs = new ArrayList<>();
		// All actors start with 2 arms and 2 legs
		limbs.add(new Arm());
		limbs.add(new Arm());
		limbs.add(new Leg());
		limbs.add(new Leg());
		
		map = gameMap;
	}
	
	@Override
	public Weapon getWeapon() {
		for (Item item : inventory) {
			if (item.asWeapon() != null)
				// May drop the weapon when using it (depending on arm count). If dropped, just use intrinsic weapon:
				if (getLimbCount(Arm.class) == 0 || (getLimbCount(Arm.class) == 1 && rand.nextBoolean())) {
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
		//TODO make weopons array iterate through weapons instead of using get intrinsic
		// this will allow for less hard coding.
		int punchChance = PUNCH_PROBABILITTY_CONSTANT * getLimbCount(Arm.class); // 50, 25 and 0 chance of punching if there are 2, 1, and 0 arms respectively
		if (rand.nextInt(100) < punchChance) {
			return new Punch();
		}
		else {
			//TODO this feels hacky. ANy way to implment the healing into weopon?
			heal(10);
			return new Bite(); 
		}
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
		// Zombie picking up a weapon doesn't count as its turn
		
		// Then:
		for (Behaviour behaviour : behaviours) {
			Action action = behaviour.getAction(this, map);
			if (action != null) {
				if (action instanceof MoveActorAction) {
					canMoveThisTurn = false; // Update for next turn
				}
				return action;
			}
		}
		// Zombie is about to do nothing, so can move next turn:
		canMoveThisTurn = true;
		return new DoNothingAction();	
	}
	
	/**
	 * Do some damage to the current Zombie. 
	 * 
	 * Has a 25% chance of knocking off one of the Zombie's arms or legs.
	 *
	 * @param points number of hitpoints to deduct.
	 */
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
			int selected = rand.nextInt(limbs.size());
			System.out.println("One of " + this.name + "'s " + limbs.get(selected).toString() + "s flung off.");
			dropItem(limbs.get(selected));
			limbs.remove(selected);
		}
	}
	
	// Custom dropItem method used instead of DropItemAction as zombies need 
	// to drop limbs in adjacent locations, not where they're standing
	private void dropItem(Item item) {
		Location dropLocation = map.locationOf(this);
		int selectedExit = rand.nextInt(dropLocation.getExits().size());
		dropLocation.getExits().get(selectedExit).getDestination().addItem(item);
	}
	
	private int getLimbCount(Class<?> limbType) {
		int tally = 0;
		for (Limb aLimb: limbs) {
			// Check if the selected limb is an instance of the parameter limbType:
			if (limbType.isInstance(aLimb)) {
				tally += 1;
			}
		}
		return tally;
	}
	
	/**
	 * Find out if the Zombie can move this turn
	 * @return a boolean, true if the Zombie can move, false otherwise
	 */
	public boolean canMove() {
		int legCount = getLimbCount(Leg.class);
		if (legCount == 2 || (legCount == 1 && canMoveThisTurn)) {
			return true;
		}
		return false;
	}
}
