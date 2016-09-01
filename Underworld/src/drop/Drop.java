package drop;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

import player.Player;

//Recompensas al limpiar cada zona. Superclase para los drops.
public class Drop {
	
	private final static Point ZONEDROPPOS = new Point(510, 340), BOSSDROPPOS = new Point(485, 310), SHADOWPOS = new Point(510, 355), //
			HITZONEBOSSDROP = new Point(500, 390), SMOKEZONEDROPPOS = new Point(500, 320), SMOKEBOSSDROPPOS = new Point(480, 300); 	  //Posiciones donde ubicar los drops.
	private final static int ZONESMOKEDIMENSION = 64, BOSSSMOKEDIMENSION = 140, SPRITEDURATION = 100;
	protected Image sprite;
	protected Rectangle hitzone;
	protected boolean dropboss, obtained = false, dropped = false, smokedraw = false; 
	protected SpriteSheet ssmoke;
	protected Animation smoke;
	
	public Drop(Image image, boolean dropboss) throws SlickException {
		ssmoke = new SpriteSheet("res/sprites/misc/smokedrop.png", ZONESMOKEDIMENSION, ZONESMOKEDIMENSION);
		smoke = new Animation(ssmoke, SPRITEDURATION);
		smoke.setLooping(false);
		this.sprite = image;
		this.dropboss = dropboss; //Dependiendo de dropboss cambiara el hitzone y la posicion(tanto de sprite como del humo) de cada drop.
		if (!dropboss)
			this.hitzone = new Rectangle(SHADOWPOS.getX(), SHADOWPOS.getY(), sprite.getWidth(), sprite.getHeight()/2);
		else 
			this.hitzone = new Rectangle(HITZONEBOSSDROP.getX(), HITZONEBOSSDROP.getY(), (float) (sprite.getWidth()/1.7), (float) (sprite.getHeight()/5));
	}
	
	//Se dibuja el sprite, la sombra y la animación de humo(solo una vez).
	public void draw (Graphics g) {
		if (!dropboss) {
			drawShadow(g);
			sprite.draw(ZONEDROPPOS.getX(), ZONEDROPPOS.getY());
			if (!smokedraw) {
				smoke.draw(SMOKEZONEDROPPOS.getX(), SMOKEZONEDROPPOS.getY(), ZONESMOKEDIMENSION, ZONESMOKEDIMENSION);
			}
		}
		else {
			sprite.draw(BOSSDROPPOS.getX(), BOSSDROPPOS.getY());
			if (!smokedraw) {
				smoke.draw(SMOKEBOSSDROPPOS.getX(), SMOKEBOSSDROPPOS.getY(), BOSSSMOKEDIMENSION, BOSSSMOKEDIMENSION);
			}
		}	
		if (smoke.isStopped())
			smokedraw = true;
		if (!dropped)
			dropped = true; // El drop ha aparecido en pantalla.
	}
	
	private void drawShadow (Graphics g) {
		Color trans = new Color(0f,0f,0f,0.2f);
	    g.setColor(trans);
	    g.fillOval(SHADOWPOS.getX(), SHADOWPOS.getY(), hitzone.getWidth(), hitzone.getHeight());
	}
	
	//Caracteristicas de cada drop que se le agregan al player.
	public void effect (Player player) {
	}	
	
	//Zona de colisión con el drop.
	public Rectangle getHitzone() {
		return hitzone;
	}
	
	//Si ya fue dibujado.
	public boolean isDropped() {
		return dropped;
	}

	public void setDropped(boolean dropped) {
		this.dropped = dropped;
	}

	//Si ya fue recogido.
	public boolean isObtained() {
		return obtained;
	}
	
}
