package player;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import map.Obstacle;

//Manejara todas las zonas de ataque de las balas del jugador en pantalla, las dibujará y las moverá.
public class BulletsManager {
	
	private static final int BULLETSPRITEDIMENSION = 9, DIRU = 0, DIRR = 1, DIRD = 2, DIRL = 3;
	private ArrayList<Bullet> bullets;
	private Rectangle area;
	private SpriteSheet imbullet;

	public BulletsManager(Rectangle area) throws SlickException {
		this.area = area;
		imbullet = new SpriteSheet("res/sprites/misc/bullet.png", BULLETSPRITEDIMENSION, BULLETSPRITEDIMENSION);
		bullets = new ArrayList<Bullet>();
	}
	
	//Desde player se agregan las balas.
	public void addBullet(float x, float y, int dir) {
		bullets.add(new Bullet(x, y, dir));
	}
	
	//Dibuja el sprite de las balas en base a la zona de ataque de cada bala y su dirección.
	public void draw(Graphics g) {
		Color trans = new Color(0f,0f,0f,0.2f);
	    g.setColor(trans);
		for (int i = 0; i < bullets.size(); i++) {
			if (bullets.get(i).getDir() == DIRU)
				imbullet.getSprite(DIRU, 0).draw(bullets.get(i).getHitZone().getX()-1, bullets.get(i).getHitZone().getY());
			else
				if (bullets.get(i).getDir() == DIRR)
					imbullet.getSprite(DIRR, 0).draw(bullets.get(i).getHitZone().getX(), bullets.get(i).getHitZone().getY()-40);
				else
					if (bullets.get(i).getDir() == DIRD )
						imbullet.getSprite(DIRD, 0).draw(bullets.get(i).getHitZone().getX()-1, bullets.get(i).getHitZone().getY());
					else
						if (bullets.get(i).getDir() == DIRL)
							imbullet.getSprite(DIRL, 0).draw(bullets.get(i).getHitZone().getX(), bullets.get(i).getHitZone().getY()-40);
			g.fillRect(bullets.get(i).getHitZone().getX(), bullets.get(i).getHitZone().getY(), bullets.get(i).getHitZone().getWidth(), bullets.get(i).getHitZone().getHeight()); //Sombra de la bala.
		}
	}
	
	//Movimiento de las balas. Cuando salen de las dimensiones del area jugable o chocan con un sprite de piedra,
	//las elimina de la lista.
	public void update(int delta, ArrayList<Obstacle> obstacles) {
		boolean ban;
		for (int i = 0; i < bullets.size(); i++){
			ban = false;
			bullets.get(i).updateBullet(delta);
			if (!(bullets.get(i).getHitZone().intersects(area)))
				ban = true;
			for (int j = 0; j < obstacles.size(); j++) {
				if (obstacles.get(j).getSprite() == 1) {
					if (bullets.get(i).getHitZone().intersects(obstacles.get(j).getRectangle()))
						ban = true;
				}
			}
			if (ban)
				bullets.remove(i);
			if (bullets.size() == 0)
				break;	
		}	
	}
	
	public ArrayList<Bullet> getBullets() {
		return bullets;
	}

	public void removeBullet(int bullet) {
		bullets.remove(bullet);
	}

	//Elimina todas las balas.
	public void clearBullets () {
		bullets.clear();
	}
	
}