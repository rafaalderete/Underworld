package monsters;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
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

//Dependiendo el type será zombielvl1 o zombielvl3
//Zombielvl1: Monstruo lento que seguira al jugador constantemente, aparece en el nivel 1 y 2.
//Zombielvl3: Monstruo rapido que seguira al jugador constantemente, aparece en el nivel 3.
public class Zombielvl1_lvl3 extends Enemy {
	
	private static final int ACC0 = 40, ATK = 1, SPRITEDURATION0 = 300, SOUNDTIME0 = 5000, ACC1 = 120, SPRITEDURATION1 = 150, DIRR = 1, DIRL = 3,
	SOUNDTIME1 = 4000;
	private static final Point[]  HTZADJUST = new Point[] {(new Point(17, 64)), (new Point(19, 76)) };
	private static final int[] LIFE = new int[] {3,5};
	private static final int[] HTZWIDTH = new int[] {40,44};
	private static final int[] HTZHEIGHT = new int[] {25,29};
	private static final int[] SPRITEWIDTH = new int[] {70,80};
	private static final int[] SPRITEHEIGHT = new int[] {90,107};
	private Animation walkr, walkl;
	private SpriteSheet swalkl, swalkr;
	private Path path;
	private int soundcounter, type, acceleration, soundtime;
	private Sound moansound;

	public Zombielvl1_lvl3(int type, int x, int y) throws SlickException {
		super(LIFE[type], ATK, x, y, HTZADJUST[type], HTZWIDTH[type], HTZHEIGHT[type], false, false);
		this.type = type;
		Random ran = new Random();
		//Si es zombielvl1.
		if (type == 0) {
			acceleration = ACC0;
			soundtime = SOUNDTIME0;
			swalkl = new SpriteSheet("res/sprites/enemies/zombielvl1/walkl.png", SPRITEWIDTH[type], SPRITEHEIGHT[type]);
			swalkr = new SpriteSheet("res/sprites/enemies/zombielvl1/walkr.png", SPRITEWIDTH[type], SPRITEHEIGHT[type]);	
			moansound = new Sound("res/sound/enemies/zombielvl1/moan.ogg");
			walkr = new Animation(swalkr, SPRITEDURATION0);
			walkl = new Animation(swalkl, SPRITEDURATION0);
		}
		else {
			//zombielvl3.
			acceleration = ACC1;
			soundtime = SOUNDTIME1;
			swalkl = new SpriteSheet("res/sprites/enemies/zombielvl3/walkl.png", SPRITEWIDTH[type], SPRITEHEIGHT[type]);
			swalkr = new SpriteSheet("res/sprites/enemies/zombielvl3/walkr.png", SPRITEWIDTH[type], SPRITEHEIGHT[type]);	
			moansound = new Sound("res/sound/enemies/zombielvl3/moan.ogg");
			walkr = new Animation(swalkr, SPRITEDURATION1);
			walkl = new Animation(swalkl, SPRITEDURATION1);
		}
		walkl.setPingPong(true);
		walkr.setPingPong(true);
		soundcounter = ran.nextInt(soundtime); //Para que los zombies del mismo tipo no reproduzcan el sonido siempre al mismo tiempo.
		dir = DIRR;
	}
	
	@Override
	public void draw(Graphics g) {
		if (!dead) {
			drawShadow(g);
			drawZombie();
		}
		else
			if (!isDeathFinalized())
				drawDeath();
	}
	
	private void drawZombie () {
		if (dir == DIRL) {
			if (!flashdamage)
				walkl.draw(pos.getX(), pos.getY());
			else
				walkl.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH[type], SPRITEHEIGHT[type], new Color(Color.red));
		}
		else {
			if (!flashdamage)
				walkr.draw(pos.getX(), pos.getY());
			else
				walkr.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH[type], SPRITEHEIGHT[type], new Color(Color.red));
		}
	}
	
	private void drawDeath() {
		death.draw(pos.getX(), pos.getY(), death.getWidth()+20, death.getHeight()+20);
	}
	
	private void drawShadow (Graphics g) {
		Color trans = new Color(0f,0f,0f,0.2f);
	    g.setColor(trans);
	    g.fillOval(pos.getHitZone().getX(), pos.getHitZone().getY()+5, pos.getHitZone().getWidth(), pos.getHitZone().getHeight()-5);
	}
	
	@Override
	public void update(PathfindingMap tilemap, int delta , Rectangle area, ArrayList<Obstacle>obstacles, Coord player) throws SlickException {
		if (!dead){
			//Cada cierto tiempo reproduce un sonido.
			if (soundcounter < soundtime)
				soundcounter += delta;
			else {
				moansound.play();
				soundcounter = 0;
			}
			behavior(tilemap, delta, area, obstacles, player);
		}
	}
	
	//Movimiento constante mediante pathfinding.
	private void behavior(PathfindingMap tilemap, int delta, Rectangle area, ArrayList<Obstacle>obstacles, Coord player) {
		if (!dead) {
			AStarPathFinder pathfind = new AStarPathFinder(tilemap, 1000, false);
			path = pathfind.findPath(this, (int)pos.getTilepos().getX(), (int)pos.getTilepos().getY(), (int)player.getTilepos().getX(), (int)player.getTilepos().getY());
			findPath(path, acceleration, delta, area, obstacles, player, HTZADJUST[type], HTZWIDTH[type], HTZHEIGHT[type]);
		}
	}
	
	@Override
	public void stopSounds(){
		if (moansound.playing())
			moansound.stop();
	}
	
	@Override
	public void startAnimations () {	
		death.start();
		walkl.start();
		walkr.start();
	}
	
	@Override
	public void stopAnimations () {
		death.stop();
		walkl.stop();
		walkr.stop();
		stopSounds();
	}
	
	@Override
	public boolean checkAttack (Coord player) {
		if (pos.getHitZone().intersects(player.getHitZone()))
			return true;
		else
			return false;	
	}

}
