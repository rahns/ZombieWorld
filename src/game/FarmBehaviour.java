package game;

import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Exit;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Ground;
import edu.monash.fit2099.engine.Location;


public class FarmBehaviour implements Behaviour {
	private Actor target;
	int sowProbability = 33;
	int fertaliseProbability =50;
	private Random rand = new Random();
	public FarmBehaviour(Actor subject) {
		this.target = subject;
	}

	@Override
	public Action getAction(Actor actor, GameMap map) {
		Action action=null;
		
		if(!map.contains(target) || !map.contains(actor))
			return null;
		
		Location here = map.locationOf(actor);
		Ground ground =here.getGround();
		action=(farmDecision(here,ground));
		if (action != null) {
			return action;
		}
		for (Exit exit : here.getExits()) {
			Location destination = exit.getDestination();
			ground = destination.getGround();
			action=(farmDecision(destination,ground));
			if (action != null) {
				return action;
			}
		}
		return action;
			
	}
	private Action farmDecision(Location destination, Ground ground) {
		if (ground instanceof Dirt) {
			return new SowAction(destination);
		}
		else if (ground instanceof Crop) {
			if ((((Crop) ground).isRipe()) & (rand.nextInt(100)<sowProbability)) {
				return new HarvestAction(destination);
			} 
			if (rand.nextInt(100)>fertaliseProbability) {
				return new FertaliseAction(destination);
			}
		}
		return null;
		
	}
}
