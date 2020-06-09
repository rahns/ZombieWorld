package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;

public class EndLifeAction extends Action {


	@Override
	public String execute(Actor actor, GameMap map) {
		actor.hurt(((ZombieActor) actor).getMaxHitpoints());
		if (!actor.isConscious()) {
			boolean shouldRise = false;
			// Check if target was human and killed by a zombie:
			if (actor.hasCapability(ZombieCapability.ALIVE) && actor.hasCapability(ZombieCapability.UNDEAD)) {
				shouldRise = true;
			}
			Corpse corpse = new Corpse(actor.toString(), shouldRise, map);
			map.locationOf(actor).addItem(corpse);
			
			Actions dropActions = new Actions();
			for (Item item : actor.getInventory())
				dropActions.add(item.getDropAction());
			for (Action drop : dropActions)		
				drop.execute(actor, map);
			map.removeActor(actor);	
		}

		return actor+ " took the easy way out ";
	}

	@Override
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return "End it all";
	}
	@Override
	public String hotkey() {
		return "+";
	}

}
