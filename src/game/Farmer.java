package game;

public class Farmer extends Human {
	private Behaviour farm = new FarmBehaviour();

	public Farmer(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public Farmer(String name, char displayChar, int hitPoints) {
		super(name, displayChar, hitPoints);
		// TODO Auto-generated constructor stub
	}

}
