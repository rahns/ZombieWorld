package game;

import java.util.ArrayList;
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
	
	private static final int PLAYER_HEALTH = 300;

	public static void main(String[] args) {
		World world = new ZombieWorld(new Display());

		FancyGroundFactory groundFactory = new FancyGroundFactory(new Dirt(), new Fence(), new Tree(), new Door());
		
		List<String> mainMapInput = Arrays.asList(
		"......................................................................##########",
		"......................................................................#........#",
		"....................................##########.................................#",
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
		"..............................#########.........######..........................",
		"............+++.......................#.........#...............................",
		".............+++++....................#.........#...............................",
		"...............++........................................+++++..................",
		".............+++....................................++++++++....................",
		"............+++.......................................+++.......................",
		"................................................................................",
		".........................................................................++.....",
		"........................................................................++.++...",
		"......................................#####_#####........................++++...",
		"..........#.......#...................#.........#.........................++....",
		"..........#########...................###########...............................");
		
		List<String> townMapInput = Arrays.asList(
		".........................................",
		".........................................",
		"....###########..........................",
		"....#.........#..........................",
		"....#####_#####..########................",
		"....#.........#..#.......................",
		"....#.........#..#.......#...............",
		"....#.........####.......#...............",
		"....#....................#..........++++.",
		"....######################........+++++..",
		"..................................+++....",
		".........................................");
		
		
		// Create main map
		GameMap mainMap = new GameMap(groundFactory, mainMapInput);
		world.addGameMap(mainMap);
		
		// Create town map
		GameMap townMap = new GameMap(groundFactory, townMapInput);
		world.addGameMap(townMap);
		
		// Create Mambo Marie's void
		List<String> mambosVoidString = Arrays.asList(".");
		GameMap mambosVoid = new GameMap(groundFactory, mambosVoidString);
		world.addGameMap(mambosVoid);
		
		// Create Mambo Marie
		mambosVoid.addActor(new MamboMarie(mambosVoid.at(0, 0), mainMap.at(79, 13)), mambosVoid.at(0, 0));
				
		// Place player on main map
		Actor player = new Player("Player", '@', PLAYER_HEALTH);
		world.addPlayer(player, mainMap.at(43, 15));
		mainMap.at(43,16).addItem(new SniperRifle());
//		townMap.moveActor(player, townMap.at(9, 6));  // Uncomment to spawn player in the town (for testing)
		
	    // Place some random humans
		String[] humans = {"Little Rock", "Tank Dempsey", "Vicente", "Andrea",
				"Elina", "Winter", "Clem", "Tallahassee", "Jaquelyn"};
		String[] farmers = {"Farmer Old McDonald", "Farmer Wendy", "Farmer Jones of Manor Farm", "Farmer Dwight Schrute"};
		int x, y;
		for (String name : humans) {
			do {
				x = (int) Math.floor(Math.random() * 20.0 + 30.0);
				y = (int) Math.floor(Math.random() * 7.0 + 5.0);
			} 
			while (mainMap.at(x, y).containsAnActor());
			mainMap.at(x,  y).addActor(new Human(name));	
		}
		
		for (String name : farmers) {
			do {
				x = (int) Math.floor(Math.random() * 20.0 + 30.0);
				y = (int) Math.floor(Math.random() * 7.0 + 5.0);
			} 
			while (mainMap.at(x, y).containsAnActor());
			mainMap.at(x,  y).addActor(new Farmer(name));	
		}
		// Place lonely Steve in the top right corner:
		mainMap.at(75, 1).addActor(new Farmer("Lonely Farmer Steve"));
		
		// place a simple weapon
		mainMap.at(74, 20).addItem(new Plank());
		
		// place a train to town on the main map
		mainMap.at(43, 23).addItem(new Vehicle("train", '*', townMap.at(20, 0), "town"));
		// place a train from town back to the main map
		townMap.at(20, 0).addItem(new Vehicle("train", '*', mainMap.at(43, 23), "the compound"));
		
		// Add guns to town map
		townMap.at(11, 3).addItem(new SniperRifle());
		townMap.at(7, 3).addItem(new Shotgun());
		
		// Add shop (for bonus marks)
		ArrayList<Product> products = new ArrayList<>();
		products.add(new Product(new Food(), 1));
		products.add(new Product(new ZombieMace(), 1));
		products.add(new Product(new ZombieClub(), 1));
		products.add(new Product(new Hoe(), 2));
		products.add(new Product(new Plank(), 4));
		mainMap.at(14, 23).setGround(new Shop("Wallmart", 'W', products));
		
		// Add zombies to the main map
		mainMap.at(30, 20).addActor(new Zombie("Groan", mainMap));
		mainMap.at(30,  18).addActor(new Zombie("Boo", mainMap));
		mainMap.at(10,  4).addActor(new Zombie("Uuuurgh", mainMap));
		mainMap.at(50, 18).addActor(new Zombie("Mortalis", mainMap));
		mainMap.at(1, 10).addActor(new Zombie("Gaaaah", mainMap));
		mainMap.at(62, 4).addActor(new Zombie("Aaargh", mainMap));	
		mainMap.at(61, 12).addActor(new Zombie("Zoombie", mainMap));	
		mainMap.at(70, 12).addActor(new Zombie("Zomzilla", mainMap));	
		mainMap.at(65, 8).addActor(new Zombie("Creeper", mainMap));	
		mainMap.at(67, 9).addActor(new Zombie("Zombie Edward Richtofen", mainMap));	
		mainMap.at(6, 12).addActor(new Zombie("Bombie", mainMap));	
		mainMap.at(2, 12).addActor(new Zombie("Zombie Micheal Jackson", mainMap));	
		mainMap.at(5, 20).addActor(new Zombie("Zombie Meg", mainMap));	
		mainMap.at(40, 1).addActor(new Zombie("Zombie Megger", mainMap));	
		mainMap.at(41, 1).addActor(new Zombie("Zombie Megosauraus", mainMap));	
		mainMap.at(42, 1).addActor(new Zombie("Zombie Megania", mainMap));	
		mainMap.at(43, 1).addActor(new Zombie("Zombie Megatron", mainMap));		

		// Add zombies to the town map
		townMap.at(10, 6).addActor(new Zombie("Zombie Mayor Duncan", townMap));
		townMap.at(8, 7).addActor(new Zombie("Zombie Bob Dylan", townMap));
		townMap.at(19, 6).addActor(new Zombie("Zombie Rupert Murdoch", townMap));
		townMap.at(13, 0).addActor(new Zombie("Zombie Kanye", townMap));
		townMap.at(33, 5).addActor(new Zombie("Zombie Rick", townMap));
		townMap.at(25, 6).addActor(new Zombie("Zombie Harold Holt", townMap));		
		
		world.run();
	}
}
