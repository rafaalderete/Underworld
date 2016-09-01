package states;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import bar.StatusBar;
import map.Map;
import map.Zone;
import monsters.Enemy;
import player.Bullet;
import player.Player;

//Estado principal del juego.
public class Game extends BasicGameState {
	
	private static final Point POSUP = new Point(455, 110), TILEPOSUP = new Point(6, 1), POSRIGHT = new Point(860, 270), TILEPOSRIHT = new Point(11, 3), //
			POSDOWN = new Point(455, 435), TILEPOSDOWN = new Point(6, 5), POSLEFT = new Point(40, 270), TILEPOSLEFT = new Point(1, 3), 					 //Posiciones en pixeles y tiles para cuando se cambia de zona y nivel.				
			CONTROLSPOS = new Point(270, 200) ; 	
	private static final int GAMEOVERTRANSITIONTIME = 6000,	FIRTSLEVEL = 0, LASTLEVEL = 2, DIRR = 1, DIRU = 0 , DIRD = 2;;
	private static final float volumenpause = 0.5f;
	private boolean paused = false, levelchange = false;
	private Music music, bossmusic;
	private Image controls;
	private Player player;
	private StatusBar statusbar;
	private Map map;
	private Rectangle area;
	private Timer gameovertimer;

	@Override
	public void init(GameContainer container, final StateBasedGame game)
			throws SlickException {
		area = new Rectangle(80,160,container.getWidth()-160,container.getHeight()-240);
		map = new Map();
		player = new Player(container, area);
		statusbar = new StatusBar(player);
		controls = new Image("res/sprites/menu/controls.png");
		music = new Music("res/sound/music/music.ogg");
		bossmusic = new Music("res/sound/music/bossmusic.ogg");
		gameovertimer = new Timer(GAMEOVERTRANSITIONTIME, new ActionListener() { //Tiempo que pasara al estado de gameover, una vez muerto el jugador.
			
			@Override
			public void actionPerformed(ActionEvent arg0) {	
				gameovertimer.stop();
				game.enterState(2 ,new FadeOutTransition(), new FadeInTransition());
				
			}
		});
	}
	
