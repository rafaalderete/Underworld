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

//Boss final.
//Posee 4 ataques:
//Sickles: lanza 4 guadañas(balas) hacia el jugador, una detras de otra.
//Mines: Pone minas por todo el area.
//Column: Aparece una columna debajo del jugador cada cierto tiempo.
//Beam: Lanza 2 rayos ocupando casi todo el area, dejandole en el medio un lugar para que el jugador se resguarde.
public class Demon extends Enemy {

	private static final int POSX = 440, POSY = 220, LIFE = 130, ATK = 2, SPRITEWIDTH = 168, SPRITEHEIGHT = 176, SPRITEDURATION = 150, SPRITEBEAMWIDTH = 535, SPRITEBEAMHEIGHT = 685, SPRITEPREBEAMDIMENSION = 50,
			SPRITECOLUMNWIDTH = 61, SPRITECOLUMNHEIGHT = 187, SPRITEMINEWIDTH = 62, SPRITEMINEHEIGHT = 60, SICKLESPRITEDURATION = 40, BEAMMINESPRITEDURATION = 70, COLUMNSPRITEDURATION = 50, SPRITESICKLEWIDTH = 53, SPRITESICKLEHEIGHT = 50, HTZWIDTH = 50, HTZHEIGHT = 30,
			MAXSICKLES = 4, HITZONESICKLEWIDTH = 33, HITZONESICKLEHEIGHT = 30, HITZONEMINEWIDTH = 42, HITZONEMINEHEIGHT = 40, MAXMINES = 25, MAXCOLUMNS = 8, HITZONECOLUMNWIDTH = 55, HITZONECOLUMNHEIGHT = 20, SICKLESPEED = 900, HITZONEBEAMWIDTH = 380, HITZONEBEAMHEIGHT = 400,
			WAITTIME = 800, BEAMATTACKTIME = 2000, SICKLETIME = 700, CHARGEBEAMATTACKTIME = 1000, PREATTACKTIME = 600, COLUMNWAITTIME = 450, COLUMNTIME = 350, MINETIME = 15000, PREPAREMINETIME = 1000, MAXMINEX = 800, MINMINEX = 80,
			MAXMINEY = 320, MINMINEY = 160;
	private static final Point HTZADJUST = new Point(58, 147), SICKLEU = new Point(500,  190), SICKLER = new Point(600, 250), SICKLED = new Point(500, 360), 
			SICKLEL = new Point(400, 250), PREBEAMLPOS = new Point(420, 230), PREBEAMRPOS = new Point(580, 230), BEAMLPOS = new Point(-20, 20), BEAMRPOS = new Point(532, 20), HITZONEBEAMLPOS = new Point(80, 160),
			HITZONEBEAMRPOS = new Point(590, 160), ADJUSTCOLUMNPLAYERPOS = new Point(15, 160), ADJUSTHITZONECOLUMN = new Point(5, 165);
	private SpriteSheet sattack, spreattack, sstay, sbeaml, sbeamr, sprebeam, ssickle, smine, scolumn;
	private Animation attack, preattack, stay, beaml, beamr, prebeam, sickle, mine, column;
	private Image seal, premine;
	private boolean attacking = false, beamattacking = false, sicklesattacking = false, truepowerplayed = false, mineattacksoundplayed = false, columnattacksoundplayed = false, mineattacking = false, columnattacking = false,
			columnplayerposobtained = false, minesprepared = false, samemineattack = false;
	private Rectangle hitzonebeaml = null, hitzonebeamr = null, hitzonecolumn = null;
	private ArrayList<Rectangle> arraymines;
	private int previousattack, waitcounter = 0, preattackcounter = 0, beamcounter = 0, chargebeamcounter = 0, sicklecounter = 0, amountsickles = 0, prepareminecounter = 0,
			minecounter = 0, amountcolumns = 0, columnwaitcounter = 0, columncounter = 0;
	private Point columnplayerpos;
	private MonsterBullet[] arraysickles;
	private Sound diesound, truepowersound, beamsound, sickleattacksound, sicklethrow, mines, touchmine, columnrise, columnattacksound, mineattacksound;

