package states;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

//Estado entre cambio de nivel en donde se controla y debe volver al estado Game o ir al estado Outro.
public class Transition extends BasicGameState {
	
	private final static int MAXTRANSITIONS = 2;
	private Sound ladder;
	private int transitions;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		ladder = new Sound("res/sound/misc/ladder.ogg");
		transitions = 0;
	}
	
	public void enter(GameContainer container, StateBasedGame game) {
		transitions++;
		ladder.play();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		if (transitions <= MAXTRANSITIONS) {
			if (!ladder.playing())
				game.enterState(1, new FadeOutTransition(), new FadeInTransition());
		}
		else {
			if (!ladder.playing()){
				game.enterState(5, new FadeOutTransition(Color.white), new FadeInTransition(Color.white));
			}
		}	
	}

	@Override
	public int getID() {
		return 6;
	}

}
