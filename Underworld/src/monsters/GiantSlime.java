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

//Jefe del nivel 1.
//Posee 2 ataques: 
//Charge: se dirige hacia el player con una velocidad elevada.
//Shoot: dispara en 8 dirreciones.
public class GiantSlime extends Enemy {

	private static final int ACC = 400, POSX = 415, POSY = 210, LIFE = 45, ATK = 1, SPRITEWIDTH = 205, SPRITEHEIGHT = 182, SPRITEDURATION = 150,
			ATTACK1SPRITEDURATION = 600, HTZWIDTH = 120, HTZHEIGHT = 60, DIRR = 1, DIRL = 3, BULLETWIDTH = 29, BULLETHEIGHT = 25, BULLETSPEED = 650, WAITTIME = 1600;
	private static final double[] SHOOTANGLES = new double[] {0, Math.toRadians(45), Math.toRadians(90), Math.toRadians(135), Math.toRadians(180),
		    Math.toRadians(225), Math.toRadians(270), Math.toRadians(315)};//Angulos en los que disparará.
	private static final Point HTZADJUST = new Point(44, 114);
	private boolean attacking = false, chargeattacking = false, bulletattacking = false;
	private double angle;
	private Animation attack1r, attack1l, stay, attack2;
	private SpriteSheet sattack1r, sattack1l, sstay, sattack2;
	private Image bullet = null;
	private MonsterBullet[] bullets;
	private int dir = DIRR, waitcounter = 0;
	private Sound diesound, chargesound, bulletsound;

	public GiantSlime() throws SlickException {
		super(LIFE, ATK, POSX, POSY, HTZADJUST, HTZWIDTH, HTZHEIGHT, true, true);
		sattack1l = new SpriteSheet("res/sprites/enemies/giantslime/attack1l.png", SPRITEWIDTH, SPRITEHEIGHT);
		sattack1r = new SpriteSheet("res/sprites/enemies/giantslime/attack1r.png", SPRITEWIDTH, SPRITEHEIGHT);		
		sattack2 = new SpriteSheet("res/sprites/enemies/giantslime/attack2.png", SPRITEWIDTH, SPRITEHEIGHT);
		sstay = new SpriteSheet("res/sprites/enemies/giantslime/stay.png", SPRITEWIDTH, SPRITEHEIGHT);	
		bullet = new Image("res/sprites/enemies/giantslime/bullet.png");
		diesound = new Sound("res/sound/enemies/giantslime/die.ogg");
		chargesound = new Sound("res/sound/enemies/giantslime/charge.ogg");
		bulletsound = new Sound("res/sound/enemies/giantslime/shoot.ogg");
		attack1l = new Animation(sattack1l, ATTACK1SPRITEDURATION);
		attack1r = new Animation(sattack1r, ATTACK1SPRITEDURATION);
		attack2 = new Animation(sattack2, SPRITEDURATION);
		stay = new Animation(sstay, SPRITEDURATION);
		stay.setPingPong(true);
		attack1l.setLooping(false);
		attack1r.setLooping(false);
		attack2.setPingPong(true);
	}
	
	@Override
	public void draw(Graphics g) {
		if (!dead) {
			drawGiantSlime();	
		}
		else
			if (!isDeathFinalized())
				drawDeath();
		if (bulletattacking && (bullets != null) )
			drawBullets(g);
	}
	