	public Demon() throws SlickException {
		super(LIFE, ATK, POSX, POSY, HTZADJUST, HTZWIDTH, HTZHEIGHT, false, true);
		sattack = new SpriteSheet("res/sprites/enemies/demon/attack.png", SPRITEWIDTH, SPRITEHEIGHT);
		spreattack = new SpriteSheet("res/sprites/enemies/demon/preattack.png", SPRITEWIDTH, SPRITEHEIGHT);		
		sstay = new SpriteSheet("res/sprites/enemies/demon/stay.png", SPRITEWIDTH, SPRITEHEIGHT);	
		ssickle = new SpriteSheet("res/sprites/enemies/demon/sickle.png", SPRITESICKLEWIDTH, SPRITESICKLEHEIGHT);	
		sbeaml= new SpriteSheet("res/sprites/enemies/demon/beaml.png", SPRITEBEAMWIDTH, SPRITEBEAMHEIGHT);	
		sbeamr = new SpriteSheet("res/sprites/enemies/demon/beamr.png", SPRITEBEAMWIDTH, SPRITEBEAMHEIGHT);	
		sprebeam = new SpriteSheet("res/sprites/enemies/demon/prebeam.png", SPRITEPREBEAMDIMENSION, SPRITEPREBEAMDIMENSION);
		scolumn = new SpriteSheet("res/sprites/enemies/demon/column.png", SPRITECOLUMNWIDTH, SPRITECOLUMNHEIGHT);	
		smine = new SpriteSheet("res/sprites/enemies/demon/mine.png", SPRITEMINEWIDTH, SPRITEMINEHEIGHT);	
		seal = new Image("res/sprites/enemies/demon/seal.png");
		premine = new Image("res/sprites/enemies/demon/premine.png");
		diesound = new Sound("res/sound/enemies/demon/die.ogg");
		truepowersound = new Sound("res/sound/enemies/demon/truepower.ogg");
		sickleattacksound = new Sound("res/sound/enemies/demon/sickleattacksound.ogg");
		sicklethrow = new Sound("res/sound/enemies/demon/sicklethrow.ogg");
		mines = new Sound("res/sound/enemies/demon/mines.ogg");
		touchmine = new Sound("res/sound/enemies/demon/touchmine.ogg");
		columnrise = new Sound("res/sound/enemies/demon/columnrise.ogg");
		columnattacksound = new Sound("res/sound/enemies/demon/columnattacksound.ogg");
		mineattacksound = new Sound("res/sound/enemies/demon/mineattacksound.ogg");
		beamsound = new Sound("res/sound/enemies/demon/beam.ogg");
		attack = new Animation(sattack, SPRITEDURATION);
		preattack = new Animation(spreattack, SPRITEDURATION);
		stay = new Animation(sstay, SPRITEDURATION);
		sickle = new Animation(ssickle, SICKLESPRITEDURATION);
		beaml = new Animation(sbeaml, BEAMMINESPRITEDURATION);
		beamr = new Animation(sbeamr, BEAMMINESPRITEDURATION);
		prebeam = new Animation(sprebeam, BEAMMINESPRITEDURATION);
		column = new Animation(scolumn, COLUMNSPRITEDURATION);
		mine = new Animation(smine, BEAMMINESPRITEDURATION);
		attack.setPingPong(true);
		preattack.setLooping(false);
		stay.setPingPong(true);
		sickle.setLooping(true);
		beaml.setPingPong(true);
		beamr.setLooping(true);
		prebeam.setPingPong(true);
		column.setLooping(false);
		mine.setLooping(true);
	}
	
	@Override
	public void draw(Graphics g) {
		boolean columnsdrawed = false;
		if (!dead) {
			if (arraymines != null)
				drawMines();
			if (columnattacking && (columnplayerpos != null) ){ //Si la posición Y del ataque de la columna es menor que la posición Y del boss, entonces la dibuja primero.
				if ((columnplayerpos.getY()+ADJUSTHITZONECOLUMN.getY() < pos.getHitZone().getCenterY()) && (!columnsdrawed)) {
					drawColumn();
					columnsdrawed = true;
				}
			}
			drawShadow(g);
			drawDemon();
			if (beamattacking)
				drawBeam();
			if (sicklesattacking)
				drawSickles();
			if (!columnsdrawed && columnattacking && (columnplayerpos != null) ) //Si el ataque de la columna no se ha dibujado antes, entonces la dibuja.
				drawColumn();
		}
		else
			if(!isDeathFinalized())
					drawDeath();
	}
	
	private void drawDemon() {
		if (!attacking)
			if (!flashdamage)
				stay.draw(pos.getX(), pos.getY());
			else
				stay.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
		else {
			//Animación que levanta los brazos.
			if (preattackcounter < PREATTACKTIME) {
				if (!flashdamage)
					preattack.draw(pos.getX(), pos.getY());
				else
					preattack.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
			}
			else {
				//Animación con los brazos levantados.
				if (!flashdamage)
					attack.draw(pos.getX(), pos.getY());
				else
					attack.drawFlash(pos.getX(), pos.getY(), SPRITEWIDTH, SPRITEHEIGHT, new Color(Color.red));
			}
		}
	}
	
