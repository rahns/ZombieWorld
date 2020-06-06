package game;

import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Menu;

public interface MenuAction {
	public abstract Menu getMenu(GameMap map);

}
