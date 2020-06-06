package game;

import java.util.Iterator;

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
		Iterator<Actor> iter=actorLocations.iterator();
		
		while(iter.hasNext()){
			Actor curr=iter.next();
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
	

}