	private void drawDeath() {	
		death.draw(pos.getX(), pos.getY(), death.getWidth()+100, death.getHeight()+100);
	}
	
	private void drawShadow (Graphics g) {
		Color trans = new Color(0f,0f,0f,0.2f);
	    g.setColor(trans);
	    g.fillOval(pos.getHitZone().getX(), pos.getHitZone().getY()+10, pos.getHitZone().getWidth(), pos.getHitZone().getHeight()-5);
	}
	
	//Si el hitzone de las guadañas en nulo, entonces la dibuja quieta, caso contrario dibuja la animación en la posición de la bala.
	private void drawSickles() {
		if (preattackcounter >= PREATTACKTIME) {
			if (arraysickles[0] == null)
				ssickle.getSprite(0, 0).draw(SICKLEU.getX(), SICKLEU.getY());
			else
				if (!arraysickles[0].isCollided())
					sickle.draw(arraysickles[0].getHitZone().getX()-10, arraysickles[0].getHitZone().getY()-10);
			if (arraysickles[1] == null)
				ssickle.getSprite(6, 0).draw(SICKLER.getX(), SICKLER.getY());
			else
				if (!arraysickles[1].isCollided())
					sickle.draw(arraysickles[1].getHitZone().getX()-10, arraysickles[1].getHitZone().getY()-10);
			if (arraysickles[2] == null)
				ssickle.getSprite(4, 0).draw(SICKLED.getX(), SICKLED.getY());
			else
				if (!arraysickles[2].isCollided())
					sickle.draw(arraysickles[2].getHitZone().getX()-10, arraysickles[2].getHitZone().getY()-10);
			if (arraysickles[3] == null)
				ssickle.getSprite(2, 0).draw(SICKLEL.getX(), SICKLEL.getY());
			else
				if (!arraysickles[3].isCollided())
					sickle.draw(arraysickles[3].getHitZone().getX()-10, arraysickles[3].getHitZone().getY()-10);
		}
	}
	
	//Primero dibujará las minas sin armar, pasado el tiempo, dibujará la animación de mina armada.
	private void drawMines() {
		for (int i = 0; i < arraymines.size(); i++){
			if (!minesprepared)
				premine.draw(arraymines.get(i).getX()-10, arraymines.get(i).getY()-10);
			else 
				mine.draw(arraymines.get(i).getX()-10, arraymines.get(i).getY()-10); 
		}		
	}
	
	//Primero dibujará la marca de donde saldrá, pasado el tiempo, dibujará la columna.
	private void drawColumn() {
		if (columnwaitcounter < COLUMNWAITTIME)
			seal.draw(columnplayerpos.getX(), columnplayerpos.getY());
		else
			column.draw(columnplayerpos.getX(), columnplayerpos.getY());
	}
	
	//Primero dibujara las 2 esferas rosas, pasado el tiempo, dibujará los 2 rayos.
	private void drawBeam() {
		if ( (hitzonebeaml == null) && (hitzonebeamr == null) && (preattackcounter >= PREATTACKTIME) ) {
			prebeam.draw(PREBEAMLPOS.getX(), PREBEAMLPOS.getY());
			prebeam.draw(PREBEAMRPOS.getX(), PREBEAMRPOS.getY());
		}
		else {
			if ( (hitzonebeaml != null) && (hitzonebeamr != null) ){
				beaml.draw(BEAMLPOS.getX(), BEAMLPOS.getY());
				beamr.draw(BEAMRPOS.getX(), BEAMRPOS.getY());
			}
		}
	}
	
	@Override
	public void update(PathfindingMap tilemap, int delta , Rectangle area, ArrayList<Obstacle>obstacles, Coord player) throws SlickException {
		if (!dead){
			behavior(delta, player);
			if (arraysickles != null)
				updateSickles(area, delta);
			if (arraymines != null)
				updateMines(delta);
		}	
	}
	
