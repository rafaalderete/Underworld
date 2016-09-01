package monsters;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

import map.Coord;
import map.Obstacle;
import map.PathfindingMap;

//Monstruo que rebota en las paredes y obstaculos y dispara.
public class Ghostlvl2 extends Enemy {
	
	private static final int ACC = 230, LIFE = 3, ATK = 1, SPRITEWIDTH = 83, SPRITEHEIGHT = 106, SPRITEDURATION = 100, HTZWIDTH = 50, HTZHEIGHT = 26, DIRR = 1, DIRL = 3, SHOOTTIME = 3000,
			BULLETWIDTH = 20, BULLETHEIGHT = 16, BULLETSPEED = 300;
	private static final Point HTZADJUST = new Point(20, 76);
	private Animation mover, movel;
	private SpriteSheet smovel, smover;
	private Image sbullet;
	private MonsterBullet bullet;
	private int dir, shootcounter = 0;

	public Ghostlvl2(int x, int y) throws SlickException {
		super(LIFE, ATK, x, y, HTZADJUST, HTZWIDTH, HTZHEIGHT, true, true);//doncheckcolision true porque las colisiones las maneja de otra manera.
		smovel = new SpriteSheet("res/sprites/enemies/ghostlvl2/movel.png", SPRITEWIDTH, SPRITEHEIGHT);
		smover = new SpriteSheet("res/sprites/enemies/ghostlvl2/mover.png", SPRITEWIDTH, SPRITEHEIGHT);	
		sbullet = new Image("res/sprites/enemies/ghostlvl2/bullet.png");
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
		drawBullets(g);
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
		death.draw(pos.getX()+5, pos.getY()+5, death.getWidth()+17, death.getHeight()+17);
	}
	
	private void drawShadow (Graphics g) {
		Color trans = new Color(0f,0f,0f,0.2f);
	    g.setColor(trans);
	    g.fillOval(pos.getHitZone().getX(), pos.getHitZone().getY()+10, pos.getHitZone().getWidth(), pos.getHitZone().getHeight()-5);
	}
	
	private void drawBullets(Graphics g) {
		if (bullet != null){
			Color trans = new Color(0f,0f,0f,0.2f);
		    g.setColor(trans);
		    if (!bullet.isCollided()){
				g.fillOval(bullet.getHitZone().getX(), bullet.getHitZone().getY()+3, bullet.getHitZone().getWidth(), bullet.getHitZone().getHeight());
				sbullet.draw(bullet.getHitZone().getX(), bullet.getHitZone().getY()-1, BULLETWIDTH, BULLETHEIGHT);
		    }
		}
	}
	
	@Override
	public void update(PathfindingMap tilemap, int delta , Rectangle area, ArrayList<Obstacle>obstacles, Coord player) throws SlickException {
		if (!dead)
			behavior(delta, area, obstacles,player);
		if (bullet != null){
			bullet.updateBullet(delta);
			bullet.checkCollision(area, obstacles);
		}
	}
	
	//Rebota con las paredes del area jugable y los obstaculos.
	private void behavior(int delta, Rectangle area, ArrayList<Obstacle>obstacles, Coord player) {
		boolean col = false;
		//Cada cierto tiempo dispara una bala.
		if (shootcounter < SHOOTTIME)
			shootcounter += delta;
		else
			shoot(player);
		float ax = speed.getX(),ay = speed.getY();
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
	
	private void shoot(Coord player) {
		shootcounter = 0;
		bullet = new MonsterBullet(new Rectangle(pos.getHitZone().getCenterX(), pos.getHitZone().getCenterY(), BULLETWIDTH, BULLETHEIGHT),
				player.getHitZone().getCenterX(), player.getHitZone().getCenterY(), BULLETSPEED);
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
		boolean ban = false;
		if (pos.getHitZone().intersects(player.getHitZone()) || checkBulletsAttack(player))
			ban = true;
		return ban;		
	}
	
	@Override
	public boolean checkBulletsAttack (Coord player) {
		boolean ban = false;
		if (bullet != null){
			if (!bullet.isCollided()){
				if (bullet.getHitZone().intersects(player.getHitZone())){
					ban = true;
					bullet.setCollided(true);
				}
			}
		}
		return ban;
	}

}
