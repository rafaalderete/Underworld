package main;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import states.Game;
import states.GameOver;
import states.Intro;
import states.Menu;
import states.Outro;
import states.Pause;
import states.Transition;

// Ventana y contenedor del juego.
public class Main extends StateBasedGame {
	private static final int WIDTH = 1040, HEIGHT = 640, STATES = 6;
	private  AppGameContainer container;

	public Main() throws SlickException {
		super("Underworld");
		container= new AppGameContainer(this);
		container.setDisplayMode(WIDTH, HEIGHT, false);;
		container.setShowFPS(false);
		container.setAlwaysRender(true);
		container.setUpdateOnlyWhenVisible(false);
		container.setMinimumLogicUpdateInterval(1);
		container.setMaximumLogicUpdateInterval(5);
		container.setIcon("res/sprites/icon/icon.png");	
		this.addState(new Menu());
		this.addState(new Game());
		this.addState(new GameOver());
		this.addState(new Pause());
		this.addState(new Intro());
		this.addState(new Outro());
		this.addState(new Transition());
		container.start();
	}
	
	//Inicialización de los estados que tendra el juego.
	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		//Inicia todos menos el estado Transition.
		for (int i = 0; i < STATES-1; i++)
			this.getState(i).init(container, this);
		this.enterState(0);	
	}

	public static void main(String[] args) {
		try {
			new Main();	
		} catch (SlickException slick) {
			slick.printStackTrace();
			System.exit(0);
		}
	}
	
}