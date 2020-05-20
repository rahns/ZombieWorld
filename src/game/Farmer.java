package game;

public class Farmer extends Human {

	public Farmer(String name) {
		super(name,'f',80);

		behaviours.add(new FarmBehaviour());
		behaviours.add(new HuntBehaviour(Crop.class,10));
		behaviours.add(new HuntBehaviour(Dirt.class,10));
		behaviours.add(new WanderBehaviour());
		addItemToInventory(new Hoe());
	}

	public Farmer(String name, char displayChar, int hitPoints) {
		super(name, displayChar, hitPoints);

	}

}