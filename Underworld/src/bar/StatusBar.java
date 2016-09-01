package bar;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;

import map.Zone;
import player.Player;

//Dibuja la vida del personaje, arma seleccionada, cantidad de balas restantes y el minimapa.
public class StatusBar {
	
	private static final String SW = "Selected Weapon", LF = "Life", MP = "Map", BYT = "Bayonet", GN = "Gun";
	private static final Point LIFEPN = new Point(130,0), SELECTWEAPON = new Point(450,0),  MAPPN = new Point(905,0), BAYONETPN = new Point(485,20)	//Coordenadas en donde se dibujarán 		
							 , GUNPN = new Point(500,20), BULLETPN = new Point(490,50), HEARTPN = new Point(30,20);									//los elementos.				
	private static final int HEARTWIDTH = 25, HEARTHEIGHT = 21, RECTANGLEWIDTH = 15, RECTANGLEHEIGHT = 5;
	private static final Point POSINIMAP = new Point (860,22);
	private Player player;
	private Image fillheart, heart, bullet;
	
	public StatusBar(Player player) throws SlickException {
		this.player = player;
		fillheart = new Image("res/sprites/misc/fillheart.png");
		heart = new Image("res/sprites/misc/heart.png");
		bullet = new Image("res/sprites/misc/statusbullet.png");
	}
	
	public void draw (Graphics g, Zone[][] zones) throws SlickException {
		drawlabels(g);
		drawhearts();
		drawWeapon(g);
		drawBullets(g);
		drawMap(g, zones);
	}
	
	//Dibuja los nombres.
	private void drawlabels(Graphics g) {
		g.setColor(Color.white);
		g.drawString(LF, LIFEPN.getX(), LIFEPN.getY());
		g.drawString(SW, SELECTWEAPON.getX(), SELECTWEAPON.getY());
		g.drawString(MP, MAPPN.getX(), MAPPN.getY());
	}
	
	//Dibuja la vida del personaje. Un for que primero dibuja la vida máxima y 
	//luego la vida actual.
	private void drawhearts() {
		boolean row2 = false;
		float x = HEARTPN.getX();
		float y = HEARTPN.getY();
		int hearts = player.getMaxLife();
		for (int j =1; j <= 2; j++) {
			if (j ==2)
				hearts = player.getLife();
			for (int i = 1; i <= hearts ; i++) {
				if (i > 10 && !row2) {
					row2 = true;
					x = HEARTPN.getX();
					y = y+HEARTHEIGHT;
				}
				if (j == 1)
					heart.draw(x, y);
				else
					fillheart.draw(x, y);
				x=x+HEARTWIDTH;
			}			
			x = HEARTPN.getX();
			y = HEARTPN.getY();
			row2 = false;
		}
	}
	
	//Dibuja el nombre del arma seleccionada.
	public void drawWeapon(Graphics g) {
		if (player.getBayonetSelected()) 
			g.drawString(BYT, BAYONETPN.getX(), BAYONETPN.getY());
		else
			g.drawString(GN, GUNPN.getX(), GUNPN.getY());	
	}
	
	//Dibuja la cantidad de balas restantes.
	public void drawBullets(Graphics g) {
		String bt = "x"+String.valueOf(player.getBullets());
		bullet.draw(BULLETPN.getX(), BULLETPN.getY());
		g.drawString(bt, BULLETPN.getX()+20, BULLETPN.getY());		
	}
	
	//Dibuja el minimapa.
	public void drawMap (Graphics g, Zone[][] zones) {
		float x = POSINIMAP.getX();
		float y = POSINIMAP.getY();	
		for (int i = 0; i < zones.length; i++){
			 for (int j = 0; j < zones[0].length; j++) {
				 g.setColor(Color.black);
				if (zones[j][i] != null){
					if (zones[j][i].isIn())
						g.setColor(Color.white);
					else
						if (zones[j][i].isVisited())
							g.setColor(Color.gray);
					g.fillRect(x, y, RECTANGLEWIDTH-1, RECTANGLEHEIGHT-1);
				}
				
				x = x+RECTANGLEWIDTH;
			 }
			 x = POSINIMAP.getX();
			 y = y+RECTANGLEHEIGHT;
		}
	}

}