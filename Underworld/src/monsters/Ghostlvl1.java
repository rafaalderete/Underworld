package monsters;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

import map.Coord;
import map.Obstacle;
import map.PathfindingMap;

//Monstruo que rebota en las paredes y obstaculos.
public class Ghostlvl1 extends Enemy {
	
	private static final int ACC = 180, LIFE = 2, ATK = 1, SPRITEWIDTH = 44, SPRITEHEIGHT = 48, SPRITEDURATION = 100, HTZWIDTH = 25, HTZHEIGHT = 12, DIRR = 1, DIRL = 3;
	private static final Point HTZADJUST = new Point(10, 34);
	private Animation mover, movel;
	private SpriteSheet smovel, smover;
	private int dir;

	public Ghostlvl1(int x, int y) throws SlickException {
		super(LIFE, ATK, x, y, HTZADJUST, HTZWIDTH, HTZHEIGHT, false, true); //doncheckcolision true porque las colisiones las maneja de otra manera.
		smovel = new SpriteSheet("res/sprites/enemies/ghostlvl1/movel.png", SPRITEWIDTH, SPRITEHEIGHT);
		smover = new SpriteSheet("res/sprites/enemies/ghostlvl1/mover.png", SPRITEWIDTH, SPRITEHEIGHT);		
		mover = new Animation(smover, SPRITEDURATION);
		movel = new Animation(smovel, SPRITEDURATION);	
		mover.setLooping(true);
		movel.setLooping(true);
		int ax,ay;
		Random ran = new Random();
		//Dirección inicial.
		int j = ran.nextInt(2);
		if (j == 0)
			ax = ACC;
		else
			ax = -ACC;
		j = ran.nextInt(2);
		if (j == 0)
			ay = ACC;
		else
			ay = -ACC;
		setSpeed(new Point(ax, ay));	
		if (ax > 0)
			dir = DIRR;
		else
			dir = DIRL;
	}
	
	@Override
	public void draw(Graphics g) {
		if (!dead) {
			drawShadow(g);
			drawGhost();
		}
		else
			if (!isDeathFinalized())
				drawDeath();
	}
	
	private void drawGhost() {
		if (dir == DIRR) {
			if (!flashdamage)
				mover.draw(pos.getX(), pos.getY());
			else
				mover.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
		}
		else {
			if (!flashdamage)
				movel.draw(pos.getX(), pos.getY());
			else
				movel.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
		}		
	}
	
	private void drawDeath() {
		death.draw(pos.getX(), pos.getY(), death.getWidth()-10, death.getHeight()-10);
	}
	
	private void drawShadow (Graphics g) {
		Color trans = new Color(0f,0f,0f,0.2f);
	    g.setColor(trans);
	    g.fillOval(pos.getHitZone().getX(), pos.getHitZone().getY()+5, pos.getHitZone().getWidth(), pos.getHitZone().getHeight()-5);
	}
	
	@Override
	public void update(PathfindingMap tilemap, int delta , Rectangle area, ArrayList<Obstacle>obstacles, Coord player) throws SlickException {
		if (!dead)
			behavior(delta, area, obstacles);
	}
	
	//Rebota con las paredes del area jugable y los obstaculos.
	private void behavior(int delta, Rectangle area, ArrayList<Obstacle>obstacles) {
		boolean col = false;
		float ax = speed.getX(),ay = speed.getY();
		//Calcula la siguiente velocidad y posición.
		float accelerationx = speed.getX() * ((float)delta/1000);
		float accelerationy = speed.getY() * ((float)delta/1000);
		Rectangle nexthitzone = new Rectangle(pos.getHitZone().getX()+accelerationx, pos.getHitZone().getY()+accelerationy, HTZWIDTH, HTZHEIGHT);
		//Controla la colisión con los bordes del area jugable.
		if (area.getMaxY() < nexthitzone.getMaxY())
			ay = -ACC;
		if (area.getMinY() > nexthitzone.getMinY())
			ay = ACC;
		if (area.getMaxX() < nexthitzone.getMaxX()){
			ax = -ACC;
			dir = DIRL;
		}
		if (area.getMinX() > nexthitzone.getMinX()){
			ax = ACC;	
			dir = DIRR;
		}
		for (int i = 0; i < obstacles.size(); i++) { //Colisión con los obstaculos.
			if (obstacles.get(i).getSprite() == 1){
				if (obstacles.get(i).getRectangle().intersects(nexthitzone) && !col) {
					col =  true;
					if ( ((pos.getHitZone().getMaxX() > obstacles.get(i).getRectangle().getMinX()) && (pos.getHitZone().getMaxX() < obstacles.get(i).getRectangle().getMaxX())) 
							|| ((pos.getHitZone().getMinX() > obstacles.get(i).getRectangle().getMinX()) && (pos.getHitZone().getMinX() < obstacles.get(i).getRectangle().getMaxX()))  ){
						ay = -ay;
					}
					else
						if ( ((pos.getHitZone().getMaxY() >= obstacles.get(i).getRectangle().getMinY()) && (pos.getHitZone().getMaxY() <= obstacles.get(i).getRectangle().getMaxY())) 
								|| ((pos.getHitZone().getMinY() >= obstacles.get(i).getRectangle().getMinY()) && (pos.getHitZone().getMinY() <= obstacles.get(i).getRectangle().getMaxY()))  ){
							ax = -ax;
						}
				}
			}
		}
		if (col)
			if (ax > 0)
				dir = DIRR;
			else
				dir = DIRL;
		//Si hubo alguna colisión, actualiza con la nueva velocidad, caso contrario actualiza con la vieja velocidad. 
		setSpeed(new Point(ax, ay));
		pos.updatePosition(speed, delta, area, obstacles, HTZADJUST, HTZWIDTH, HTZHEIGHT);
	}
	
	@Override
	public void startAnimations () {	
		death.start();
		movel.start();
		mover.start();
	}
	
	@Override
	public void stopAnimations () {
		death.stop();
		movel.stop();
		mover.stop();
	}
	
	@Override
	public boolean checkAttack (Coord player) {
		if (pos.getHitZone().intersects(player.getHitZone()))
			return true;
		else
			return false;	
	}

}