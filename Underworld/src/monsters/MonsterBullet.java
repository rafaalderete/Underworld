package monsters;
import java.util.ArrayList;

import org.newdawn.slick.geom.Rectangle;

import map.Obstacle;

//Balas de los monstruos con su velocidad y ángulo.
public class MonsterBullet {
	
	private int speed;
	private Rectangle hitzone;
	double angle;
	private boolean collided = false; //Si la bala chocó, ya sea el area jugable, obstaculo o jugador.

	//Constructor para cuando las balas son en dirección al jugador.
	public MonsterBullet(Rectangle hitzone, float targetx, float targety, int speed) {
		this.hitzone = hitzone;
		this.speed = speed;
		angle = Math.toRadians(Math.atan2(targety - hitzone.getCenterY(), targetx - hitzone.getCenterX())* 180 / Math.PI);
	}
	
	//Constructor para cuando las balas en direcciones especificas(ya se le da un ángulo).
	public MonsterBullet(Rectangle hitzone, double angle, int speed) {
		this.hitzone = hitzone;
		this.speed = speed;
		this.angle = angle;
	}
	
	//Actualiza la posicion de la bala.
	public void updateBullet(int delta) {
		if (!collided) {
			float x = (float) (hitzone.getX() + Math.cos((angle)) * ((speed)*((float)(delta)/1000)));
			float y = (float) (hitzone.getY() + Math.sin((angle)) * ((speed)*((float)(delta)/1000)));
			hitzone.setLocation(x, y);
		}
	}
	
	//Verifica que se haya salido del area jugable y la colisión con los obstaculos.
	//Es usado por los monstruos que no sean boss.
	public void checkCollision(Rectangle area, ArrayList<Obstacle>obstacles) {
		if ((!area.intersects(hitzone)))
			collided = true;
		for (int j = 0; j < obstacles.size(); j++)
			if (obstacles.get(j).getSprite() == 1)
				if (obstacles.get(j).getRectangle().intersects(hitzone))
					collided = true;
	}
	
	public Rectangle getHitZone() {
		return hitzone;
	}
	
	public boolean isCollided() {
		return collided;
	}
	
	public void setCollided(boolean collided) {
		this.collided = collided;
	}

}
