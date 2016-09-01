package states;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

//Menu que se dibujará al entrar al estado.
public class Pause extends BasicGameState {
	
	private final static Point PAUSEDPOS = new Point(320, 130), RESUMEPOS = new Point(400, 250), TOMAINMENUPOS = new Point(270, 320), SUREPOS = new Point(330, 250),
			YESPOS = new Point(380, 310), NOPOS = new Point(530, 310); //Posiciones donde dibujar cada imagen.
	private Image paused, resume1, resume2, tomainmenu1, tomainmenu2, sure, yes1, yes2, no1, no2;
	private Rectangle resume, tomainmenu, yes, no;
	private Input in;
	boolean tomenu = false;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		in = container.getInput();
		paused = new Image("res/sprites/menu/paused.png");
		resume1 = new Image("res/sprites/menu/resume1.png");
		resume2 = new Image("res/sprites/menu/resume2.png");
		tomainmenu1 = new Image("res/sprites/menu/tomainmenu1.png");
		tomainmenu2 = new Image("res/sprites/menu/tomainmenu2.png");
		sure = new Image("res/sprites/menu/sure.png");
		yes1 = new Image("res/sprites/menu/yes1.png");
		yes2 = new Image("res/sprites/menu/yes2.png");
		no1 = new Image("res/sprites/menu/no1.png");
		no2 = new Image("res/sprites/menu/no2.png");
		resume = new Rectangle(RESUMEPOS.getX(), RESUMEPOS.getY(), resume1.getWidth(), resume1.getHeight());
		tomainmenu = new Rectangle(TOMAINMENUPOS.getX(), TOMAINMENUPOS.getY(), tomainmenu1.getWidth(), tomainmenu1.getHeight());
		yes = new Rectangle(YESPOS.getX(), YESPOS.getY(), yes1.getWidth(), yes1.getHeight());
		no = new Rectangle(NOPOS.getX(), NOPOS.getY(), no1.getWidth(), no1.getHeight());
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		drawBackground(container, game, g);
		drawShadowRect(container, g);
		paused.draw(PAUSEDPOS.getX(), PAUSEDPOS.getY());
		if (!tomenu) {
			drawResume();
			drawToMainMenu();
		}
		else {
			sure.draw(SUREPOS.getX(), SUREPOS.getY());
			drawYes();
			drawNo();
		}		
	}
	
	//Dibuja el ultimo update del estado Game.
	private void drawBackground (GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		game.getState(1).render(container, game, g);
	}
	
	//Dibuja un cuadrado con transparencia, dejando en segundo plano el background.
	private void drawShadowRect(GameContainer container, Graphics g){
		Color trans = new Color(0f,0f,0f,0.5f);
	    g.setColor(trans);
	    g.fillRect(0,0, container.getWidth(), container.getHeight());
	}
	
	private void drawResume () {
		if (resume.contains(in.getMouseX(), in.getMouseY())){		
			resume2.draw(RESUMEPOS.getX(), RESUMEPOS.getY());													 
		}																		
		else{																	
			resume1.draw(RESUMEPOS.getX(), RESUMEPOS.getY());	
		}
	}
	
	private void drawToMainMenu () {
		if (tomainmenu.contains(in.getMouseX(), in.getMouseY())){		
			tomainmenu2.draw(TOMAINMENUPOS.getX(), TOMAINMENUPOS.getY());													 
		}																		
		else{																	
			tomainmenu1.draw(TOMAINMENUPOS.getX(), TOMAINMENUPOS.getY());		
		}
	}
	
	private void drawYes () {
		if (yes.contains(in.getMouseX(), in.getMouseY())){		
			yes2.draw(YESPOS.getX(), YESPOS.getY());													 
		}																		
		else{																	
			yes1.draw(YESPOS.getX(), YESPOS.getY());		
		}
	}
	
	private void drawNo () {
		if (no.contains(in.getMouseX(), in.getMouseY())){		
			no2.draw(NOPOS.getX(), NOPOS.getY());													 
		}																		
		else{																	
			no1.draw(NOPOS.getX(), NOPOS.getY());			
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		endPause(container, game);
		if (in.isMousePressed(0)){
			int xp= in.getMouseX();
			int yp= in.getMouseY();
			if (!tomenu) {
				resumePressed(game, xp, yp);
				toMainMenuPressed(game, xp, yp);
			}
			else {
				yesPressed(container, game, xp, yp);
				noPressed(game, xp, yp);
			}
		}
	}
	
	private void endPause (GameContainer container, StateBasedGame game) {
		if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)){
			game.enterState(1);	
		}
	}
	
	private void resumePressed (StateBasedGame game,int x, int y) {
		if (resume.contains(x, y))
			game.enterState(1);
	}
	
	private void toMainMenuPressed (StateBasedGame game,int x, int y) {
		if (tomainmenu.contains(x, y))
			tomenu = true;
	}
	
	private void yesPressed (GameContainer container, StateBasedGame game,int x, int y) throws SlickException {
		if (yes.contains(x, y)) {
			tomenu = false;
			game.getState(1).enter(container, game);
			game.enterState(0);
		}
	}
	
	private void noPressed (StateBasedGame game,int x, int y) {
		if (no.contains(x, y)){
			tomenu = false;
		}
	}
	
	@Override
	public int getID() {
		return 3;
	}

}
