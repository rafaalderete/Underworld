package monsters;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.Path;

import map.Coord;
import map.Obstacle;
import map.PathfindingMap;

//Superclase para los monstruos.
public class Enemy implements Mover {
	
	protected final int FLASHDAMAGEDELAY = 70, SMOKEDIMENSION = 64, SMOKEDURATION = 30, DIRU = 0, DIRR = 1, DIRD = 2, DIRL = 3;
	protected Coord pos;
	protected Point speed;
	protected boolean damaged = false, flashdamage = false, dead = false, shootsbullets;
	protected SpriteSheet sdeath;
	protected Animation death;
	protected int life, atk, dir;
	protected Timer flashdamagetimer;
	protected Sound deathsound;

	public Enemy(int life, int atk, int x, int y, Point htzadjust, int htzwidth, int htzheight, boolean shootsbullets, boolean dontcheckcollision) throws SlickException {
		sdeath = new SpriteSheet("res/sprites/misc/smoke.png", SMOKEDIMENSION, SMOKEDIMENSION);
		deathsound = new Sound("res/sound/misc/monsterdeath.ogg");
		death = new Animation(sdeath, SMOKEDURATION);
		death.setLooping(false);
		pos = new Coord(x, y, true, dontcheckcollision);
		this.shootsbullets = shootsbullets;
		this.life = life;
		this.atk = atk;
		this.speed = new Point(0, 0);
		this.pos.setPosition(x, y, htzadjust, htzwidth, htzheight);
		this.pos.setTilepos(pos.getPixelmap().tilePosition(pos.getHitZone().getCenterX(), pos.getHitZone().getCenterY()));
		flashdamagetimer = new Timer(FLASHDAMAGEDELAY, new ActionListener() { //Tiempo en que se dibujará el sprite en rojo, indicando que fue golpeado.
			
			@Override
			public void actionPerformed(ActionEvent e) {
				flashdamage = false;
				flashdamagetimer.stop();			
			}
			
		});
	}
	
	public void draw(Graphics g) {	
	}
	

	public void update(PathfindingMap tilemap, int delta , Rectangle area, ArrayList<Obstacle>obstacles, Coord player) throws SlickException {
	}
	
	public Coord getCoord () {
		return pos;
	}
	
	//Verifica si alguno de los ataques golpea al jugador.
	public boolean checkAttack (Coord player) {
		return false;	
	}
	
	//Solo verifica si las balas del monstruo golpea al jugador.
	public boolean checkBulletsAttack (Coord player){
		return false;
	}
	
	//Si la animación del humo ha terminado.
	public boolean isDeathFinalized() {
		if (death.getFrame() == death.getFrameCount()-1)
			return true;
		else
			return false;
	}
	
	public void startAnimations() {	
	}
	
	public void stopAnimations() {
	}
	
	public void stopSounds() {
		
	}
	
	public void setSpeed (Point acceleration) {
		speed = new Point (acceleration.getX(), acceleration.getY());
	}
	
	//Setea la velocidad del mosntruos en base al pathfinding.
	public void findPath(Path path, int acceleration, int delta, Rectangle area, ArrayList<Obstacle>obstacles, Coord player, Point htadjust, int htwidth, int htheight) {
		int ax = 0,ay = 0;
		//Para cuando el monstruo se encuentra en el mismo tile que el jugador.
		if ( (pos.getTilepos().getX() == player.getTilepos().getX()) && (pos.getTilepos().getY() == player.getTilepos().getY()) ){
			if (pos.getHitZone().getCenterX() > player.getHitZone().getCenterX())
				ax = -acceleration;
			if (pos.getHitZone().getCenterX() < player.getHitZone().getCenterX())
				ax = acceleration;
			if (pos.getHitZone().getCenterY() > player.getHitZone().getCenterY())
				ay = -acceleration;
			if (pos.getHitZone().getCenterY() < player.getHitZone().getCenterY())
				ay = acceleration;
		}
		else {
			//Indica que velocidad deberá tener, en base al siguiente tile que deberá moverse.
			if (path != null) {
				if (path.getStep(1).getX()> path.getStep(0).getX() )
					ax = acceleration;
				if (path.getStep(1).getX()< path.getStep(0).getX() )
					ax = -acceleration;
				if (path.getStep(1).getY()> path.getStep(0).getY() )
					ay = acceleration;
				if (path.getStep(1).getY()< path.getStep(0).getY() )
					ay = -acceleration;
			}
		}
		//Setea la velocidad.
		setSpeed(new Point(ax, ay));
		pos.updatePosition(speed, delta, area, obstacles, htadjust, htwidth, htheight);
		if ( (player.getHitZone().getCenterX()-3 > pos.getHitZone().getCenterX()) || (player.getHitZone().getCenterX()+3 < pos.getHitZone().getCenterX()) ) { //Para evitar que cambie muchas veces de sprite
			if (ax ==  acceleration)																														  //cuando el monstruo se encuentra muy cerca del jugador.
				dir = DIRR;
			else
				if (ax == -acceleration)
					dir = DIRL;	
		}
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
		flashdamage = true;
		flashdamagetimer.start();
	}

	public boolean isDamaged() {
		return damaged;
	}

	public void setDamaged(boolean damaged) {
		this.damaged = damaged;
	}

	public int getAtk() {
		return atk;
	}
	
	public void playDeathSound() {
		if (!deathsound.playing())
			deathsound.play();
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		stopSounds();
		this.dead = dead;
		playDeathSound();
	}
	
	public boolean shootsBullets() {
		return shootsbullets;
	}
	
	public Coord getPos() {
		return pos;
	}
	
}