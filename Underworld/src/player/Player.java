package player;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

import map.Coord;
import map.Obstacle;

public class Player {
	private static final int SPEED = 200, SPRITEWIDTH = 133, SPRITEHEIGHT = 124, DIRR = 1, DIRL = 3, DIRU = 0 
						   , DIRD = 2, SHOT = -1, SPRITEDURATION = 100, SPRITEDEATHDURATION = 1000, BAZWIDTH = 100, BAZHEIGHT = 90
						   , HTZWIDTH = 35, HTZHEIGHT = 23, INITIALTILEPOSX = 6, INITILATILEPOSY = 5, DAMAGEDELAY = 1500, FLASHINGDELAY = 30, HURTSOUNDS = 3;
	private static final Point HTZADJUST = new Point(51, 84);
	private int acceleration = SPEED, life, maxlife, bayonetatk, gunatk, bullets, dir, damagecounter;
	private SpriteSheet sstay, satkr, satkl, satku, satkd, sgunr, sgunl, sgunu, sgund, swalkr, swalkl, swalku, swalkd, sdeath, sweapondrop;
	private Animation bytr, bytl, bytu, bytd, gunr, gunl, gunu, gund, walkr, walkl, walku, walkd, death, weapondrop;
	private Input in;
	private BulletsManager bulletmanaget;
	private Rectangle atkzone, area;
	private boolean vmoving, hmoving, moved, moveu, mover, movel, damaged, dead, attacking, gunattacking, alreadyattacked
			      , bayonetselected, atku, atkr, atkd, atkl, nodraw;
	private Point speed;
	private Coord pos;
	private Timer flashingtimer;
	private Sound bayonetattackhit, bayonetattackmiss, shotsound, nobulletsound;
	private Sound[] hurtsound;
	
