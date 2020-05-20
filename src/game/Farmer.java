package game;

public class Farmer extends Human {

	public Farmer(String name) {
		super(name);
		behaviours.add(new FarmBehaviour());
	}

	public Farmer(String name, char displayChar, int hitPoints) {
		super(name, displayChar, hitPoints);
		// TODO Auto-generated constructor stub
	}

}
