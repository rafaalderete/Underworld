package player;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

//Zona de ataque y dirección en la que debe ir cada bala del jugador.
public class Bullet {
	
	private static final int BULLETSPEED = 2000, HTZWIDTH = 10, HTZHEIGHT = 6, DIRR = 1, DIRL = 3, DIRU = 0 , DIRD = 2;
	private int dir; //Dirección de la bala.
	private Rectangle hitzone;
	private Point speed;

	public Bullet(float x, float y, int dir) {
		if ((dir == DIRR) || (dir == DIRL))
			hitzone = new Rectangle(x, y, HTZWIDTH, HTZHEIGHT);
		else
			hitzone = new Rectangle(x, y, HTZHEIGHT, HTZWIDTH);
		this.dir = dir;
		if (this.dir == DIRU)
			speed = new Point(0, -BULLETSPEED);
		else
			if (dir == DIRR)
				speed = new Point(BULLETSPEED, 0);
			else
				if (dir == DIRD)
					speed = new Point(0, BULLETSPEED);
				else
					if (dir == DIRL) 
						speed = new Point(-BULLETSPEED, 0);		
	}
	
	//Movimiento de la bala dependiendo de la dirección.
	public void updateBullet(int delta) {
		float x = hitzone.getX() + speed.getX() * ((float)delta/1000);
		float y = hitzone.getY() + speed.getY() * ((float)delta/1000);
		hitzone.setLocation(x, y);
	}
	
	public int getDir() {
		return dir;
	}
	
	public Rectangle getHitZone() {
		return hitzone;
	}
	
}
