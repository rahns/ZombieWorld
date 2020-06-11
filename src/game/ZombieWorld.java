package game;


import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.World;

public class ZombieWorld extends World {
	// Game state attributes;
	private boolean containsPlayer;
	private boolean containsMambo;
	private boolean containsZombie;
	private boolean containsHuman;

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
			return "Player wins";
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
