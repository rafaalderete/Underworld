package drop;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import player.Player;

//Drop que aumenta el daño de los ataques del jugador en 1. 
public class WeaponUpgrade extends Drop {
	private Sound upgradesound;

	public WeaponUpgrade() throws SlickException {
		super(new Image("res/sprites/misc/weaponupgrade.png"), true);
		upgradesound = new Sound("res/sound/misc/damageup.ogg");
	}
	
	@Override
	public void effect(Player player) {
		upgradesound.play();
		player.setBayonetAtk(player.getBayonetAtk()+1);
		player.setGunAtk(player.getGunAtk()+1);
		obtained = true;
	}

}
