package drop;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import player.Player;

//Drop que aumenta la velocidad en 50.
public class MoreSpeed extends Drop {
	private static final int SPEED = 50;
	private Sound bootssound;

	public MoreSpeed() throws SlickException {
		super(new Image("res/sprites/misc/boots.png"), false);
		bootssound = new Sound("res/sound/misc/boots.ogg");
	}
	
	@Override
	public void effect(Player player) {
		player.setAcceleration(player.getAcceleration()+SPEED);
		bootssound.play();
		obtained = true;
	}

}
