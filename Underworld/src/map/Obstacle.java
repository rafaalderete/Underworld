package map;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

//Obstaculos con su tipo(sprite a dibujar en base al spritesheet, zona de colisión y posición.
public class Obstacle {
	
	private static final int OBSTACLEDIMENSION = 80;
	private int sprite;
	private Rectangle pos;

	public Obstacle(int sprite, Point pos) {
		this.sprite = sprite;
		this.pos = new Rectangle(pos.getX(), pos.getY(), OBSTACLEDIMENSION, OBSTACLEDIMENSION);
	}

	public int getSprite() {
		return sprite;
	}

	public Rectangle getRectangle() {
		return pos;
	}

}