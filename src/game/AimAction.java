package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

public class AimAction extends Action {
	Actor target;
	SniperRifle gun;

	public AimAction(Actor actor,SniperRifle gun) {
		this.target=actor;
		this.gun=gun;
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		String healthStat=((ZombieActor) actor).getHealthStatus();
		this.gun.aim(target,healthStat);
		System.out.println("What was passed was: "+healthStat);
		return actor+" aimed at " + target;
	}

	@Override
	public String menuDescription(Actor actor) {
		return "Aim";
	}

}
