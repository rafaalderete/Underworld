package states;
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

//Imagenes al iniciar el juego.
public class Intro extends BasicGameState {
	
	private static final int IMAGEWIDTH = 900, BACKHEIGHT = 458, DIALOGHEIGHT = 190;
	private static final Point BACKPOS = new Point(70, 0), DIALOGPOS = new Point(70, 458), SKIPPOS = new Point(20, 0); //Posiciones de las imagenes.
	private SpriteSheet back, dialog;
	private Image skip;
	private Sound earth, fell, day;
	private boolean dayplayed, earthplayed, fellplayed;
	private int indexdialog, indexback; //Indices para ir mostrando las imagenes de los spritesheet.
	private Input in;
	
	@Override
	public void init(GameContainer container, final StateBasedGame game)
			throws SlickException {
		in = container.getInput();
		back = new SpriteSheet("res/sprites/intro/back.jpg", IMAGEWIDTH, BACKHEIGHT);
		dialog = new SpriteSheet("res/sprites/intro/dialog.jpg", IMAGEWIDTH, DIALOGHEIGHT);
		earth = new Sound("res/sound/intro/earth.ogg");
		fell = new Sound("res/sound/intro/fell.ogg");
		day = new Sound("res/sound/intro/day.ogg");
		skip = new Image(("res/sprites/intro/skip.png"));
	}
	
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		indexback = 0;
		indexdialog = 0;
		dayplayed = false;
		earthplayed = false;
		fellplayed = false;
	}
	
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		earth.stop();
		day.stop();
		fell.stop();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		skip.draw(SKIPPOS.getX(), SKIPPOS.getY());
		if (indexback < back.getHorizontalCount())
			back.getSprite(indexback, 0).draw(BACKPOS.getX(), BACKPOS.getY());
		if (indexdialog < dialog.getHorizontalCount())
			dialog.getSprite(indexdialog, 0).draw(DIALOGPOS.getX(), DIALOGPOS.getY());
	}

	//Dependiendo del indexdialog sonará un sonido.
	//
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		if (indexdialog == 0){
			if( !day.playing() && !dayplayed){
				day.play();
				dayplayed = true;
			}
		}
		else {
			if (indexdialog == 1){
				if (!earth.playing() && !earthplayed){
					day.stop();
					earth.play();
					earthplayed = true;
				}
			}
			else {
				//Se le da un valor nulo a indexdialog para que suene fell.
				if (indexdialog == 3) {
					if (!fell.playing() && !fellplayed){
						earth.stop();
						fell.play();
						fellplayed = true;
					}
					if (!fell.playing()){
						indexdialog = 2;
						in.clearKeyPressedRecord(); //Para que no detecte las teclas presionadas mientras sonaba fell.
					}
				}
			}
		}
		if (in.isKeyPressed(Input.KEY_ESCAPE)) {
			toGame(game);
		}
		else {
			//Al presionar enter o espacio mientras no este sonando fell, pasa al siguiente cuadro de dialogo y fondo.
			if (!fell.playing()){
				if ( (in.isKeyPressed(Input.KEY_ENTER)) || (in.isKeyPressed(Input.KEY_SPACE)) ){
					indexback++;
					if (indexdialog == 1)
						indexdialog = 3;
					else
						if (indexdialog == 2)
							toGame(game);
						else
							indexdialog++;
				}
			}
		}
	}
	
	private void toGame(StateBasedGame game) {
		game.enterState(1, new FadeOutTransition(), new FadeInTransition());
	}

	@Override
	public int getID() {
		return 4;
	}

}
