package monsters;
import java.util.ArrayList;

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

//Monstruo de velocidad media que seguira al jugador constantemente, aparece en el nivel 2 y 3.
public class Zombielvl2 extends Enemy {
	
	private static final int ACC1 = 40, ACC2 = 110, LIFE = 4, ATK = 1, SPRITEWIDTH = 72, SPRITEHEIGHT = 94, SPRITEDURATION1 = 300,
			SPRITEDURATION2 = 170, HTZWIDTH = 41, HTZHEIGHT = 26, DIRR = 1, DIRL = 3, DISTANCE = 4;
	private static final Point HTZADJUST = new Point(17, 67);
	private Animation walkr, walkrunr, walkl, walkrunl;
	private SpriteSheet swalkl, swalkr, swalkrunl, swalkrunr;
	private int acceleration = ACC1;
	private Path path;
	private Sound moansound;

	public Zombielvl2(int x, int y) throws SlickException {
		super(LIFE, ATK, x, y, HTZADJUST, HTZWIDTH, HTZHEIGHT, false, false);
		swalkl = new SpriteSheet("res/sprites/enemies/zombielvl2/walkl.png", SPRITEWIDTH, SPRITEHEIGHT);
		swalkr = new SpriteSheet("res/sprites/enemies/zombielvl2/walkr.png", SPRITEWIDTH, SPRITEHEIGHT);
		swalkrunl = new SpriteSheet("res/sprites/enemies/zombielvl2/walkrunl.png", SPRITEWIDTH, SPRITEHEIGHT);
		swalkrunr = new SpriteSheet("res/sprites/enemies/zombielvl2/walkrunr.png", SPRITEWIDTH, SPRITEHEIGHT);
		moansound = new Sound("res/sound/enemies/zombielvl2/moan.ogg");
		walkr = new Animation(swalkr, SPRITEDURATION1);
		walkl = new Animation(swalkl, SPRITEDURATION1);
		walkrunr = new Animation(swalkrunr, SPRITEDURATION2);
		walkrunl = new Animation(swalkrunl, SPRITEDURATION2);
		walkl.setPingPong(true);
		walkr.setPingPong(true);
		walkrunl.setPingPong(true);
		walkrunr.setPingPong(true);
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
	
	//Dependiendo de la velocidad dibuja un sprite u otro.
	private void drawZombie () {
		if (dir == DIRL) {
			if (acceleration == ACC1) {
				if (!flashdamage)
					walkl.draw(pos.getX(), pos.getY());
				else
					walkl.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
			}
			else {
				if (!flashdamage)
					walkrunl.draw(pos.getX(), pos.getY());
				else
					walkrunl.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
			}
		}
		else {
			if (acceleration == ACC1) {
				if (!flashdamage)
					walkr.draw(pos.getX(), pos.getY());
				else
					walkr.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
			}
			else {
				if (!flashdamage)
					walkrunr.draw(pos.getX(), pos.getY());
				else
					walkrunr.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
			}
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
		if (!dead)
			behavior(tilemap, delta, area, obstacles, player);
	}
	
	//Movimiento constante mediante pathfinding.
	private void behavior(PathfindingMap tilemap, int delta, Rectangle area, ArrayList<Obstacle>obstacles, Coord player) {
		if (!dead) {
			AStarPathFinder pathfind = new AStarPathFinder(tilemap, 1000, false);
			path = pathfind.findPath(this, (int)pos.getTilepos().getX(), (int)pos.getTilepos().getY(), (int)player.getTilepos().getX(), (int)player.getTilepos().getY());
			checkDistance();
			findPath(path, acceleration, delta, area, obstacles, player, HTZADJUST, HTZWIDTH, HTZHEIGHT);
		}
	}
	
	//Cuando la distancia de los tiles entre el monstruo y el jugador es menor a cierto valor, aumenta su velocidad, caso contrario, la disminuye.
	private void checkDistance() {
		if (path != null)
			if (path.getLength() < DISTANCE){
				if (acceleration != ACC2){
					if (!moansound.playing())
						moansound.play();
					acceleration = ACC2;
				}
			}
			else
				if (acceleration != ACC1)
					acceleration = ACC1;
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
		walkrunl.start();
		walkrunr.start();
	}
	
	@Override
	public void stopAnimations () {
		death.stop();
		walkl.stop();
		walkr.stop();
		walkrunl.stop();
		walkrunr.stop();
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