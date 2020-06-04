package game;
/**
 * Class for farmers, those that grow the food in the game.
 * 
 * A farmer can sow, harvest, or fertalise a crop. They get one hoe each.
 */
public class Farmer extends Human {
	/**
	 * Constructor
	 * 
	 * @param name of the farmer
	 */
	public Farmer(String name) {
		super(name,'f',100);

		behaviours.add(new PickupWeaponBehaviour());
		behaviours.add(new AttackBehaviour(ZombieCapability.UNDEAD));
		behaviours.add(new FarmBehaviour());
		behaviours.add(new HuntBehaviour(Crop.class, 10));
		behaviours.add(new WanderBehaviour());
		addItemToInventory(new Hoe());
	}

	/**
	 * The protected constructor can be used to create subtypes
	 * of Farmer.
	 * 
	 * @param name the Farmer's's display name
	 * @param displayChar character that will represent the Farmer in the map 
	 * @param hitPoints amount of damage that the Farmer can take before it dies
	 */
	public Farmer(String name, char displayChar, int hitPoints) {
		super(name, displayChar, hitPoints);

	}

}