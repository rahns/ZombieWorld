package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
/**
 * Action that is used to relaod a gun
 * @author ariehendrikse
 *
 */
public class ReloadAction extends Action {
	
	private AmmunitionCartridge ammo;
	private Gun gun;
	/**
	 * Constructor for the class.
	 * @param ammo - ammo that is to be put into the gun
	 * @param gun - the gun to be reloaded
	 */
	public ReloadAction(AmmunitionCartridge ammo, Gun gun) {
		this.gun=gun;
		this.ammo=ammo;
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		gun.reload(ammo);
		actor.removeItemFromInventory(ammo);
		return actor + " reloads " +gun+" with " +ammo.getBulletCount()+" bullets";
	}

	@Override
	public String menuDescription(Actor actor) {

		return "Reload with "+ammo.getBulletCount()+" bullets";
	}

}
