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

import map.Coord;
import map.Obstacle;
import map.PathfindingMap;

//Jefe del nivel 2.
//Posee 3 ataques:
//Charge: Avanza hacia abajo.
//Fire: Si dirige a alguna de las paredes laterales y lanza fuego hasta la pared contraria.
//Fireball: Lanza bolas de fuego al jugador.
public class Dragon extends Enemy {
	
	private static final int[] STARTINGATTACK = new int[] {-80,1200}; //Donde puede iniciar el ataque de Fire.
	private static final int ACC = 100, ACC2 = 220, ACC3 = 300, BULLETSPEED = 1000, POSX = 240, POSY = -300, LIFE = 80, ATK = 2, SPRITEWIDTH = 500, SPRITEHEIGHT = 298, FIRESPRITEWIDTH = 105, FIRESPRITEHEIGHT = 415,
			SPRITEDURATION = 150, FIRESPRITEDURATION = 100, MAXFIREBALLS = 7, HTZWIDTH = 180, HTZHEIGHT = 80, FIREBALLHTZWIDTH = 40, FIREBALLHTZHEIGHT = 38, ADJUSTTOHEAD = 40, FIREATTACKDISTANCE1 = 240, FIREATTACKDISTANCE2 = 320,
			CHARGEWAITTIME = 500, FOLLOWTIME = 2000, FIREBALLTIME = 1000, POSTOP = 220, POSDOWN = 550;
	private static final Point HTZADJUST = new Point(170, 165), ADJUSTFIRE = new Point(140, 210), ADJUSTFIREBALL = new Point(150, 200);
	private boolean attacking = false, chargeattacking = false, fireattacking = false, fireballattacking = false, inposition = false, reachpositionu = false, reachpositionr = false, reachpositiond = false,
			reachpositionl = false, entersoundplayed = false, chargesoundplayed = false;
	private MonsterBullet attackfireball;
	private Rectangle attackfire = null;
	private int acceleration, chargewaitcounter = 0, followcounter = 0, fireballcounter = 0, firestartposition = 0, amountfireball = 0;
	private Image spritefireball;
	private Animation firing, nofiring, fire;
	private SpriteSheet sfiring, snofiring, sfire;
	private Sound diesound, chargesound, firesound, fireballsound, wings, entersound;

	public Dragon() throws SlickException {
		super(LIFE, ATK, POSX, POSY, HTZADJUST, HTZWIDTH, HTZHEIGHT, true, true);
		sfiring = new SpriteSheet("res/sprites/enemies/dragon/firing.png", SPRITEWIDTH, SPRITEHEIGHT);
		snofiring = new SpriteSheet("res/sprites/enemies/dragon/nofiring.png", SPRITEWIDTH, SPRITEHEIGHT);
		sfire = new SpriteSheet("res/sprites/enemies/dragon/fire.png", FIRESPRITEWIDTH, FIRESPRITEHEIGHT);	
		spritefireball = new Image("res/sprites/enemies/dragon/fireball.png");
		diesound = new Sound("res/sound/enemies/dragon/die.ogg");
		chargesound = new Sound("res/sound/enemies/dragon/charge.ogg");
		firesound = new Sound("res/sound/enemies/dragon/fire.ogg");
		fireballsound = new Sound("res/sound/enemies/dragon/fireball.ogg");
		wings = new Sound("res/sound/enemies/dragon/wings.ogg");
		entersound = new Sound("res/sound/enemies/dragon/enter.ogg");
		fire = new Animation(sfire, FIRESPRITEDURATION);
		nofiring = new Animation(snofiring, SPRITEDURATION);
		firing = new Animation(sfiring, SPRITEDURATION);
		firing.setPingPong(true);
		nofiring.setPingPong(true);
		fire.setLooping(true);
	}
	
	@Override
	public void draw(Graphics g) {
		if (!dead) {
			drawShadow(g);
			drawDragon();
			if (fireattacking && attacking)
				drawFire();
			if(fireballattacking && (attackfireball != null))
				if (!attackfireball.isCollided())
					drawFireBall();
		}
		else
			if (!isDeathFinalized())
				drawDeath();
	}
	
	private void drawDragon() {
		if (!attacking)
			if (!flashdamage)
				nofiring.draw(pos.getX(), pos.getY());
			else
				nofiring.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
		else
			if (!flashdamage)
				firing.draw(pos.getX(), pos.getY());
			else
				firing.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
	}
	
