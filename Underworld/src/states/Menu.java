package states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

//Estado menu en el cual hay 2 botones: start y exit.
public class Menu extends BasicGameState {
	
	private static final Point STARTPOS = new Point(100, 300), EXITPOS = new Point(110, 370);
	private Image back, start1, start2, exit1, exit2;
	private Rectangle start,exit;
	private static Music music;
	private Input in;
	private Sound click;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		in = container.getInput();
		back = new Image("res/sprites/menu/ini.jpg");
		start1 = new Image("res/sprites/menu/start1.png");
		start2 = new Image("res/sprites/menu/start2.png");
		exit1 = new Image("res/sprites/menu/exit1.png");
		exit2 = new Image("res/sprites/menu/exit2.png");
		music = new Music("res/sound/music/menumusic.ogg");
		click = new Sound("res/sound/misc/click.ogg");
		start = new Rectangle(STARTPOS.getX(), STARTPOS.getY(), start1.getWidth(), start1.getHeight());
		exit = new Rectangle(EXITPOS.getX(), EXITPOS.getY(), exit1.getWidth(), exit1.getHeight());	
	}
	
	//Se ejecutan al entrar o al salir de un estado.
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		game.getState(6).init(container, game); //Inicializa el estado Transition cada vez que entra al menu para que las transisiones vuelvan a 0.
		music.play();
	}
	
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		music.stop();
	}
	//

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		back.draw();
		drawStart();
		drawExit();																	
	}
	
	//Captura las coordenadas del mouse en movimento y si esta dentro de un rectangle que contiene al start
	//o al exit, cambia de imagen.
	private void drawStart() {
		if (start.contains(in.getMouseX(), in.getMouseY())){		
			start2.draw(STARTPOS.getX(), STARTPOS.getY());													 
		}																		
		else{																	
			start1.draw(STARTPOS.getX(), STARTPOS.getY());													
		}			
	}
	
	private void drawExit() {
		if (exit.contains(in.getMouseX(), in.getMouseY())){		
			exit2.draw(EXITPOS.getX(), EXITPOS.getY());												
		}																		
		else{																	
			exit1.draw(EXITPOS.getX(), EXITPOS.getY());													
		}
	}
	//
	
	//Captura las coordenadas del click.
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		if (in.isMousePressed(0)){
			int xp= in.getMouseX();
			int yp= in.getMouseY();
			startPressed(game, xp, yp);
			exitPressed(container, xp, yp);
		}
	}
	
	//Verifica si las coordenadas capturadas estan dentro del rectangle de start o exit.
	private void startPressed (StateBasedGame game, int x, int y) {
		if (start.contains(x, y)){
			click.play();
			game.enterState(4, new FadeOutTransition(), new FadeInTransition());
		}
	}
	
	private void exitPressed(GameContainer container, int x, int y) {
		if (exit.contains(x, y)){
			container.exit();
		}
	}
	//
	
	//ID para identificar a cada estado.
	@Override
	public int getID() {
		return 0;
	}
	
}