package game;


import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.World;

public class ZombieWorld extends World {

	public ZombieWorld(Display display) {
		super(display);
	}
	
	@Override
	protected boolean stillRunning() {
		boolean containsPlayer=actorLocations.contains(player);
		boolean containsMambo=false;
		boolean containsZombie=false;
		boolean containsHuman=false;
		
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
		
		return containsPlayer && (containsMambo || containsZombie) && containsHuman;
	}
	
	@Override 
	public String endGameMessage() {
		boolean containsMambo=false;
		boolean containsZombie=false;
		boolean containsHuman=false;
		
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
		
		if (!(containsMambo || containsZombie)) {
			return "Player wins";
		}
		else if (!(containsHuman)) {
			return "All humans are dead! Game over.";
		}
		
		return "Player died. Game Over.";
	}
}
