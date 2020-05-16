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
	private Behaviour[] behaviours = {
			new AttackBehaviour(ZombieCapability.ALIVE),
			new HuntBehaviour(Human.class, 10),
			new WanderBehaviour()
	};
	private GameMap map;
	private Random rand = new Random();
	private boolean canMoveThisTurn = false;
	private List<Limb> limbs;
	private static final int PUNCH_PROBABILITTY_CONSTANT = 25;

	public Zombie(String name, GameMap gameMap) {
		super(name, 'Z', 100, ZombieCapability.UNDEAD);
		
		limbs = new ArrayList<>();
		// All actors start with 2 arms and 2 legs
		limbs.add(new Arm());
		limbs.add(new Arm());
		limbs.add(new Leg());
		limbs.add(new Leg());
		
		hitProbability=50;
		
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
		
		int punchChance = PUNCH_PROBABILITTY_CONSTANT * getLimbCount(Arm.class); // 50, 25 and 0 chance of punching if there are 2, 1, and 0 arms respectively
		if (rand.nextInt(100) < punchChance) {
			return new IntrinsicWeapon(10, "punches");
		}
		else {
			// return bite attack here
			return new IntrinsicWeapon(0, "placeholder"); // this is only here to make the program work until bite is added.
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
			int legCount = getLimbCount(Leg.class);
			boolean hasPreviouslyLostLeg = false; // Used so that on the turn a zombie looses its leg, 
												  // it doesn't change canMoveThisTurn and move straight away
			Action action = behaviour.getAction(this, map);
			if (action != null) {
				if (action instanceof MoveActorAction) {
					if (legCount == 2 || (legCount == 1 && canMoveThisTurn)) {
						canMoveThisTurn = false;
					}
					else {
						// Move to the next iteration and skip this action, as it is a move action and the Zombie shouldn't move
						continue;
					}
				}
				// If this action isn't a move action, the zombie can move next turn:
				if (!(action instanceof MoveActorAction) && legCount != 2) {
					if (!hasPreviouslyLostLeg) {
						// This is still the turn where the zombie lost its first leg so don't change canMoveThisTurn
						hasPreviouslyLostLeg = true;
					}
					else {
						canMoveThisTurn = true;
					}
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
	
}