	//Si no está realizando ninguna acción entonces acumula en un contador el delta hasta llegar a cierto valor, una vez
	//alcanzado ese valor, mediante un random, seleciona la próxima acción a realizar.
	//Si se encuentre realizando una acción, entonces continua con dicha acción.
	private void behavior(int delta, Coord player){
		Random ran = new Random();
		int j;
		if (attacking) 
			preattackcounter += delta; //Todos los ataques se realizan cuando el preattackcounter es mayor al preattacktime, para que el ataque no salga mientra realiza la animación de levantar los brazos.
		if (waitcounter < WAITTIME)
			waitcounter += delta;
		else {
			attacking = true;
			if (!beamattacking && !sicklesattacking && !mineattacking && !columnattacking){
				if (previousattack == 3)
					j = ran.nextInt(3);
				else
					j = ran.nextInt(4);
				if (j == 0){
					sicklesattacking = true;
					previousattack = 0;
				}
				else
					if (j == 1) {
						mineattacking = true;
						previousattack = 1;
					}
					else
						if (j == 2) {
							columnattacking = true;
							previousattack = 2;
						}
						else {
							beamattacking = true;
							previousattack = 3;
						}
			}
			if (beamattacking)
				beamAttack(delta);
			else
				if (sicklesattacking)
					sickleAttack(delta, player);
				else
					if (mineattacking)
						minesAttack(delta);
					else
						if (columnattacking)
							columnAttack(delta, player);
		}		
	}
	
	//Dibuja 2 rayos hacia los costados durante un tiempo, ocupando casi todo el area, una vez transcurrido el tiempo termina el ataque.
	private void beamAttack(int delta) {
		if ( (preattackcounter >= PREATTACKTIME) && !truepowerplayed)
			if (!truepowersound.playing()){
				truepowersound.play();
				truepowerplayed = true;
			}
		if (truepowerplayed)
			chargebeamcounter += delta; //Contador para darle tiempo al jugador a reaccionar y durante el cual se dibujaran el preattackbeam.
		if ((hitzonebeaml == null) && (hitzonebeamr == null) && (preattackcounter >= PREATTACKTIME) && (chargebeamcounter >= CHARGEBEAMATTACKTIME) ) {
			hitzonebeaml = new Rectangle(HITZONEBEAMLPOS.getX(), HITZONEBEAMLPOS.getY(), HITZONEBEAMWIDTH, HITZONEBEAMHEIGHT);
			hitzonebeamr = new Rectangle(HITZONEBEAMRPOS.getX(), HITZONEBEAMRPOS.getY(), HITZONEBEAMWIDTH, HITZONEBEAMHEIGHT);
			if (!beamsound.playing())
				beamsound.play();
		}
		if ( (hitzonebeaml != null) && (hitzonebeamr != null) ){ //Si los rayos no son nulos, comienza el contador.
			if (beamcounter < BEAMATTACKTIME) //Tiempo que permanecerá el rayo.
				beamcounter += delta;
			else {
				hitzonebeaml = null;
				hitzonebeamr = null;
				beamcounter = 0;
				chargebeamcounter = 0;
				attacking = false;
				beamattacking = false;
				truepowerplayed = false;
				preattack.restart();
				waitcounter = 0;
				preattackcounter = 0;
			}
		}
	}
	
	//Lanza guadañas al jugador cada cierto tiempo. 
	//Lanza 4 guadañas de a una a la vez.
	//Cuando todas las guadañas han colisionado, termina el ataque.
	private void sickleAttack(int delta, Coord player) {
		//Crea las guadañas inmoviles.
		if (arraysickles == null) {
			sickleattacksound.play();
			arraysickles = new MonsterBullet[MAXSICKLES];
			for (int i = 0; i < arraysickles.length; i++)
				arraysickles[i] = null;
		}
		else {
			//Lanza las guadañas de a una a la vez.
			if (preattackcounter >= PREATTACKTIME) {
				if (amountsickles < MAXSICKLES) {
					if (sicklecounter < SICKLETIME) { //Tiempo entre cada guadaña.
						sicklecounter += delta;
					}
					else {
						//Dependiendo el amountsickles, es la siguiente guadaña a lanzar.
						if (amountsickles == 0)
							arraysickles[0] = new MonsterBullet(new Rectangle(SICKLEU.getX(), SICKLEU.getY(), HITZONESICKLEWIDTH, HITZONESICKLEHEIGHT), player.getHitZone().getCenterX(), player.getHitZone().getCenterY(), SICKLESPEED);
						if (amountsickles == 1)
							arraysickles[1] = new MonsterBullet(new Rectangle(SICKLER.getX(), SICKLER.getY(), HITZONESICKLEWIDTH, HITZONESICKLEHEIGHT), player.getHitZone().getCenterX(), player.getHitZone().getCenterY(), SICKLESPEED);
						if (amountsickles == 2)
							arraysickles[2] = new MonsterBullet(new Rectangle(SICKLED.getX(), SICKLED.getY(), HITZONESICKLEWIDTH, HITZONESICKLEHEIGHT), player.getHitZone().getCenterX(), player.getHitZone().getCenterY(), SICKLESPEED);
						if (amountsickles == 3)
							arraysickles[3] = new MonsterBullet(new Rectangle(SICKLEL.getX(), SICKLEL.getY(), HITZONESICKLEWIDTH, HITZONESICKLEHEIGHT), player.getHitZone().getCenterX(), player.getHitZone().getCenterY(), SICKLESPEED);
						sicklethrow.play();
						amountsickles++;
						sicklecounter = 0;
					}
				}
				else {
					if (allSicklesCollided()) {
						arraysickles = null;
						amountsickles = 0;
						attacking = false;
						sicklesattacking = false;
						preattack.restart();
						waitcounter = 0;
						preattackcounter = 0;
					}
				}
			}
		}
	}
	
