package monsters;
import java.util.ArrayList;
import java.util.Random;

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

//Monstruo que dispara en distintas direcciones, dependiendo del type, aparece en el nivel 3.
public class SkullPillar extends Enemy {
	
	private static final int LIFE = 9, ATK = 1, SPRITEWIDTH = 85, SPRITEHEIGHT = 80, HTZWIDTH = 49, HTZHEIGHT = 55, WAITTIME = 2500, BULLETSPEED = 400,
			BULLETWIDTH = 13, BULLETHEIGHT = 10;
	private static final double[] TYPE0ANGLES = new double[] {0, Math.toRadians(90), Math.toRadians(180), Math.toRadians(270)};						//
	private static final double[] TYPE1ANGLES = new double[] {Math.toRadians(45), Math.toRadians(135), Math.toRadians(225), Math.toRadians(315)};	//Angulos en los que disparará.
	private static final Point HTZADJUST = new Point(18, 18);
	private boolean attacking;
	private SpriteSheet skull;
	private int type, waitcounter = 2000;
	private Image sbullet;
	private MonsterBullet[] bullets;

	public SkullPillar(int x, int y) throws SlickException {
		super(LIFE, ATK, x, y, HTZADJUST, HTZWIDTH, HTZHEIGHT, true, true);
		Random ran = new Random();
		type = ran.nextInt(2);
		if (type == 0)
			skull = new SpriteSheet("res/sprites/enemies/skullpillar/type0.png", SPRITEWIDTH, SPRITEHEIGHT);
		else
			skull = new SpriteSheet("res/sprites/enemies/skullpillar/type1.png", SPRITEWIDTH, SPRITEHEIGHT);
		sbullet = new Image("res/sprites/enemies/ghostlvl2/bullet.png");
	}
	
	@Override
	public void draw(Graphics g) {
		if (!dead) {
			drawSkull(g);
		}
		else{
			if (!isDeathFinalized())
				drawDeath();
		}
		if (bullets != null)
			drawBullets(g);
	}
	
	private void drawSkull(Graphics g) {
		if (!attacking)
			if (!flashdamage)
				skull.getSprite(0, 0).draw(pos.getX(), pos.getY());
			else
				skull.getSprite(0, 0).drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
		else
			if (!flashdamage)
				skull.getSprite(1, 0).draw(pos.getX(), pos.getY());
			else
				skull.getSprite(1, 0).drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
	}
	
	private void drawBullets(Graphics g) {
		Color trans = new Color(0f,0f,0f,0.2f);
	    g.setColor(trans);
		for (int i = 0; i < bullets.length; i++) {
			if (!bullets[i].isCollided()){
				g.fillOval(bullets[i].getHitZone().getX(), bullets[i].getHitZone().getY()+5, bullets[i].getHitZone().getWidth(), bullets[i].getHitZone().getHeight());
				sbullet.draw(bullets[i].getHitZone().getX(), bullets[i].getHitZone().getY()-1);
			}
		}
	}
	
	private void drawDeath() {
		death.draw(pos.getX()+10, pos.getY()+5, death.getWidth()+5, death.getHeight()+5);
	}
	
	@Override
	public void update(PathfindingMap tilemap, int delta , Rectangle area, ArrayList<Obstacle>obstacles, Coord player) throws SlickException {
		if (!dead)
			behavior(delta, area, obstacles);
		if (bullets != null){
			for (int i = 0; i < bullets.length; i++){
				bullets[i].updateBullet(delta);
				bullets[i].checkCollision(area, obstacles);
			}
			if (allBulletsCollided()){
				attacking = false;
				bullets = null;
			}
		}	
	}
	
	//Mientras el contador sea menor a cierto tiempo, espera.
	//Terminado el tiempo de espera dispara en cuatro dirreciones, dependiendo del type.
	private void behavior(int delta, Rectangle area, ArrayList<Obstacle>obstacles) {
		if (waitcounter <= WAITTIME)
			waitcounter += delta;
		else {
			attacking = true;
			bullets = new MonsterBullet[4];
			if (type == 0){
				for (int i = 0; i < bullets.length; i++)
				bullets[i] = new MonsterBullet(new Rectangle(pos.getHitZone().getCenterX()-5, pos.getHitZone().getCenterY()-5, BULLETWIDTH, BULLETHEIGHT), TYPE0ANGLES[i], BULLETSPEED);
			}
			else {
				for (int i = 0; i < bullets.length; i++)
					bullets[i] = new MonsterBullet(new Rectangle(pos.getHitZone().getCenterX()-5, pos.getHitZone().getCenterY()-5, BULLETWIDTH, BULLETHEIGHT), TYPE1ANGLES[i], BULLETSPEED);
			} 
			waitcounter = 0;
		}
	}
	
	private boolean allBulletsCollided() {
		boolean ban = true;
		for (int i = 0; i < bullets.length; i++)
			if (!bullets[i].isCollided())
				ban = false;
		return ban;
	}
	
	@Override
	public void startAnimations () {	
		death.start();
	}
	
	@Override
	public void stopAnimations () {
		death.stop();
	}
	
	@Override
	public boolean checkAttack (Coord player) {
		if (pos.getHitZone().intersects(player.getHitZone()) || checkBulletsAttack(player) )
			return true;
		else
			return false;	
	}
	
	@Override
	public boolean checkBulletsAttack (Coord player) {
		boolean ban = false;
		if (bullets != null){
			for (int i = 0; i < bullets.length; i++){
				if (!bullets[i].isCollided()){
					if (bullets[i].getHitZone().intersects(player.getHitZone())){
						ban = true;
						bullets[i].setCollided(true);
					}
				}
			}
		}
		return ban;
	}

}