package game;


import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.World;

/**
 * The world class for the zombie game
 * @author ariehendrikse
 *
 */
public class ZombieWorld extends World {
	// Game state attributes;
	private boolean containsPlayer;
	private boolean containsMambo;
	private boolean containsZombie;
	private boolean containsHuman;

	/**
	 * ZombieWorld constructor
	 * @param display the display to printing messages
	 */
	public ZombieWorld(Display display) {
		super(display);
	}
	
	@Override
	protected boolean stillRunning() {
		updateGameState();
		return containsPlayer && (containsMambo || containsZombie) && containsHuman;
	}
	
	@Override 
	public String endGameMessage() {
		updateGameState();
		if (!(containsMambo || containsZombie)) {
			return "Mambo Marie and all the zombies are dead! Player wins.";
		}
		else if (!(containsHuman)) {
			return "All humans are dead! Game over.";
		}
		
		return "Player died. Game Over.";
	}
	
	/**
	 * Updates the private attributes about the game's current state
	 */
	private void updateGameState() {
		containsPlayer=actorLocations.contains(player);
		containsMambo=false;
		containsZombie=false;
		containsHuman=false;
		
		for (Actor curr : actorLocations) {
			if (curr instanceof Zombie){
				containsZombie=true;
			}
			else if (curr instanceof MamboMarie) {
				containsMambo=true;
			}
			else if(curr instanceof Human && !(curr instanceof Player)) {
				containsHuman=true;
			}
		}
	}
}