	//Crea minas en el area, se le da al jugador un tiempo para reaccionar antes de que la mine se arme y haga daño.
	//Una vez armadas las minas, termina el ataque.
	//Las minas permanceran en el area por un largo tiempo o hasta que se realice de nuevo el ataque.
	private void minesAttack(int delta) {
		Random ran = new Random();
		if (!mineattacksound.playing() && !mineattacksoundplayed){
			mineattacksound.play();
			mineattacksoundplayed = true;
		}
		if (preattackcounter >= PREATTACKTIME){
			if (arraymines == null || !samemineattack) {
				arraymines = new ArrayList<Rectangle>();
				mines.play();
				samemineattack = true; //Para que solamente cree las minas una vez por ataque.
				minesprepared = false; 
				minecounter = 0;
				for (int i = 0; i < MAXMINES; i++) //Crea las minas sin armar.
					arraymines.add(new Rectangle(ran.nextInt(MAXMINEX)+MINMINEX, ran.nextInt(MAXMINEY)+MINMINEY, HITZONEMINEWIDTH, HITZONEMINEHEIGHT));
			}
			else {
				if (prepareminecounter < PREPAREMINETIME) //Tiempo para que se armen las minas.
					prepareminecounter += delta;
				else {
					samemineattack = false;
					mineattacksoundplayed = false;
					minesprepared = true;
					attacking = false;
					mineattacking = false;
					preattack.restart();
					prepareminecounter = 0;
					waitcounter = 0;
					preattackcounter = 0;
				}
			}
		}		
	}
	
	//Lanza columnas que salen del piso en la posición del jugador, se le da un tiempo para que el jugador reaccione y durante el cual se dibuja una marca que es de donde saldrá la columna.
	//Las columnas permaneran por un tiempo.
	//Una vez lanzadas todas las columnas, termina el ataque.
	private void columnAttack(int delta, Coord player) {
		if (!columnattacksound.playing() && !columnattacksoundplayed) {
			columnattacksound.play();
			columnattacksoundplayed = true;
		}
		if (preattackcounter >= PREATTACKTIME) {
			if (amountcolumns < MAXCOLUMNS) {
				if (!columnplayerposobtained){ //Captura la posición del jugador una vez por columna.
					columnplayerpos = new Point(player.getHitZone().getX()-ADJUSTCOLUMNPLAYERPOS.getX(), player.getHitZone().getY()-ADJUSTCOLUMNPLAYERPOS.getY());
					columnplayerposobtained = true;
				}
				if (columnwaitcounter < COLUMNWAITTIME) { //Tiempo para que el jugador reaccione.
					columnwaitcounter += delta;
				}
				else {
					if (hitzonecolumn == null) {
						columnrise.play();
						hitzonecolumn = new Rectangle(columnplayerpos.getX()+ADJUSTHITZONECOLUMN.getX(), columnplayerpos.getY()+ADJUSTHITZONECOLUMN.getY(), HITZONECOLUMNWIDTH, HITZONECOLUMNHEIGHT);
					}
					if (columncounter < COLUMNTIME) { //Tiempo que permanecera la columna una vez creada.
						columncounter += delta;
					}
					else { //Terminado el tiempo, continua con la siguiente columna.
						column.restart();
						columnplayerposobtained = false;
						hitzonecolumn = null;
						columncounter = 0;
						columnwaitcounter = 0;
						amountcolumns++;
					}
				}
			}
			else {
				attacking = false;
				columnattacking = false;
				preattack.restart();
				columnplayerpos = null;
				waitcounter = 0;
				preattackcounter = 0;
				amountcolumns = 0;
				columnattacksoundplayed = false;
			}
		}
	}
	
