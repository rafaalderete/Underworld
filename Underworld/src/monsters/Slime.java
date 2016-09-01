package monsters;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;

import map.Coord;
import map.Obstacle;
import map.PathfindingMap;

//Monstruo lento que dispara, aparece en el nivel 1 y 2.
public class Slime extends Enemy {
	
	private static final int ACC = 30, LIFE = 4, ATK = 1, SPRITEWIDTH = 75, SPRITEHEIGHT = 69, SPRITEDURATION = 300, ATTACKSPRITEDURATION = 150, HTZWIDTH = 40, HTZHEIGHT = 20, DIRR = 1, DIRL = 3,
			BULLETWIDTH = 20, BULLETHEIGHT = 16, BULLETSPEED = 400, WAITTIME = 1500, MOVETIME = 2000;
	private static final Point HTZADJUST = new Point(18, 43);
	private Animation attackl, attackr, stayl, stayr;
	private Image sbullet;
	private SpriteSheet sattackl, sattackr, sstayl, sstayr, smove;
	private MonsterBullet bullet;
	private boolean attacking = false, alreadyshoot = false, moving = false;
	private Path path;
	private int movecounter = 0, waitcounter = 0;
	private Sound bulletsound;

	public Slime(int x, int y) throws SlickException {
		super(LIFE, ATK, x, y, HTZADJUST, HTZWIDTH, HTZHEIGHT, true, false);
		sstayl = new SpriteSheet("res/sprites/enemies/slime/stayl.png", SPRITEWIDTH, SPRITEHEIGHT);
		sstayr = new SpriteSheet("res/sprites/enemies/slime/stayr.png", SPRITEWIDTH, SPRITEHEIGHT);
		sattackl = new SpriteSheet("res/sprites/enemies/slime/attackl.png", SPRITEWIDTH, SPRITEHEIGHT);
		sattackr = new SpriteSheet("res/sprites/enemies/slime/attackr.png", SPRITEWIDTH, SPRITEHEIGHT);
		smove = new SpriteSheet("res/sprites/enemies/slime/move.png", SPRITEWIDTH, SPRITEHEIGHT);
		sbullet = new Image("res/sprites/enemies/giantslime/bullet.png");
		bulletsound = new Sound("res/sound/enemies/slime/shoot.ogg");
		stayl = new Animation(sstayl, SPRITEDURATION);
		stayr = new Animation(sstayr, SPRITEDURATION);		
		attackl = new Animation(sattackl, ATTACKSPRITEDURATION);
		attackr = new Animation(sattackr, ATTACKSPRITEDURATION);
		attackl.setLooping(false);
		attackr.setLooping(false);
		stayl.setPingPong(true);
		stayr.setPingPong(true);
		dir = DIRR;
	}
	
	@Override
	public void draw(Graphics g) {
		if (!dead) {
			drawSlime();	
		}
		else
			if (!isDeathFinalized())
				drawDeath();
		drawBullets(g);
	}
	
	private void drawSlime () {
		if (!attacking) {
			if ( (speed.getX() == 0) && (speed.getY() == 0) ){
				if (dir == DIRL) {
					if (!flashdamage)
						stayl.draw(pos.getX(), pos.getY());
					else
						stayl.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
				}
				else {
					if (!flashdamage)
						stayr.draw(pos.getX(), pos.getY());
					else
						stayr.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
				}
			}
			else {
				if (dir == DIRL) {
					if (!flashdamage)
						smove.getSprite(0, 0).draw(pos.getX(), pos.getY());
					else
						smove.getSprite(0, 0).drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
				}
				else {
					if (!flashdamage)
						smove.getSprite(1, 0).draw(pos.getX(), pos.getY());
					else
						smove.getSprite(1, 0).drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
				}
			}
		}
		else {
			if (dir == DIRL) {
				if (!flashdamage)
					attackl.draw(pos.getX(), pos.getY());
				else
					attackl.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
			}
			else {
				if (!flashdamage)
					attackr.draw(pos.getX(), pos.getY());
				else
					attackr.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
			}	
		}
	}
	
	private void drawDeath() {
		death.draw(pos.getX(), pos.getY()-10, death.getWidth()+10, death.getHeight()+10);
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
			behavior(tilemap, delta, area, obstacles, player);	
		if (bullet != null){
			bullet.updateBullet(delta);
			bullet.checkCollision(area, obstacles);
		}
	}
	
	//Si no está realizando ninguna acción entonces acumula en un contador el delta hasta llegar a cierto valor, una vez
	//alcanzado ese valor, mediante un random, seleciona la próxima acción a realizar.
	//Si se encuentre realizando una acción, entonces continua con dicha acción.
	private void behavior(PathfindingMap tilemap, int delta, Rectangle area, ArrayList<Obstacle>obstacles, Coord player) {
		Random ran = new Random();
		int j;
		if (!attacking && !moving) {
			if (waitcounter < WAITTIME) {
				waitcounter += delta;
				setSpeed(new Point(0, 0));
			}
			else {
				j = ran.nextInt(2);
				if (j == 0)
					attacking = true;
				else
					moving = true;
			}
		}
		else {
			if (attacking)
				shoot(player);
			if (moving)
				move(tilemap, delta, area, obstacles, player);
		}
	}
	
	//Una vez que dispara, la acción no finaliza hasta que haya terminado toda la animación de ataque.
	private void shoot(Coord player) {
		if (!alreadyshoot) {
			//Para saber que sprite dibujar.
			if (player.getHitZone().getCenterX() >= pos.getHitZone().getCenterX())
				dir = DIRR;
			else
				dir = DIRL;
			bulletsound.play();
			bullet = new MonsterBullet(new Rectangle(pos.getHitZone().getCenterX()-15, pos.getHitZone().getCenterY()-10, BULLETWIDTH, BULLETHEIGHT),
					player.getHitZone().getCenterX(), player.getHitZone().getCenterY(), BULLETSPEED);
			alreadyshoot = true; //La bala fue disparada.
		}
		else {
			if (attackl.isStopped() || attackr.isStopped()){
				waitcounter = 0;
				attacking = false;
				alreadyshoot = false;
				attackl.restart();
				attackr.restart();
			}
		}
	}
	
	//Se mueve durante cierta cantidad de tiempo, acumulando el delta,, mediante pathfinding. Una pasado el tiempo, termina la acción.
	private void move(PathfindingMap tilemap, int delta, Rectangle area, ArrayList<Obstacle>obstacles, Coord player) {
		if (movecounter < MOVETIME) {
			movecounter += delta;
			AStarPathFinder pathfind = new AStarPathFinder(tilemap, 1000, false);
			path = pathfind.findPath(this, (int)pos.getTilepos().getX(), (int)pos.getTilepos().getY(), (int)player.getTilepos().getX(), (int)player.getTilepos().getY());
			findPath(path, ACC, delta, area, obstacles, player, HTZADJUST, HTZWIDTH, HTZHEIGHT);
		}
		else {
			moving = false;
			waitcounter = 0;
			movecounter = 0;
		}
	}
	
	@Override
	public void stopSounds(){
		if (bulletsound.playing())
			bulletsound.stop();
	}
	
	@Override
	public void startAnimations () {	
		death.start();
		stayl.start();
		stayr.start();
		attackl.start();
		attackr.start();
	}
	
	@Override
	public void stopAnimations () {
		death.stop();
		stayl.stop();
		stayr.stop();
		attackl.stop();
		attackr.stop();
		stopSounds();
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

