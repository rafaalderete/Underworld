package drop;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import player.Player;

//Drop que rellena la vida y agrega 5 de máxima cantidad de vida.
public class MaxLife extends Drop {
	private Sound maxlifesound;

	public MaxLife() throws SlickException {
		super(new Image("res/sprites/misc/maxlife.png"), true);
		maxlifesound = new Sound("res/sound/misc/maxlife.ogg");
	}
	
	@Override
	public void effect(Player player) {
		maxlifesound.play();
		player.setMaxLife(player.getMaxLife()+5);
		player.setLife(player.getMaxLife());
		obtained = true;
	}
	
}