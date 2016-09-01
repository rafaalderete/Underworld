package map;
import java.util.ArrayList;

import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

//Administra la posición.
public class Coord {
	
	private static final Point INVALIDPOS = new Point(-1, -1); 
	private float x, y;
	private Point tilepos; //Posición dentro del mapa de pathfinding.
	private Rectangle hitzone;
	private PathFindingMapPositionPixels pixelmap; //Utilizado para calcular la posición en el tilemap en base a la posición en pixeles.
	private boolean ismonster, dontcheckcollision;

	public Coord(float x, float y, boolean ismonster, boolean dontcheckcollision) {
		this.x = x;
		this.y = y;
		this.ismonster = ismonster;
		this.dontcheckcollision = dontcheckcollision; //Con los bosses no se controla la colisión con los obstáculos, colisión con el area ni la posición de tile.
		pixelmap = new PathFindingMapPositionPixels();
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	//Zona de colisión del jugador o monstruo.
	public void setHitZone (Point htzadjust, int htzwidth, int htzheight) {
		hitzone = new Rectangle((float)(x+htzadjust.getX()),(float)(y+htzadjust.getY()), htzwidth, htzheight);
	}

	public void setPosition (float x, float y, Point htzadjust, int htzwidth, int htzheight) {
		this.x = x;
		this.y = y;
		setHitZone(htzadjust, htzwidth, htzheight);
	}
		
	//En base de la velocidad y la cantidad de milisegundos transcurridos entre cada llamado
	//al metodo(delta), se actualiza la posición después de comprobar las colisiones con los obstaculos y los bordes del area jugable.
	public void updatePosition (Point speed, int delta, Rectangle area, ArrayList<Obstacle> obstacles, Point htzadjust, int htzwidth, int htzheight) {
		Point nexttile;
		boolean col = false;
		int obstaclecol = 0;
		float accelerationx = speed.getX() * ((float)delta/1000);
		float accelerationy = speed.getY() * ((float)delta/1000);
		if (!dontcheckcollision) {
			Rectangle nexthitzone = new Rectangle(hitzone.getX()+accelerationx, hitzone.getY()+accelerationy, htzwidth, htzheight);
			for (int i = 0; i < obstacles.size(); i++) { //Colisión con los obstaculos.
				if (obstacles.get(i).getRectangle().intersects(nexthitzone)) {
					col = true;
					obstaclecol = i;
					if ( ((hitzone.getMaxX() > obstacles.get(i).getRectangle().getMinX()) && (hitzone.getMaxX() < obstacles.get(i).getRectangle().getMaxX())) 
							|| ((hitzone.getMinX() > obstacles.get(i).getRectangle().getMinX()) && (hitzone.getMinX() < obstacles.get(i).getRectangle().getMaxX()))  ){
						accelerationy = 0;
					}
					if ( ((hitzone.getMaxY() >= obstacles.get(i).getRectangle().getMinY()) && (hitzone.getMaxY() <= obstacles.get(i).getRectangle().getMaxY())) 
							|| ((hitzone.getMinY() >= obstacles.get(i).getRectangle().getMinY()) && (hitzone.getMinY() <= obstacles.get(i).getRectangle().getMaxY()))  ){
						accelerationx = 0;
					}
				}
			}
			if (col && ismonster){ //Corrige el movimiento de los monstruos y permite que se "deslice" cuando colisiona con un obstaculo.
				if (speed.getX() == 0){
					if ( (hitzone.getCenterX() < obstacles.get(obstaclecol).getRectangle().getCenterX()) && (hitzone.getCenterY() < obstacles.get(obstaclecol).getRectangle().getCenterY())  )
						accelerationx = -speed.getY() * ((float)delta/1000);
					if ( (hitzone.getCenterX() > obstacles.get(obstaclecol).getRectangle().getCenterX()) && (hitzone.getCenterY() < obstacles.get(obstaclecol).getRectangle().getCenterY())  )
						accelerationx = speed.getY() * ((float)delta/1000);
					if ( (hitzone.getCenterX() < obstacles.get(obstaclecol).getRectangle().getCenterX()) && (hitzone.getCenterY() > obstacles.get(obstaclecol).getRectangle().getCenterY())  )
						accelerationx = speed.getY() * ((float)delta/1000);
					if ( (hitzone.getCenterX() > obstacles.get(obstaclecol).getRectangle().getCenterX()) && (hitzone.getCenterY() > obstacles.get(obstaclecol).getRectangle().getCenterY())  )
						accelerationx = -speed.getY() * ((float)delta/1000);
				}
				if (speed.getY() == 0){
					if ( (hitzone.getCenterX() < obstacles.get(obstaclecol).getRectangle().getCenterX()) && (hitzone.getCenterY() < obstacles.get(obstaclecol).getRectangle().getCenterY())  )
						accelerationy = -speed.getX() * ((float)delta/1000);
					if ( (hitzone.getCenterX() < obstacles.get(obstaclecol).getRectangle().getCenterX()) && (hitzone.getCenterY() > obstacles.get(obstaclecol).getRectangle().getCenterY())  )
						accelerationy = speed.getX() * ((float)delta/1000);
					if ( (hitzone.getCenterX() > obstacles.get(obstaclecol).getRectangle().getCenterX()) && (hitzone.getCenterY() < obstacles.get(obstaclecol).getRectangle().getCenterY())  )
						accelerationy = speed.getX() * ((float)delta/1000);
					if ( (hitzone.getCenterX() > obstacles.get(obstaclecol).getRectangle().getCenterX()) && (hitzone.getCenterY() > obstacles.get(obstaclecol).getRectangle().getCenterY())  )
						accelerationy = -speed.getX() * ((float)delta/1000);
					
				}
			}
			if (checkAreaColisionX(area, nexthitzone))	//Chequea que este dentro del area.
				accelerationx = 0;						//
			if (checkAreaColisionY(area, nexthitzone))  //
				accelerationy = 0;	
		}
		setPosition(x+accelerationx, y+accelerationy, htzadjust, htzwidth, htzheight);	
		if (!dontcheckcollision) {
			nexttile = pixelmap.tilePosition(hitzone.getCenterX(), hitzone.getCenterY()); //Actualiza el tilepos si es diferente.
			if ( ((nexttile.getX() != tilepos.getX()) || (nexttile.getY() != tilepos.getY())) && ((nexttile.getX() != INVALIDPOS.getX()) && (nexttile.getY() != INVALIDPOS.getY())) )										  //
				setTilepos(nexttile);
		}//
	}
	
	public boolean checkAreaColisionX (Rectangle area, Rectangle nexthitzone){
		boolean ban = false;
		if ((nexthitzone.getMinX() < area.getMinX()) || (nexthitzone.getMaxX() > area.getMaxX()))	//Chequea que este dentro del area.
			ban = true;
		return ban;
	}
	
	public boolean checkAreaColisionY (Rectangle area, Rectangle nexthitzone){
		boolean ban = false;
		if ((nexthitzone.getMinY() < area.getMinY()) || (nexthitzone.getMaxY() > area.getMaxY()))	//
			ban = true;
		return ban;
	}
	
	public Rectangle getHitZone () {
		return hitzone;
	}

	public Point getTilepos() {
		return tilepos;
	}

	public void setTilepos(Point tilepos) {
		this.tilepos = tilepos;
	}

	public PathFindingMapPositionPixels getPixelmap() {
		return pixelmap;
	}

}