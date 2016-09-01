package drop;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import player.Player;

//Drop que agrega 1 vida.
public class Life extends Drop {
	private Sound lifesound;

	public Life() throws SlickException {
		super(new Image("res/sprites/misc/fillheart.png"), false);
		lifesound = new Sound("res/sound/misc/lifeup.ogg");
	}
	
	@Override
	public void effect(Player player) {
		if (player.getLife() < player.getMaxLife()) {
			lifesound.play();
			player.setLife(player.getLife()+1);
			obtained = true;
		}
		else
			obtained = false;
	}

}