	private void drawGiantSlime() {
		if (!chargeattacking && !bulletattacking) {
			if (!flashdamage)
				stay.draw(pos.getX(), pos.getY());
			else
				stay.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
		}
		else {
			if (chargeattacking) {
				if (dir == DIRR) 
					if (!flashdamage)
						attack1r.draw(pos.getX(), pos.getY());
					else
						attack1r.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
				else
					if (!flashdamage)
						attack1l.draw(pos.getX(), pos.getY());
					else
						attack1l.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
			}
			else
				if (bulletattacking)
					if (!flashdamage)
						attack2.draw(pos.getX(), pos.getY());
					else
						attack2.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));	
		}
	}
	
	private void drawDeath() {
		death.draw(pos.getX(), pos.getY(), death.getWidth()+100, death.getHeight()+100);
	}
	
	private void drawBullets(Graphics g) {
		Color trans = new Color(0f,0f,0f,0.2f);
	    g.setColor(trans);
		for (int i = 0; i < bullets.length; i++) {
			if (!bullets[i].isCollided()){
				g.fillOval(bullets[i].getHitZone().getX(), bullets[i].getHitZone().getY()+3, bullets[i].getHitZone().getWidth(), bullets[i].getHitZone().getHeight());
				bullet.draw(bullets[i].getHitZone().getX(), bullets[i].getHitZone().getY()-1);
			}
		}
	}
	
	@Override
	public void update(PathfindingMap tilemap, int delta , Rectangle area, ArrayList<Obstacle>obstacles, Coord player) throws SlickException {
		if (!dead)	
			behavior(delta, area, obstacles, player);	
		if (bullets != null)
			updateBulletAttack(delta, area, obstacles, player);
	}
	
	//Si no está realizando ninguna acción entonces acumula en un contador el delta hasta llegar a cierto valor, una vez
	//alcanzado ese valor, mediante un random, seleciona la próxima acción a realizar.
	//Si se encuentre realizando una acción, entonces continua con dicha acción.
	private void behavior(int delta , Rectangle area, ArrayList<Obstacle>obstacles, Coord player){
		Random ran = new Random();
		int j;
		if (!chargeattacking && !bulletattacking) { 
			if (waitcounter < WAITTIME)
				waitcounter	+= delta;
			else {
				j = ran.nextInt(2);
				if (j == 0)
					chargeattacking = true;
				else
					bulletattacking = true;
			}
		}
		if (chargeattacking)
			chargeAttack(delta, area, player);
		else
			if (bulletattacking)
				bulletAttack(delta, area, obstacles, player);
	}
	
	//Se lanza hacia el jugador a una elevada velocidad.
	private void chargeAttack(int delta, Rectangle area, Coord player) {
		if (!attacking) {
			//Para saber que sprite dibujar.
			if (player.getHitZone().getCenterX() >= pos.getHitZone().getCenterX())
				dir = DIRR;
			else
				dir = DIRL;
			chargesound.play();
			//Obtiene el angulo hacia el jugador y le da la velocidad para verificar si ya estaba colisionando con la pared.
			angle = Math.toRadians( Math.atan2( (player.getHitZone().getCenterY() - pos.getHitZone().getCenterY()), (player.getHitZone().getCenterX() - pos.getHitZone().getCenterX()) )* 180 / Math.PI);
			float x = (float) (Math.cos((angle)) * ((ACC)*((float)(delta)/1000)));
			float y = (float) (Math.sin((angle)) * ((ACC)*((float)(delta)/1000)));
			Rectangle nexthitzone = new Rectangle(pos.getHitZone().getX()+x, pos.getHitZone().getY()+y, HTZWIDTH, HTZHEIGHT);
			//Si ya se encontraba colisionando alguna de las paredes del area entonces cambia el angulo.
			if (pos.checkAreaColisionX(area, nexthitzone)) {
				if (player.getHitZone().getCenterY() >= pos.getHitZone().getCenterY())
					angle = SHOOTANGLES[2]; //Ángulo 90.
				else
					if (player.getHitZone().getCenterY() < pos.getHitZone().getCenterY())
						angle = SHOOTANGLES[6]; //Ángulo 270.
			}
			else {
				if (pos.checkAreaColisionY(area, nexthitzone)) {
					if (dir == DIRR)
						angle = SHOOTANGLES[0]; //Ángulo 0;
					else
						if (dir == DIRL)
							angle = SHOOTANGLES[4]; //Ángulo de 180.
				}
			}
			attacking = true;
			attack1l.restart();
			attack1r.restart();
		}
		else
			updateChargeAttack(delta, area);
	}
	
	//Mientras no colisione, continua actualizando la posición, caso contrario, termina el ataque.
	private void updateChargeAttack(int delta, Rectangle area) {
		float x = (float) (Math.cos((angle)) * ((ACC)*((float)(delta)/1000)));
		float y = (float) (Math.sin((angle)) * ((ACC)*((float)(delta)/1000)));
		Rectangle nexthitzone = new Rectangle(pos.getHitZone().getX()+x, pos.getHitZone().getY()+y, HTZWIDTH, HTZHEIGHT);
		if ( (pos.checkAreaColisionX(area, nexthitzone)) || (pos.checkAreaColisionY(area, nexthitzone)) ){
			waitcounter = 0;
			chargeattacking = false;
			attacking = false;
		}
		else 
			pos.setPosition(pos.getX()+x, pos.getY()+y, HTZADJUST, HTZWIDTH, HTZHEIGHT);		
	}
	
	//Dispara balas en 8 direcciones.
	private void bulletAttack(int delta , Rectangle area, ArrayList<Obstacle>obstacles, Coord player) {
		if (bullets == null) {
			bulletsound.play();
			bullets = new MonsterBullet[8];
			for (int i = 0; i < bullets.length; i++)
				bullets[i] = new MonsterBullet(new Rectangle(pos.getHitZone().getCenterX()-10, pos.getHitZone().getCenterY()-15, BULLETWIDTH, BULLETHEIGHT), SHOOTANGLES[i], BULLETSPEED);
		}
	}
	
	//Hasta que las balas no hayan colisionado, no termina el ataque.
	private void updateBulletAttack(int delta, Rectangle area, ArrayList<Obstacle>obstacles, Coord player) {
		for (int i = 0; i < bullets.length; i++){
			bullets[i].updateBullet(delta);
			bullets[i].checkCollision(area, obstacles);
		}
		if (allBulletsCollided()){
			bullets = null;
			bulletattacking = false;
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
	public void playDeathSound() {
		if (!diesound.playing())
			diesound.play();
	}
	
	@Override
	public void stopSounds(){
		if (diesound.playing())
			diesound.stop();
		if (chargesound.playing())
			chargesound.stop();
		if (bulletsound.playing())
			bulletsound.stop();
	}
	
	@Override
	public void startAnimations () {	
		death.start();
		attack1l.start();
		attack1r.start();
		attack2.start();
		stay.start();
	}
	
	@Override
	public void stopAnimations () {
		death.stop();
		attack1l.stop();
		attack1r.stop();
		attack2.stop();
		stay.stop();
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
		boolean ban = false, damage = false;
		int i = 0;
		if (bullets != null){
			while ((i < bullets.length) && !damage){
				if (!bullets[i].isCollided()) {
					if (bullets[i].getHitZone().intersects(player.getHitZone())){
						ban = true;
						damage = true;
						bullets[i].setCollided(true);
					}
				}
				i++;
			}
		}
		return ban;
	}

}
