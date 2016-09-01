package map;

import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

//Puertas con su dirección y posición.
public class Door {
	
	private static final Point POSDOORUP = new Point(455, 60), POSDOORRIGHT = new Point(932, 297), POSDOORDOWN = new Point(455, 532), POSDOORLEFT = new Point(-23, 297), //
						 RECTDOORUP = new Point(503, 134), RECTDOORRIGHT = new Point(955, 347), RECTDOORDOWN = new Point(503, 557), RECTDOORLEFT = new Point(55, 347);   // Ubicaciones de las puertas.					
	private static final int RECTDIMENSION = 30, //Dimensión de la colision con las puertas.
						 DIRU = 0, DIRR = 1, DIRD = 2;
	private int doordirection;
	private Point dooropen, doorclose, pos;
	private Rectangle colisionzone; 

	
	public Door(Point doorclosed, Point dooropen, int doordirection) {
		this.doordirection = doordirection;
		this.doorclose = doorclosed;
		this.dooropen = dooropen;
		if (doordirection == DIRU) {
			this.pos = new Point(POSDOORUP.getX(), POSDOORUP.getY());
			this.colisionzone = new Rectangle(RECTDOORUP.getX(), RECTDOORUP.getY(), RECTDIMENSION, RECTDIMENSION);
		}
		else {
			if (doordirection == DIRR) {
				this.pos = new Point(POSDOORRIGHT.getX(), POSDOORRIGHT.getY());
				this.colisionzone = new Rectangle(RECTDOORRIGHT.getX(), RECTDOORRIGHT.getY(), RECTDIMENSION, RECTDIMENSION);
			}
			else {
				if (doordirection == DIRD) {
					this.pos = new Point(POSDOORDOWN.getX(), POSDOORDOWN.getY());
					this.colisionzone = new Rectangle(RECTDOORDOWN.getX(), RECTDOORDOWN.getY(), RECTDIMENSION, RECTDIMENSION);
				}
				else {
					this.pos = new Point(POSDOORLEFT.getX(), POSDOORLEFT.getY());
					this.colisionzone = new Rectangle(RECTDOORLEFT.getX(), RECTDOORLEFT.getY(), RECTDIMENSION, RECTDIMENSION);
				}
			}
		}
	}
	
	public Point getPos() {
		return pos;
	}
	
	public int getDoorDirection() {
		return doordirection;
	}

	public Point getDoorOpen() {
		return dooropen;
	}

	public Point getDoorClose() {
		return doorclose;
	}

	public Rectangle getColisionZone() {
		return colisionzone;
	}	
	
}