	public void drawDeath() {
		death.draw(pos.getHitZone().getX()-80, pos.getHitZone().getY()-160, death.getWidth()+200, death.getHeight()+200);
	}
	
	private void drawShadow (Graphics g) {
		Color trans = new Color(0f,0f,0f,0.2f);
	    g.setColor(trans);
	    g.fillOval(pos.getHitZone().getX(), pos.getHitZone().getY()+25, pos.getHitZone().getWidth(), pos.getHitZone().getHeight()-5);
	}
	
	private void drawFire() {
		fire.draw(pos.getX()+ADJUSTFIRE.getX()-15, pos.getY()+ADJUSTFIRE.getY()-10);
	}
	
	private void drawFireBall() {
		spritefireball.draw(attackfireball.getHitZone().getX()-5, attackfireball.getHitZone().getY()-5);
	}
	
	//El dragon vendrá desde arriba, mientras no llegue a cierta posición Y, seguira bajando. 
	//Una vez esté en posición, comenzará con behavior.
	@Override
	public void update(PathfindingMap tilemap, int delta , Rectangle area, ArrayList<Obstacle>obstacles, Coord player) throws SlickException {
		if (!dead) {
			//El sonido de las alas sonará siempre.
			if (!wings.playing())
				wings.loop();
			if (attackfire != null && !firesound.playing())
				firesound.play();
			if (!inposition)
				descend(delta, area, obstacles);
			else{
				behavior(delta, area, obstacles, player);
				if (fireballattacking && (attackfireball != null)){
					attackfireball.updateBullet(delta);
					checkFireballColision(area);
				}
			}
		}
	}	
	
	//Si no está realizando ninguna acción entonces acumula en un contador el delta y sigue al jugador en el eje X, cuando el delta llega a cierto valor,
	//mediante un random, seleciona la próxima acción a realizar.
	//Si se encuentre realizando una acción, entonces continua con dicha acción.
	private void behavior (int delta , Rectangle area, ArrayList<Obstacle>obstacles, Coord player) {
		Random ran = new Random();
		int j;
		if (followcounter < FOLLOWTIME){
			followcounter += delta;
			follow(delta, area, obstacles, player);
		}
		else {
			if (!chargeattacking && !fireattacking && !fireballattacking){
				j = ran.nextInt(3);
				if (j == 0)
					chargeattacking = true;
				else
					if (j == 1)
						fireattacking = true;
					else
						fireballattacking = true;
			}
			if (chargeattacking)
				chargeAttack(delta, area, obstacles);
			else
				if (fireattacking)
					fireAttack(delta, area, obstacles);
				else
					fireballAttack(delta, player);
		}		
	}
	
	//Baja hasta cierta posición.
	private void descend(int delta, Rectangle area, ArrayList<Obstacle>obstacles) {
		if (!entersound.playing() && !entersoundplayed){
			entersound.play();
			entersoundplayed = true;
		}
		if (pos.getHitZone().getCenterY() <  POSTOP){
			setSpeed(new Point(0, ACC));
			pos.updatePosition(speed, delta, area, obstacles, HTZADJUST, HTZWIDTH, HTZHEIGHT);
		}
		else{
			inposition = true;
		}
	}
	
	//Sigue al jugador por en eje X.
	private void follow(int delta, Rectangle area, ArrayList<Obstacle>obstacles , Coord player) {
		if ( (pos.getHitZone().getCenterX()-ADJUSTTOHEAD < player.getHitZone().getCenterX()+3) && (pos.getHitZone().getCenterX()-ADJUSTTOHEAD > player.getHitZone().getCenterX()-3) ) //Para evitar que tiemble cuande este sobre el eje X del jugador.
			acceleration = 0;
		else
			if (pos.getHitZone().getCenterX()-ADJUSTTOHEAD > player.getHitZone().getCenterX())
				acceleration = -ACC2;
			else
				if (pos.getHitZone().getCenterX()-ADJUSTTOHEAD < player.getHitZone().getCenterX())
					acceleration = ACC2;			
		setSpeed(new Point(acceleration, 0));
		pos.updatePosition(speed, delta, area, obstacles, HTZADJUST, HTZWIDTH, HTZHEIGHT);			
	}
	