	//Movimiento de las guadañas.
	private void updateSickles(Rectangle area, int delta) {
		for (int i = 0; i < arraysickles.length; i++) {
			if (arraysickles[i] != null) {
				arraysickles[i].updateBullet(delta);
				checkSickleColision(area, arraysickles[i]);
			}
		}
	}
	
	//Tiempo que duraran las minas.
	private void updateMines(int delta) {
		if (minecounter < MINETIME)
			minecounter += delta;
		else {
			arraymines = null;
			minecounter = 0;
		}
	}
	
	//Colisión de las guadañas con los bordes de area.
	private void checkSickleColision (Rectangle area, MonsterBullet sickle) {
		if (!sickle.getHitZone().intersects(area))
			sickle.setCollided(true);
	}
	
	//Si colisionaron tanto con el jugador como con los bordes.
	private boolean allSicklesCollided () {
		boolean ban = true;
		for (int i = 0; i < arraysickles.length; i++) 
			if (arraysickles[i] == null)
				ban = false;
			else
				if (!arraysickles[i].isCollided())
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
		if (truepowersound.playing())
			truepowersound.stop();
		if (beamsound.playing())
			beamsound.stop();
		if (sickleattacksound.playing())
			sickleattacksound.stop();
		if (sicklethrow.playing())
			sicklethrow.stop();
		if (mineattacksound.playing())
			mineattacksound.stop();
		if (mines.playing())
			mines.stop();
		if (touchmine.playing())
			touchmine.stop();
		if (columnattacksound.playing())
			columnattacksound.stop();
		if (columnrise.playing())
			columnrise.stop();
	}
		
	@Override
	public void startAnimations () {	
		death.start();
		preattack.start();
		attack.start();
		stay.start();
		prebeam.start();
		beaml.start();
		beamr.start();
		sickle.start();
		mine.start();
		column.start();
	}
	
	@Override
	public void stopAnimations () {
		death.stop();
		preattack.stop();
		attack.stop();
		stay.stop();
		prebeam.stop();
		beaml.stop();
		beamr.stop();
		sickle.stop();
		mine.stop();
		column.stop();
		stopSounds();
	}
	
	@Override
	public boolean checkAttack (Coord player) {
		boolean ban = false;
		if (pos.getHitZone().intersects(player.getHitZone()) || checkBeamAttack(player) || checkSickleAttack(player) ||
				checkMineAttack(player) || checkColumnttack(player) )
			ban = true;
		return ban;
	}
	
	//Los 2 rayos.
	private boolean checkBeamAttack(Coord player) {
		boolean ban = false;
		if ( (hitzonebeaml != null) && (hitzonebeamr != null))
			if ( (hitzonebeaml.intersects(player.getHitZone())) || (hitzonebeamr.intersects(player.getHitZone())) )
				ban = true;
		return ban;
	}
	
	//Las guadañas solo golpearán cuando su hitzone no es nulo.
	private boolean checkSickleAttack(Coord player) {
		boolean ban = false, damage = false;
		int i = 0;
		if (arraysickles != null) {
			i = 0;
			damage = false;
			while ( (i < arraysickles.length) && !damage) {
				if (arraysickles[i] != null) {
					if ( (arraysickles[i].getHitZone().intersects(player.getHitZone()) ) && (!arraysickles[i].isCollided()) ){
						ban = true;
						damage = true;
						arraysickles[i].setCollided(true);
					}
				}
				i++;
			}
		}
		return ban;
	}
	
	//Las minas attack haran daño si estan armadas.
	private boolean checkMineAttack(Coord player) {
		boolean ban = false, damage = false;
		int i = 0;
		if (arraymines != null) {
			damage = false;
			i = 0;
			while ( (i < arraymines.size()) && !damage) {
				if ( (arraymines.get(i).intersects(player.getHitZone())) && minesprepared ) {
					ban = true;
					damage = true;
					arraymines.remove(i);
					touchmine.play();
				}
				i++;
			}
		}
		return ban;
	}
	
	//Columan que sale del piso.
	private boolean checkColumnttack(Coord player) {
		boolean ban = false;
		if (hitzonecolumn != null)
			if (hitzonecolumn.intersects(player.getHitZone()))
				ban = true;
		return ban;
	}

}