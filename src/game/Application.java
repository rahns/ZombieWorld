package game;

import java.util.Arrays;
import java.util.List;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.FancyGroundFactory;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.World;

/**
 * The main class for the zombie apocalypse game.
 *
 */
public class Application {

	public static void main(String[] args) {
		World world = new World(new Display());

		FancyGroundFactory groundFactory = new FancyGroundFactory(new Dirt(), new Fence(), new Tree());
		
		List<String> map = Arrays.asList(
		"......................................................................##########",
		"......................................................................#........#",
		"....................................##########........................#........#",
		"..........................###########........#####....................##########",
		"............++...........##......................########.......................",
		"..............++++.......#..............................##......................",
		".............+++...+++...#...............................#......................",
		".........................##..............................##.....................",
		"..........................#...............................#.....................",
		".........................##...............................##....................",
		".........................#...............................##.....................",
		".........................###..............................##....................",
		"...........................####......................######.....................",
		"..............................#########.........#####...........................",
		"............+++.......................#.........#...............................",
		".............+++++....................#.........#...............................",
		"...............++........................................+++++..................",
		".............+++....................................++++++++....................",
		"............+++.......................................+++.......................",
		"................................................................................",
		".........................................................................++.....",
		"........................................................................++.++...",
		".........................................................................++++...",
		"..........................................................................++....",
		"................................................................................");
		GameMap gameMap = new GameMap(groundFactory, map );
		world.addGameMap(gameMap);
		
		Actor player = new Player("Player", '@', 100);
		world.addPlayer(player, gameMap.at(42, 15));
		
	    // Place some random humans
		String[] humans = {"Little Rock", "Tank Dempsey", "Vicente", "Andrea",
				"Elina", "Winter", "Clem", "Tallahassee", "Jaquelyn"};
		String[] farmers = {"Farmer Old McDonald", "Farmer Wendy"};
		int x, y;
		for (String name : humans) {
			do {
				x = (int) Math.floor(Math.random() * 20.0 + 30.0);
				y = (int) Math.floor(Math.random() * 7.0 + 5.0);
			} 
			while (gameMap.at(x, y).containsAnActor());
			gameMap.at(x,  y).addActor(new Human(name));	
		}
		
		for (String name : farmers) {
			do {
				x = (int) Math.floor(Math.random() * 20.0 + 30.0);
				y = (int) Math.floor(Math.random() * 7.0 + 5.0);
			} 
			while (gameMap.at(x, y).containsAnActor());
			gameMap.at(x,  y).addActor(new Farmer(name));	
		}
		// Place lonely Steve in the top right corner:
		gameMap.at(75, 1).addActor(new Farmer("Lonely Farmer Steve"));
		
		// place a simple weapon
		gameMap.at(74, 20).addItem(new Plank());
		
		// FIXME: Add more zombies!
		gameMap.at(30, 20).addActor(new Zombie("Groan", gameMap));
		gameMap.at(30,  18).addActor(new Zombie("Boo", gameMap));
		gameMap.at(10,  4).addActor(new Zombie("Uuuurgh", gameMap));
		gameMap.at(50, 18).addActor(new Zombie("Mortalis", gameMap));
		gameMap.at(1, 10).addActor(new Zombie("Gaaaah", gameMap));
		gameMap.at(62, 4).addActor(new Zombie("Aaargh", gameMap));	
		gameMap.at(61, 12).addActor(new Zombie("Zoombie", gameMap));	
		gameMap.at(70, 12).addActor(new Zombie("Zomzilla", gameMap));	
		gameMap.at(65, 8).addActor(new Zombie("Creeper", gameMap));	
		gameMap.at(67, 9).addActor(new Zombie("Zombie Edward Richtofen", gameMap));	
		gameMap.at(6, 12).addActor(new Zombie("Bombie", gameMap));	
		gameMap.at(2, 12).addActor(new Zombie("Zombie Micheal Jackson", gameMap));	
		gameMap.at(5, 20).addActor(new Zombie("Zombie Meg", gameMap));	
		gameMap.at(40, 1).addActor(new Zombie("Zombie Megger", gameMap));	
		gameMap.at(41, 1).addActor(new Zombie("Zombie Megosauraus", gameMap));	
		gameMap.at(42, 1).addActor(new Zombie("Zombie Megania", gameMap));	
		gameMap.at(43, 1).addActor(new Zombie("Zombie Megatron", gameMap));		






		
		
		
		
		world.run();
	}
}