	//Cada vez que entra a este estado verifica si viene de una pausa o cambio de nivel.
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		container.getInput().clearKeyPressedRecord(); //Para que no detecte las teclas presionadas mientras no estaba en Game.
		player.resetDeath();
		if (paused) 
			leavePause();
		else 
			if (levelchange) 
				leaveTransition();
			else 
				leaveMenu(container);
	}
	
	//Al salir del estado detiene la musica y todas las animaciones.
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		stopAllAnimations();
		if (levelchange) {
			music.stop();
			if (map.getCurrentLevel() == LASTLEVEL)
				levelchange = false; //Para que cuando se inicie de nuevo el juego no quede en true. 
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		if (!player.isDead()) {
			map.draw();
			if (map.getCurrentLevel() == FIRTSLEVEL && map.getZones()[(int)map.getCurrentZone().getX()][(int)map.getCurrentZone().getY()].isStart() )
				controls.draw(CONTROLSPOS.getX(), CONTROLSPOS.getY());
			drawMonsterPlayerAndDrop(g);
			statusbar.draw(g, map.getZones());
			player.getBulletmanaget().draw(g);
		}
		else
			player.draw(g); //Solo dibujará la animación de muerte.
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
		throws SlickException {
		if (!player.isDead()) {		
			checkPause(container, game);
			map.update(delta, area, player.getCoord());
			player.update(delta, map.getZones()[(int)map.getCurrentZone().getX()][(int)map.getCurrentZone().getY()].getObstacles());
			player.getBulletmanaget().update(delta, map.getZones()[(int)map.getCurrentZone().getX()][(int)map.getCurrentZone().getY()].getObstacles());	
			hitMonsters();
			hitPlayer();
			checkCollisionDrop();
			changezone();	
			changeLevel(game);
		}
		else
			gameOver(game);
	}
	
	//Cada vez que se toca una puerta, elimina todas las balas y los monstruos y avanza a la siguiente zona en dirección a la puerta.
	private void changezone () {
		Zone zone = map.getZones()[(int)map.getCurrentZone().getX()][(int)map.getCurrentZone().getY()];
		if (zone.isCleared()) {
			for (int i = 0; i < zone.getDoors().size(); i++) {
				if (zone.getDoors().get(i).getColisionZone().intersects(player.getCoord().getHitZone())) {
					player.getBulletmanaget().clearBullets();
					zone.removeMonsters();
					if (zone.getDoors().get(i).getDoorDirection() == DIRD) {
						player.getCoord().setPosition(POSUP.getX(), POSUP.getY(), player.getHTZAdjust(), player.getHTZWidth(), player.getHTZHeight());
						player.getCoord().setTilepos(TILEPOSUP);
						map.changeZone(new Point(map.getCurrentZone().getX(), map.getCurrentZone().getY()+1));
					}
					else {
						if (zone.getDoors().get(i).getDoorDirection() == DIRU) {
							player.getCoord().setPosition(POSDOWN.getX(), POSDOWN.getY(), player.getHTZAdjust(), player.getHTZWidth(), player.getHTZHeight());
							player.getCoord().setTilepos(TILEPOSDOWN);
							map.changeZone(new Point(map.getCurrentZone().getX(), map.getCurrentZone().getY()-1));
						}
						else {
							if (zone.getDoors().get(i).getDoorDirection() == DIRR) {
								player.getCoord().setPosition(POSLEFT.getX(), POSLEFT.getY(), player.getHTZAdjust(), player.getHTZWidth(), player.getHTZHeight());
								player.getCoord().setTilepos(TILEPOSLEFT);
								map.changeZone(new Point(map.getCurrentZone().getX()+1, map.getCurrentZone().getY()));
							}
							else {
								player.getCoord().setPosition(POSRIGHT.getX(), POSRIGHT.getY(), player.getHTZAdjust(), player.getHTZWidth(), player.getHTZHeight());
								player.getCoord().setTilepos(TILEPOSRIHT);
								map.changeZone(new Point(map.getCurrentZone().getX()-1, map.getCurrentZone().getY()));
							}
						}
					}
				}
				if (map.getZones()[(int)map.getCurrentZone().getX()][(int)map.getCurrentZone().getY()].isEnd()) { //Cuando entra a la zona del boss se cambia de musica.
					if (music.playing()){
						music.stop();
						bossmusic.loop();
					}
				}
			}		
		}
	}
	
	//Al tocar la escalera se cambia de nivel, controlando que se haya recogido el drop, exceptuando el último nivel.
	private void changeLevel(StateBasedGame game) throws SlickException {
		Zone zone = map.getZones()[(int)map.getCurrentZone().getX()][(int)map.getCurrentZone().getY()];
		if (map.getCurrentLevel() == LASTLEVEL) {
			if (zone.isCleared() && zone.isEnd()){
				if (zone.getLadder().intersects(player.getCoord().getHitZone())){
					levelchange = true;
					game.enterState(6, new FadeOutTransition(), new FadeInTransition());
				}
			}
		}
		else {
			if (zone.isCleared() && zone.isEnd() && zone.getDrop().isObtained()){
				if (zone.getLadder().intersects(player.getCoord().getHitZone())){
					levelchange = true;
					game.enterState(6, new FadeOutTransition(), new FadeInTransition());
				}
			}
		}
	}
	
	//Chequea si los monstruos fueron golpeados por cualquiera de los 2 ataques del jugador.
	//Luego de chequear con cada ataque, si hubo algun golpe, revisa los monstruos que deben ser seteados muertos.
	private void hitMonsters () {
		boolean hitban = false;
		Zone zone = map.getZones()[(int)map.getCurrentZone().getX()][(int)map.getCurrentZone().getY()];
		ArrayList<Enemy> monsters = zone.getMonsters();
		ArrayList<Bullet> actualbullets = player.getBulletmanaget().getBullets();
		//Ataque de bayoneta.
 		if (player.isAttacking() && !player.isAlreadyattacked()){
			player.setAlreadyattacked(true);
			for (int i = 0; i < monsters.size(); i++) {
				if (!monsters.get(i).isDead()){
					if (monsters.get(i).getCoord().getHitZone().intersects(player.getAttackzone())) {
						if (!monsters.get(i).isDamaged()){
							hitban = true;
							monsters.get(i).setDamaged(true);
							monsters.get(i).setLife(monsters.get(i).getLife()-player.getBayonetAtk());
						}
					}
				}
			}
			if (hitban) //Sonidos de golpe o fallo dependiendo si se golpeó algun monstruo.
				player.hitMonster();
			else
				player.missMonster();
		}
		else {
			if (!player.isAttacking()) { //Cuando termina la aanimacion de ataque, setea a los monstruos para que puedan ser dañados de nuevo.
				player.setAlreadyattacked(false);
				for (int i = 0; i < monsters.size(); i++) 
					monsters.get(i).setDamaged(false);
			}
		}
 		if (hitban)
 			zone.checkMonstersLife();
 		hitban = false;
 		//Ataque de disparo.
		if (actualbullets.size() > 0) {
			for (int i = 0; i < monsters.size(); i++) {
				if (!monsters.get(i).isDead()){
					for (int j = 0; j < actualbullets.size(); j++) {
						if (monsters.get(i).getCoord().getHitZone().intersects(actualbullets.get(j).getHitZone())) {
							actualbullets.remove(j);
							hitban = true;
							monsters.get(i).setLife(monsters.get(i).getLife()-player.getGunAtk());
						}		
						if (actualbullets.size() == 0)
							break;
					}
				}
			}
		}
		if (hitban)
			zone.checkMonstersLife();
		if (zone.isEnd() && zone.isCleared()) //Al matar al boss se detiene la musica.
			bossmusic.stop();
	}
	
	//Chequea si el jugador fue golpeado.
	private void hitPlayer () {
		ArrayList<Enemy> monsters = map.getZones()[(int)map.getCurrentZone().getX()][(int)map.getCurrentZone().getY()].getMonsters();
		for (int i = 0; i < monsters.size(); i++) {
			if (!monsters.get(i).isDead()) { //Controla todos los ataques.
				if (monsters.get(i).checkAttack(player.getCoord()) && !player.isDamaged()) {
					player.setLife(player.getLife()-monsters.get(i).getAtk());
					player.startDamagedTime();
				}
			}
			else {
				if (monsters.get(i).shootsBullets()) { // Cuando el mosntruo está muerto solo controla que balas que dispara.
					if (monsters.get(i).checkBulletsAttack(player.getCoord()) && !player.isDamaged()) {
						player.setLife(player.getLife()-monsters.get(i).getAtk());
						player.startDamagedTime();
					}
				}
			}
		}
	}
	
	//Chequea la colisión del jugador con el drop.
	private void checkCollisionDrop() {
		Zone zone = map.getZones()[(int)map.getCurrentZone().getX()][(int)map.getCurrentZone().getY()];
		if (zone.getDrop() != null) {
			if (zone.getDrop().isDropped() && !zone.getDrop().isObtained()) {
				if (zone.getDrop().getHitzone().intersects(player.getCoord().getHitZone()))
					zone.getDrop().effect(player);
			}
		}
	}
	
	//Dibuja los monstruos, drop y el personaje(ordenados mediante la posición Y del hitzone).
	private void drawMonsterPlayerAndDrop(Graphics g) {
		Zone zone = map.getZones()[(int)map.getCurrentZone().getX()][(int)map.getCurrentZone().getY()];
		ArrayList<Enemy> monsters = zone.getMonsters();
		boolean ban = false;
		for (int i = 0; i < monsters.size(); i++){
			//Antes de dibujar el monstruo verifica las coordenadas Y, si la del jugador es menor entonces lo dibuja primero,
			//controlando a su vez las coordenadas Y del drop. 
			if ( (monsters.get(i).getCoord().getHitZone().getCenterY() > player.getCoord().getHitZone().getCenterY()) && !ban) {
				if (zone.isCleared() && zone.getDrop() != null) {
					if (zone.getDrop().getHitzone().getCenterY() < player.getCoord().getHitZone().getCenterY()) {
						zone.drawDrop(g);
						player.draw(g);
					}
					else {
						player.draw(g);
						zone.drawDrop(g);
					}
				}
				else
					player.draw(g);
				ban = true;
			}
			monsters.get(i).draw(g);
		}
		//Si no se dibujó el jugador entre los monstruos entonces se lo dibuja al final, controlando con las coodenadas del drop.
		if (!ban) {
			if (zone.isCleared() && zone.getDrop() != null) {
				if (zone.getDrop().getHitzone().getCenterY() < player.getCoord().getHitZone().getCenterY()) {
					zone.drawDrop(g);
					player.draw(g);
				}
				else {
					player.draw(g);
					zone.drawDrop(g);
				}
			}
			else
				player.draw(g);
		}
	}
	
	//Chequea si se presionó la tecla Esc, de ser asi, s ebaja el volumen de la musica y entra al estado Pause.
	private void checkPause (GameContainer container, StateBasedGame game) throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
			music.setVolume(volumenpause);
			bossmusic.setVolume(volumenpause);
			paused = true;	
			game.enterState(3);
		}
	}
	
	//Continua con las animaciones del jugador y los monstruos.
	private void startAllAnimations () {	
		Zone zone = map.getZones()[(int)map.getCurrentZone().getX()][(int)map.getCurrentZone().getY()];
		player.startAnimations();
		for (int i = 0; i < zone.getMonsters().size(); i++)
			zone.getMonsters().get(i).startAnimations();
	}
	
	//Detiene las animaciones y sonidos del jugador y los montruos.
	private void stopAllAnimations () {
		Zone zone = map.getZones()[(int)map.getCurrentZone().getX()][(int)map.getCurrentZone().getY()];
		player.stopAnimations();
		for (int i = 0; i < zone.getMonsters().size(); i++)
			zone.getMonsters().get(i).stopAnimations();	
	}
	
	//Cuando vuelve del estado Pause.
	private void leavePause() {
		startAllAnimations();
		paused = false;
		music.setVolume(1);
		bossmusic.setVolume(1);
	}
	
	//Cuando vuelve del estado Transition.
	//Setea al jugador en la posicion inicial y aumenta el nivel.
	private void leaveTransition() throws SlickException {
		levelchange = false;
		player.getCoord().setPosition(POSDOWN.getX(), POSDOWN.getY(), player.getHTZAdjust(), player.getHTZWidth(), player.getHTZHeight());
		map.setCurrentLevel(map.getCurrentLevel()+1);
		music.loop();
	}
	
	//Cuando viene del estado Menu.
	//Inicia el jugador y crea las zonas.
	private void leaveMenu(GameContainer container) throws SlickException {
		map.init();
		player.init(POSDOWN.getX(), POSDOWN.getY());
		music.loop();
	}
	
	//Cuando muere el jugador se detiene la musica, todas las animaciones (excepto la animacion de la muerte del jugador)
	//e inicia el contador para entrar al estado GameOver.
	private void gameOver(StateBasedGame game) {
		gameovertimer.start();
		stopAllAnimations();
		music.stop();
		bossmusic.stop();
	}
	
	@Override
	public int getID() {
		return 1;
	}

}