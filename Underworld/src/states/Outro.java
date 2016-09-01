package states;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

//Imagenes al terminar el juego.
public class Outro extends BasicGameState {
	
	private static final int IMAGEWIDTH = 900, BACKHEIGHT = 458, DIALOGHEIGHT = 190, TOBECONTINUEDTIMERDELAY = 2000, TOMENUTIMERDELAY = 4000;
	private static final Point BACKPOS = new Point(70, 0), DIALOGPOS = new Point(70, 458), SKIPPOS = new Point(20, 0); //Posiciones de las imagenes.
	private SpriteSheet back, dialog;
	private Image skip, tobecontinued;
	private Sound wind, continuedsound;
	private boolean drawcontinued, continuedsoundplayed;
	private int indexdialog, indexback; //Indices para ir mostrando las imagenes de los spritesheet.
	private Input in;
	private Timer tobecontinuedtimer, tomenutimer;
	
	@Override
	public void init(GameContainer container, final StateBasedGame game)
			throws SlickException {
		in = container.getInput();
		back = new SpriteSheet("res/sprites/outro/back.jpg", IMAGEWIDTH, BACKHEIGHT);
		dialog = new SpriteSheet("res/sprites/outro/dialog.jpg", IMAGEWIDTH, DIALOGHEIGHT);
		tobecontinued = new Image("res/sprites/outro/continued.jpg");
		wind = new Sound("res/sound/outro/wind.ogg");
		continuedsound = new Sound("res/sound/outro/continued.ogg");
		skip = new Image(("res/sprites/intro/skip.png"));
		tobecontinuedtimer = new Timer(TOBECONTINUEDTIMERDELAY, new ActionListener() { //Timer para dibujar la imagen de tobecontinued.
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				wind.stop();
				indexback++;
				drawcontinued = true;
				if ( !continuedsound.playing() && !continuedsoundplayed ){
					continuedsound.play();
					continuedsoundplayed = true;
				}
				tomenutimer.start();
			}
		});
		
		tomenutimer = new Timer(TOMENUTIMERDELAY, new ActionListener() {//Timer para volver al menu luego de dibujar la imagen de tobecontinued.
			
			@Override
			public void actionPerformed(ActionEvent e) {
				toMenu(game);
			}
		});
	}
	
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		wind.loop();
		indexback = 0;
		indexdialog = 0;
		drawcontinued = false;
		continuedsoundplayed = false;
	}
	
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		wind.stop();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		skip.draw(SKIPPOS.getX(), SKIPPOS.getY());
		if (indexback < back.getHorizontalCount())
			back.getSprite(indexback, 0).draw(BACKPOS.getX(), BACKPOS.getY());
		if (indexdialog < dialog.getHorizontalCount())
			dialog.getSprite(indexdialog, 0).draw(DIALOGPOS.getX(), DIALOGPOS.getY());
		if (drawcontinued)
			tobecontinued.draw();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		if (in.isKeyPressed(Input.KEY_ESCAPE)) {
			toMenu(game);
		}
		else {
			//Al presionar enter o espacio pasa al siguiente cuadro de dialogo.
			if ( (in.isKeyPressed(Input.KEY_ENTER)) || (in.isKeyPressed(Input.KEY_SPACE)) ){
				indexdialog++;
				if (indexdialog == 2)
					indexback++;
				if (indexdialog > 2)
					tobecontinuedtimer.start();
			}		
		}
	}
	
	private void toMenu(StateBasedGame game) {
		tobecontinuedtimer.stop();
		tomenutimer.stop();
		game.enterState(0, new FadeOutTransition(Color.white), new FadeInTransition(Color.white));
	}

	@Override
	public int getID() {
		return 5;
	}

}