	//Cuando inicia el ataque, se reproduce el sonido y se le un un pequeño tiempo para que reaccione el jugador.
	//Pasado ese tiempo, baja hasta cierta posición, una vez que llegue a la posicion de abajo, vuelve a la posición de arriba y termina el ataque.
	private void chargeAttack(int delta, Rectangle area, ArrayList<Obstacle>obstacles) {
		attacking = true;
		if (!chargesound.playing() && !chargesoundplayed){
			chargesound.play();
			chargesoundplayed = true;
		}		
		if (chargewaitcounter < CHARGEWAITTIME) //Tiempo para que reaccione el jugador.
			chargewaitcounter +=delta;
		else {
			if (!reachpositiond) { //Baja.
				if (pos.getHitZone().getCenterY() < POSDOWN)
					toDown(delta, area, obstacles);	
				else
					reachpositiond = true;
			}
			else {
				if (!reachpositionu) { //Sube.
					if (pos.getHitZone().getCenterY() > POSTOP)
						toUp(delta, area, obstacles);
					else
						reachpositionu = true;
				}
				else {
					chargewaitcounter = 0;
					chargesoundplayed = false;
					reachpositiond = false;
					reachpositionu = false;
					attacking = false;
					chargeattacking = false;
					followcounter = 0;
				}
			}	
		}		
	}
	
	//Lanza una columna de fuego que avanza de un lateral a otro, dejando un espacio al final para que el jugador se resguarde.
	//Una vez que alcanze las 2 posiciones, termina el ataque.
	private void fireAttack(int delta, Rectangle area, ArrayList<Obstacle>obstacles) {
		Random ran = new Random();
		int j;
		//Elige la posición inicial.
		if (firestartposition == 0) {
			j = ran.nextInt(STARTINGATTACK.length);
			firestartposition = STARTINGATTACK[j];
		}
		//Dependiendo la startposition elegida, primero se dirigirá a la izquierda o derecha.
		if (firestartposition == STARTINGATTACK[0]){
			if (!reachpositionl) { //Se dirige a la izquierda.
				if (pos.getHitZone().getCenterX() > STARTINGATTACK[0]) {
					toLeft(delta, area, obstacles);
				}
				else {
					reachpositionl = true;
					attacking = true;
					attackfire = new Rectangle(pos.getX()+ADJUSTFIRE.getX(), pos.getY()+ADJUSTFIRE.getY(), fire.getWidth()-30, fire.getHeight()-20); //Una vez en posisión, lanza el fuego.
				}
			}
			else {
				if (!reachpositionr) { //Se dirige a derecha lanzando el fuego.
					if (pos.getHitZone().getCenterX() < STARTINGATTACK[1]-FIREATTACKDISTANCE1) {
						toRight(delta, area, obstacles);
					}
					else {
						reachpositionr = true;						
					}
				}
				else {
					attackfire = null;
					firestartposition = 0;
					reachpositionr = false;
					reachpositionl = false;
					attacking = false;
					fireattacking = false;
					followcounter = 0;
				}
			}
		}
		else {
			if (!reachpositionr) { //Se dirige a la derecha.
				if (pos.getHitZone().getCenterX() < STARTINGATTACK[1]) {
					toRight(delta, area, obstacles);
				}
				else {
					reachpositionr = true;
					attacking = true;
					attackfire = new Rectangle(pos.getX()+ADJUSTFIRE.getX(), pos.getY()+ADJUSTFIRE.getY(), fire.getWidth()-30, fire.getHeight()-20);//Una vez en posisión, lanza el fuego.
				}
			}
			else {
				if (!reachpositionl) {//Se dirige a izquierda lanzando el fuego.
					if (pos.getHitZone().getCenterX() > STARTINGATTACK[0]+FIREATTACKDISTANCE2) {
						toLeft(delta, area, obstacles);
					}
					else {
						reachpositionl = true;
					}
				}
				else {
					attackfire = null;
					firestartposition = 0;
					reachpositionr = false;
					reachpositionl = false;
					attacking = false;
					fireattacking = false;
					followcounter = 0;
				}
			}				
		}		
	}
	