	public Player(GameContainer container, Rectangle area) throws SlickException {
		in = container.getInput();		
		this.area = area;	
		sstay = new SpriteSheet("res/sprites/player/stay.png", SPRITEWIDTH, SPRITEHEIGHT);
		swalku = new SpriteSheet("res/sprites/player/walku.png", SPRITEWIDTH, SPRITEHEIGHT);
		swalkr = new SpriteSheet("res/sprites/player/walkr.png", SPRITEWIDTH, SPRITEHEIGHT);
		swalkd = new SpriteSheet("res/sprites/player/walkd.png", SPRITEWIDTH, SPRITEHEIGHT);
		swalkl = new SpriteSheet("res/sprites/player/walkl.png", SPRITEWIDTH, SPRITEHEIGHT);
		satku = new SpriteSheet("res/sprites/player/atku.png", SPRITEWIDTH, SPRITEHEIGHT);
		satkr = new SpriteSheet("res/sprites/player/atkr.png", SPRITEWIDTH, SPRITEHEIGHT);
		satkd = new SpriteSheet("res/sprites/player/atkd.png", SPRITEWIDTH, SPRITEHEIGHT);
		satkl = new SpriteSheet("res/sprites/player/atkl.png", SPRITEWIDTH, SPRITEHEIGHT);
		sgunu = new SpriteSheet("res/sprites/player/gunu.png", SPRITEWIDTH, SPRITEHEIGHT);
		sgunr = new SpriteSheet("res/sprites/player/gunr.png", SPRITEWIDTH, SPRITEHEIGHT);
		sgund = new SpriteSheet("res/sprites/player/gund.png", SPRITEWIDTH, SPRITEHEIGHT);
		sgunl = new SpriteSheet("res/sprites/player/gunl.png", SPRITEWIDTH, SPRITEHEIGHT);
		sdeath = new SpriteSheet("res/sprites/player/death.png", SPRITEWIDTH, SPRITEHEIGHT);
		sweapondrop = new SpriteSheet("res/sprites/player/weapondrop.png", SPRITEWIDTH, SPRITEHEIGHT);
		bayonetattackhit = new Sound("res/sound/player/cut.ogg");
		bayonetattackmiss = new Sound("res/sound/player/slash.ogg");
		shotsound = new Sound("res/sound/player/shot.ogg");
		nobulletsound = new Sound("res/sound/player/nobullets.ogg");
		hurtsound = new Sound[HURTSOUNDS];
		hurtsound[0] = new Sound("res/sound/player/hurt1.ogg");
		hurtsound[1] = new Sound("res/sound/player/hurt2.ogg");
		hurtsound[2] = new Sound("res/sound/player/hurt3.ogg");
		walku = new Animation(swalku, SPRITEDURATION);
		walkr = new Animation(swalkr, SPRITEDURATION);
		walkd = new Animation(swalkd, SPRITEDURATION);
		walkl = new Animation(swalkl, SPRITEDURATION);
		bytu = new Animation(satku, SPRITEDURATION);
		bytr = new Animation(satkr, SPRITEDURATION);
		bytd = new Animation(satkd, SPRITEDURATION);
		bytl = new Animation(satkl, SPRITEDURATION);
		gunu = new Animation(sgunu, SPRITEDURATION);
		gunr = new Animation(sgunr, SPRITEDURATION);
		gund = new Animation(sgund, SPRITEDURATION);
		gunl = new Animation(sgunl, SPRITEDURATION);
		death = new Animation(sdeath, SPRITEDEATHDURATION);
		weapondrop = new Animation(sweapondrop, SPRITEDURATION);
		bytu.setLooping(false);
		bytr.setLooping(false);
		bytd.setLooping(false);
		bytl.setLooping(false);
		gunu.setLooping(false);
		gunr.setLooping(false);
		gund.setLooping(false);
		gunl.setLooping(false);	
		death.setLooping(false);
		weapondrop.setLooping(false);
		flashingtimer = new Timer (FLASHINGDELAY, new ActionListener() { // Timer que alterna entre dibujar o no el jugador, parpadeando e indicando que el jugador fue golpeado.
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				nodraw = !nodraw;
				flashingtimer.stop();		
			}
		});
	}
	
	public void init(float x, float y) throws SlickException {
		atkzone = null; 
		vmoving = false;
		hmoving = false;
		moved = false;
		moveu = false;
		mover = false;
		movel = false;
		damaged = false;
		dead = false;
		attacking = false;
		gunattacking = false;
		alreadyattacked = false;
		bayonetselected = true;
		atku = false;
		atkr = false;
		atkd = false;
		atkl = false;
		nodraw = false;
		this.bulletmanaget = new BulletsManager(area);
		pos = new Coord(x, y, false, false);
		speed = new Point (0,0);
		pos.setHitZone(HTZADJUST, HTZWIDTH, HTZHEIGHT); //Zona en donde el jugador recibirá daño.
		pos.setTilepos(new Point(INITIALTILEPOSX, INITILATILEPOSY));
		acceleration = SPEED;
		life = 10;
		maxlife = 10; 
		bayonetatk = 1; 
		gunatk = 2;
		bullets = 10;
		dir = 2;
	}
	
	public void draw(Graphics g) {	
		if (!dead) {
			drawShadow(g);
			drawPlayer();
		}
		else
			drawDeath();	
	}
	
	//Dibuja el moviento y ataque del personaje.
	//Dibuja el parsonaje si esta quieto dependiendo de dir(dirección en donde está mirando el personaje).
	//Si está atacando, dibuja el ataque de la bayoneta.
	//Si está atacando y no con la bayoneta, dibuja el disparo.
	private void drawPlayer() {
		if (!vmoving && !hmoving && !attacking && !gunattacking)	
			if (!nodraw)
				sstay.getSprite(dir, 0).draw(pos.getX(),pos.getY());
		if (attacking || gunattacking) {
			if (bayonetselected){
				if (!nodraw)
					if (atku)								
							bytu.draw(pos.getX(),pos.getY());	
					else									
						if (atkr)							
							bytr.draw(pos.getX(),pos.getY());		
						else								
							if (atkd)						
								bytd.draw(pos.getX(),pos.getY());		
							else							
								if (atkl)					
									bytl.draw(pos.getX(),pos.getY());	
			}
			else {
				if (!nodraw)
					if (atku) {								
						gunu.draw(pos.getX(),pos.getY());	
						if (bullets == 0)
							gunu.stopAt(0);
					}
					else									
						if (atkr) {							
							gunr.draw(pos.getX(),pos.getY());
							if (bullets == 0)
								gunr.stopAt(0);
						}
						else								
							if (atkd) {					
								gund.draw(pos.getX(),pos.getY());	
								if (bullets == 0)
									gund.stopAt(0);
							}
							else							
								if (atkl) {				
									gunl.draw(pos.getX(),pos.getY());	
									if (bullets == 0)
										gunl.stopAt(0);
								}
			}		
		}
		else {
			if (!nodraw)
				if (moved)									
					walkd.draw(pos.getX(),pos.getY());				
				else										
					if (moveu)								
						walku.draw(pos.getX(),pos.getY());				
					else									
						if (mover)							
							walkr.draw(pos.getX(),pos.getY());			
						else								
							if (movel)						
								walkl.draw(pos.getX(),pos.getY());		
		}		
	}
	
	private void drawShadow(Graphics g) {
		Color trans = new Color(0f,0f,0f,0.2f);
	    g.setColor(trans);
	    g.fillOval(pos.getHitZone().getX(), pos.getHitZone().getY()+5, pos.getHitZone().getWidth(), pos.getHitZone().getHeight()-5);
	}
	
	private void drawDeath() {
		death.draw(pos.getX(), pos.getY());
		if (death.getFrame() == death.getFrameCount()-1)
			weapondrop.draw(pos.getX(), pos.getY()+SPRITEHEIGHT);
	}
	
	public void update(int delta, ArrayList<Obstacle> obstacles) throws SlickException {
		checkDamageTimer(delta);
		checkFlashingTimer();
			move(delta, obstacles);	
		attack();
		selectWeapon();
	}
	
	//Al presionar alguna tecla (WASD) se setea la velocidad, indica que se está moviendo, en que dirrección está mirando
	//y que sprite debe dibujar. En base a la velocidad se actualiza la posición del sprite y verifica que sea valida.
	//Si no se presionó ninguna tecla entonces resetea la animación de los sprites.
	private void move(int delta, ArrayList<Obstacle> obstacles) {
		int vacceleration = 0, hacceleration = 0;
		if (!attacking && !gunattacking) {
			if (in.isKeyDown(Input.KEY_S)) {
				dir = DIRD;
				vacceleration = acceleration;
				moved = true;
				vmoving = true;
			}
			else {
				moved = false;
				walkd.restart();
				if (in.isKeyDown(Input.KEY_W)) {
					dir = DIRU;
					vacceleration = -acceleration;
					moveu = true;
					vmoving = true;
				}
				else {
					moveu = false;
					vmoving = false;
					walku.restart();
					vacceleration = 0;
				}
			}
				
			if (in.isKeyDown(Input.KEY_D)) {
				dir = DIRR;
				hacceleration = acceleration;
				mover = true;
				hmoving = true;
			}
			else {
				mover = false;
				walkr.restart();
				if (in.isKeyDown(Input.KEY_A)) {
					dir = DIRL;
					hacceleration = -acceleration;
					movel = true;
					hmoving = true;
				}
				else {
					movel = false;
					hmoving = false;			
					walkl.restart();
					hacceleration = 0;
				}
			}
			setSpeed(new Point(hacceleration,vacceleration));
			pos.updatePosition(speed, delta, area, obstacles, HTZADJUST, HTZWIDTH, HTZHEIGHT);
		}				
	}
	
	//Al presionar alguna flecha, indica que está atacando, la dirección y que sprite debe dibujar.
	//Ademas se llama a la funcion que delimita la zona de golpe del personaje. Si al atacar 
	//esta seleccionado el disparo, se resta una bala.
	//Termina de atacar al terminar por completo la animación del ataque y los resetea.
	//Cuando no se está presionando ninguna flecha, se llama al metodo que genera la zona de ataque
	//con(-1) el cual genera una zona de ataque nula.
	private void attack() {
		if (in.isKeyPressed(Input.KEY_UP) && (!attacking && !gunattacking)) {
			atku = true;
			dir = DIRU;	
			setAttackZone(dir);
			if (bayonetselected)
				attacking = true;	
			else {
				setBullets(SHOT);
				gunattacking = true;
			}
		}
		else {		
			if (bytu.isStopped()){
				atku = false;						
				bytu.restart();		
				attacking = false;	
				setAttackZone(-1);
			}
			if (gunu.isStopped()) {
				atku = false;	
				gunu.restart();
				gunattacking = false;
			}
			if (in.isKeyPressed(Input.KEY_DOWN) && (!attacking && !gunattacking)) {
				atkd = true;
				dir = DIRD;
				setAttackZone(dir);
				if (bayonetselected)
					attacking = true;
				else {
					setBullets(SHOT);
					gunattacking = true;
				}
			}
			else {
				if (bytd.isStopped()){
					atkd = false;						
					bytd.restart();		
					attacking = false;	
					setAttackZone(-1);
				}
				if (gund.isStopped()) {
					atkd = false;
					gund.restart();
					gunattacking = false;
				}
				if (in.isKeyPressed(Input.KEY_LEFT) && (!attacking && !gunattacking)) {
					atkl = true;
					dir = DIRL;
					setAttackZone(dir);
					if (bayonetselected)
						attacking = true;
					else {
						setBullets(SHOT);
						gunattacking = true;
					}
				}
				else {
					if (bytl.isStopped()){
						atkl = false;						
						bytl.restart();		
						attacking = false;	
						setAttackZone(-1);
					}
					if (gunl.isStopped()) {
						atkl = false;	
						gunl.restart();
						gunattacking = false;
					}
					if (in.isKeyPressed(Input.KEY_RIGHT) && (!attacking && !gunattacking)) {
						atkr = true;
						dir = DIRR;
						setAttackZone(dir);
						if (bayonetselected)
							attacking = true;
						else {
							setBullets(SHOT);
							gunattacking = true;
						}
					}
					else {
						if (bytr.isStopped()){
							atkr = false;						
							bytr.restart();		
							attacking = false;	
							setAttackZone(-1);
						}
						if (gunr.isStopped()) {
							atkr = false;	
							gunr.restart();
							gunattacking = false;
						}
					}
				}
			}
		}
	}
	
	//Genera los Rectangle de zona de ataque dependiendo de la dirreción en donde esté mirando.
	//el personaje.
	//Si está disparando se agrega una nueva bala en la lista del bulletmanager.
	private void setAttackZone(int dir) {
		if (dir == -1) 
			atkzone = null;
		else {		
			if (bayonetselected) {
				if (dir == DIRU) {
					atkzone = new Rectangle(pos.getX()+20, pos.getY()+5, BAZWIDTH, BAZHEIGHT);				
				}
				else {
					if (dir == DIRR) {
						atkzone = new Rectangle(pos.getX()+58, pos.getY()+25, BAZHEIGHT, BAZWIDTH);
					}
					else {
						if (dir == DIRD) {
							atkzone = new Rectangle(pos.getX()+20, pos.getY()+64, BAZWIDTH, BAZHEIGHT);
						}
						else {
							if (dir == DIRL) {
								atkzone = new Rectangle(pos.getX()-20, pos.getY()+25, BAZHEIGHT, BAZWIDTH);				
							}
						}
					}
				}	
			}
			else {
				if (bullets > 0) {
					shotsound.play();
					if (dir == DIRU) {
						bulletmanaget.addBullet(pos.getX()+65, pos.getY()+30, dir);				
					}
					else {
						if (dir == DIRR) {
							bulletmanaget.addBullet(pos.getX()+100, pos.getY()+90, dir);
						}
						else {
							if (dir == DIRD) {
								bulletmanaget.addBullet(pos.getX()+65, pos.getY()+115, dir);
							}
							else {
								if (dir == DIRL) {
									bulletmanaget.addBullet(pos.getX()+25, pos.getY()+90, dir);				
								}
							}
						}
					}
				}
				else
					nobulletsound.play();
			}
		}	
	}
	
	private void stopSounds() {
		if (bayonetattackhit.playing())
			bayonetattackhit.stop();
		if (bayonetattackmiss.playing())
			bayonetattackmiss.stop();
		if (shotsound.playing())
			shotsound.stop();
		if (nobulletsound.playing())
			nobulletsound.stop();
	}
	
	//Para cuando entra en estado pausa.
	public void stopAnimations() {
		walku.stop();
		walkr.stop();
		walkd.stop();
		walkl.stop();
		bytu.stop();
		bytr.stop();
		bytd.stop();
		bytl.stop();
		gunu.stop();
		gunr.stop();
		gund.stop();
		gunl.stop();
		stopSounds();
	}
	
	public void startAnimations() {
		walku.start();
		walkr.start();
		walkd.start();
		walkl.start();
		bytu.start();
		bytr.start();
		bytd.start();
		bytl.start();
		gunu.start();
		gunr.start();
		gund.start();
		gunl.start();
	}
	
	public void resetDeath() {
		death.restart();
		weapondrop.restart();
	}
	
	public BulletsManager getBulletmanaget() {
		return bulletmanaget;
	}
	
	//Sonido random al recibir daño.
	private void playHurtSound() {
		Random ran = new Random();
		int i = ran.nextInt(hurtsound.length);
		hurtsound[i].play();
	}

	//Cada vez que recibe daño se llama a este metodo para iniciar el contador en donde no podrá recibir daño.
	public void startDamagedTime() {
		setDamaged(true);		
		damagecounter = 0;
		flashingtimer.start();
		playHurtSound();
	}
	
	//Actualiza el contador de daño, si llega hasta cierto valor, el player podrá recibir daño nuevamente.
	private void checkDamageTimer(int delta) {
		if (damaged){
			damagecounter += delta;
			if (damagecounter >= DAMAGEDELAY) 
				damaged = false;
		}
	}
	
	//Si ha sido dañado comienza el timer y el parpadeo del player.
	private void checkFlashingTimer () {
		if (!flashingtimer.isRunning()) {
			if (damaged)
				flashingtimer.start();
			else
				nodraw = false;
		}
	}
	
	//Posición de jugador,
	public Coord getCoord () {
		return pos;
	}
	
	private void setSpeed (Point acceleration) {
		speed = new Point (acceleration.getX(), acceleration.getY());
	}
		
	//Al presionar Q cambia de arma entre la bayoneta y el disparo.
	private void selectWeapon () {
		if (in.isKeyPressed(Input.KEY_Q) && !attacking && !gunattacking)
			bayonetselected= !bayonetselected;
	}
	
	public boolean getBayonetSelected () {
		return bayonetselected;
	}
	
	public void setBullets (int b) {
		if ((bullets + b) >= 0)
			bullets = bullets + b;
	}
	
	public int getBullets () {
		return bullets;
	}
	
	public Rectangle getAttackzone () {
		return atkzone;
	}
	
	public boolean isAttacking () {
		return attacking;
	}
	
	//Para evitar golpear monstruos una vez atacado, Al atacar golpea a los monstruos que esten dentro del attackzone,
	//los monstruos que posteriormente entren al attackzone no seran golpeados.
	public boolean isAlreadyattacked() {
		return alreadyattacked;
	}

	public void setAlreadyattacked(boolean alreadyattacked) {
		this.alreadyattacked = alreadyattacked;
	}
	//

	public boolean isGunattacking() {
		return gunattacking;
	}

	//La maxima cantidad de vida que puede tener el personaje.
	public int getMaxLife () {
		return maxlife;
	}
	
	public void setMaxLife (int maxlife) {
		this.maxlife = maxlife;
	}
	
	//Vida actual del personaje.
	public int getLife () {
		return life;
	}
	
	public void setLife(int life) {
		this.life = life;
		if (life <= 0){
			dead = true;
		}
			
	}

	//Daño provocado al atacar con la bayoneta.
	public int getBayonetAtk () {
		return bayonetatk;
	}
	
	public void setBayonetAtk(int bayonetatk) {
		this.bayonetatk = bayonetatk;
	}
	
	//Daño provocado al atacar con el disparo.
	public int getGunAtk () {
		return gunatk;
	}
	
	public void setGunAtk(int gunatk) {
		this.gunatk = gunatk;
	}
	
	public int getAcceleration(){
		return acceleration;
	}
	
	public void setAcceleration(int acceleration) {
		this.acceleration = acceleration;
	}
	
	//Posición y dimensión de que tendrá la zona de golpe del jugador.
	public Point getHTZAdjust () {
		return HTZADJUST;
	}
	//
	public int getHTZWidth () {
		return HTZWIDTH;
		
	}
	//
	public int getHTZHeight () {
		return HTZHEIGHT;		
	}

	public boolean isDamaged() {
		return damaged;
	}

	public void setDamaged(boolean damaged) {
		this.damaged = damaged;
	}

	public boolean isDead() {
		return dead;
	}
	
	//Cuando golpe a un monstruo con la bayoneta.
	public void hitMonster(){
		bayonetattackhit.play();
	}
	
	//Cuando no golpea ningun monstruo al atacar con la bayoneta.
	public void missMonster() {
		bayonetattackmiss.play();
	}
	
}