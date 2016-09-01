package drop;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import player.Player;

//Drop que agrega 5 balas.
public class MoreBullets extends Drop {
	private static final int BULLETS = 5;
	private Sound reloadsound;

	public MoreBullets() throws SlickException {
		super(new Image("res/sprites/misc/cartridge.png"), false);
		reloadsound = new Sound("res/sound/misc/reload.ogg");
	}
	
	@Override
	public void effect(Player player) {
		player.setBullets(BULLETS);
		reloadsound.play();
		obtained = true;
	}

}