	//Cada cierto tiempo dispara una bola de fuego hacia el jugador, una vez que haya disparado todas, termina el ataque.
	private void fireballAttack(int delta, Coord player) {
		attacking = true;
		if (fireballcounter < FIREBALLTIME) {
			fireballcounter += delta;
		}
		else {
			if (amountfireball < MAXFIREBALLS){
				fireballsound.play();
				attackfireball = new MonsterBullet(new Rectangle(pos.getX()+ADJUSTFIREBALL.getX(), pos.getY()+ADJUSTFIREBALL.getY(), FIREBALLHTZWIDTH, FIREBALLHTZHEIGHT),
						player.getHitZone().getCenterX(), player.getHitZone().getCenterY(), BULLETSPEED);
				amountfireball++;						
				fireballcounter = 0;
			}
			else {
				amountfireball = 0;
				attacking = false;
				fireballattacking = false;
				fireballcounter = 0;
				followcounter = 0;
			}
				
		}
	}
	
	//Las bolas de fuego solo colisionan con el eje Y mayor. 
	private void checkFireballColision (Rectangle area) {
		if (attackfireball.getHitZone().getMaxY() > area.getMaxY())
			attackfireball.setCollided(true);		
	}
	
	//Para el Fire attack.
	private void toLeft (int delta, Rectangle area, ArrayList<Obstacle>obstacles) {
		acceleration = -ACC2;
		setSpeed(new Point(acceleration, 0));
		pos.updatePosition(speed, delta, area, obstacles, HTZADJUST, HTZWIDTH, HTZHEIGHT);	
		if (attackfire != null)  //Actualiza la posicion del fuego a medida que se mueve.
			attackfire.setLocation(pos.getX()+ADJUSTFIRE.getX(), pos.getY()+ADJUSTFIRE.getY());
	}
	
	//Para el Fire attack.
	private void toRight (int delta, Rectangle area, ArrayList<Obstacle>obstacles) {
		acceleration = ACC2;
		setSpeed(new Point(acceleration, 0));
		pos.updatePosition(speed, delta, area, obstacles, HTZADJUST, HTZWIDTH, HTZHEIGHT);	
		if (attackfire != null)  //Actualiza la posicion del fuego a medida que se mueve.
			attackfire.setLocation(pos.getX()+ADJUSTFIRE.getX(), pos.getY()+ADJUSTFIRE.getY());
	}
	
	//Cuando desciende y la primera parte del Charge.
	private void toDown (int delta, Rectangle area, ArrayList<Obstacle>obstacles) {
		acceleration = ACC3;
		setSpeed(new Point(0, acceleration));
		pos.updatePosition(speed, delta, area, obstacles, HTZADJUST, HTZWIDTH, HTZHEIGHT);
	}
	
	//Segunda parte del Charge.
	private void toUp (int delta, Rectangle area, ArrayList<Obstacle>obstacles) {
		acceleration = -ACC3;
		setSpeed(new Point(0, acceleration));
		pos.updatePosition(speed, delta, area, obstacles, HTZADJUST, HTZWIDTH, HTZHEIGHT);	
	}
	
	@Override
	public void playDeathSound() {
		if (!diesound.playing())
			diesound.play();
	}
	
	@Override
	public void stopSounds(){
		if (diesound.playing())
			diesound.stop();
		if (wings.playing())
			wings.stop();
		if (firesound.playing())
			firesound.stop();
		if (fireballsound.playing())
			fireballsound.stop();
		if (chargesound.playing())
			chargesound.stop();
		if (entersound.playing())
			entersound.stop();
	}
	
	@Override
	public void startAnimations () {	
		death.start();
		firing.start();
		nofiring.start();
		fire.start();
	}
	
	@Override
	public void stopAnimations () {
		death.stop();
		firing.stop();
		nofiring.stop();
		fire.stop();
		stopSounds();
	}
	
	@Override
	public boolean checkAttack (Coord player) {
		boolean ban = false;
		if (pos.getHitZone().intersects(player.getHitZone()) || checkFireAttack(player) || checkBulletsAttack(player))
			ban = true;
		return ban;
	}
	
	//Las bolas de fuego.
	@Override
	public boolean checkBulletsAttack (Coord player) {
		boolean ban = false;
		if (fireballattacking && attackfireball != null) {
			if (!attackfireball.isCollided()){ 
				if (attackfireball.getHitZone().intersects(player.getHitZone())){
					ban = true;
					attackfireball.setCollided(true);
				}
			}
		}
		return ban;
	}
	
	//Columna de fuego.
	private boolean checkFireAttack(Coord player){
		boolean ban = false;
		if (attackfire != null){
			if (attackfire.intersects(player.getHitZone()))
				ban = true;
		}
		return ban;
	}

}
