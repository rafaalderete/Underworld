package states;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

//Muestra la imagen de gameover e incia el contador para volver al menu.
public class GameOver extends BasicGameState {
	
	private static final int BACKTOMENUDELAY = 5000;
	private Image background;
	private Timer backtomenu;

	@Override
	public void init(GameContainer container, final StateBasedGame game)
			throws SlickException {
		background = new Image("res/sprites/menu/gameover.jpg");
		backtomenu = new Timer(BACKTOMENUDELAY, new ActionListener() { //Tiempo que durará la imagen antes de volver al estado Menu.
			
			@Override
			public void actionPerformed(ActionEvent e) {
				backtomenu.stop();
				game.enterState(0, new FadeOutTransition(), new FadeInTransition());
				
			}
		});
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		background.draw();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		if (!backtomenu.isRunning())
			backtomenu.start();
	}

	@Override
	public int getID() {
		return 2;
	}

}
