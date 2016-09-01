package map;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

public class Map {
	
	//Posiciones para los obstaculos.
	private static final ObstaclePosition[] OBSTACLESCOORD = new ObstaclePosition[] {
		new ObstaclePosition(new Point (80,160), new Point(1, 1)), new ObstaclePosition(new Point (160,160), new Point(2, 1)),
		new ObstaclePosition(new Point (240,160), new Point(3, 1)), new ObstaclePosition(new Point (320,160), new Point(4, 1)),
		new ObstaclePosition(new Point (400,160), new Point(5, 1)), new ObstaclePosition(new Point (560,160), new Point(7, 1)),
		new ObstaclePosition(new Point (640,160), new Point(8, 1)), new ObstaclePosition(new Point (720,160), new Point(9, 1)),
		new ObstaclePosition(new Point (800,160), new Point(10, 1)), new ObstaclePosition(new Point (880,160), new Point(11, 1)),
		new ObstaclePosition(new Point (80,240), new Point(1, 2)), new ObstaclePosition(new Point (160,240), new Point(2, 2)),
		new ObstaclePosition(new Point (240,240), new Point(3, 2)), new ObstaclePosition(new Point (320,240), new Point(4, 2)),
		new ObstaclePosition(new Point (400,240), new Point(5, 2)), new ObstaclePosition(new Point (560,240), new Point(7, 2)),
		new ObstaclePosition(new Point (640,240), new Point(8, 2)), new ObstaclePosition(new Point (720,240), new Point(9, 2)),
		new ObstaclePosition(new Point (800,240), new Point(10, 2)), new ObstaclePosition(new Point (880,240), new Point(11, 2)),
		new ObstaclePosition(new Point (80,400), new Point(1, 4)), new ObstaclePosition(new Point (160,400), new Point(2, 4)),
		new ObstaclePosition(new Point (240,400), new Point(3, 4)), new ObstaclePosition(new Point (320,400), new Point(4, 4)),
		new ObstaclePosition(new Point (400,400), new Point(5, 4)), new ObstaclePosition(new Point (560,400), new Point(7, 4)),
		new ObstaclePosition(new Point (640,400), new Point(8, 4)), new ObstaclePosition(new Point (720,400), new Point(9, 4)),
		new ObstaclePosition(new Point (800,400), new Point(10, 4)), new ObstaclePosition(new Point (880,400), new Point(11, 4)),
		new ObstaclePosition(new Point (80,480), new Point(1, 5)), new ObstaclePosition(new Point (160,480), new Point(2, 5)),
		new ObstaclePosition(new Point (240,480), new Point(3, 5)), new ObstaclePosition(new Point (320,480), new Point(4, 5)),
		new ObstaclePosition(new Point (400,480), new Point(5, 5)), new ObstaclePosition(new Point (560,480), new Point(7, 5)),
		new ObstaclePosition(new Point (640,480), new Point(8, 5)), new ObstaclePosition(new Point (720,480), new Point(9, 5)),
		new ObstaclePosition(new Point (800,480), new Point(10, 5)), new ObstaclePosition(new Point (880,480), new Point(11, 5)),
	};

	private static final int DIMENSION = 80, DOORDIMENSION = 130, MAXZONES = 10;
	private Zone [][] zones;
	private TiledMap lvl1 ,lvl2, lvl3;
	private SpriteSheet floor, doors;
	private ArrayList<TiledMap> maps;
	private ArrayList<Point> usedzones;
	private int currentlevel;
	private Point currentzone;
	public Sound opendoor, closedoor;

	public Map() throws SlickException {
		lvl1 = new TiledMap("res/sprites/maps/lvl1/lvl1.tmx");
		lvl2 = new TiledMap("res/sprites/maps/lvl2/lvl2.tmx");
		lvl3 = new TiledMap("res/sprites/maps/lvl3/lvl3.tmx");
		floor = new SpriteSheet("res/sprites/maps/floor.png", DIMENSION, DIMENSION);
		doors = new SpriteSheet("res/sprites/maps/doors.png", DOORDIMENSION, DOORDIMENSION);
		opendoor = new Sound("res/sound/misc/opendoor.ogg");
		closedoor = new Sound("res/sound/misc/closedoor.ogg");
		maps = new ArrayList<>();
		maps.add(lvl1);
		maps.add(lvl2);
		maps.add(lvl3);
	}
	
	public void init() throws SlickException {
		currentlevel = 0;
		zones = new Zone[MAXZONES][MAXZONES];
		for (int i = 0; i < MAXZONES; i++)
			for (int j = 0; j < MAXZONES; j++)
				zones[i][j] = null;
		generateZones();
		generateAlternativesZones();
	}
	
	//Genera, de forma aleatoria, el recorrido principal de cada nivel en donde cada zona tendrá un numero de obstaculos, monstruos y puertas.
	//Hay 3 casos:
	//Caso 1: la zona actual no es nula.
	//Caso 2: la zona actual es nula y posée las 4 direcciones invalidas.
	//Caso 3: la zona actual es nula y no posée las 4 direcciones invalidas(en este caso se guarda la zona en usedzones, para la generacion de zonas alternativas).
	private void generateZones() throws SlickException {
		int x, y, nextzone, i, incrementx, incrementy, amountmonsters, amountobstacles, doorboss;
		int distance; //Zonas de inicio a final.
		boolean start, end, ban;
		boolean doorup, doorleft, doorright, doordown, backdoorup, backdoorleft, backdoorright, backdoordown;
		backdoorup = false;
		backdoorright = false;
		backdoordown = false;
		backdoorleft = false;
		incrementx = 0;
		incrementy = 0;
		Random ran = new Random();
		usedzones = new ArrayList<Point>();	
		//La cantidad de zonas dependen del nivel.
		if (currentlevel == 0) 
			distance = 6;
		else
			if (currentlevel == 1)
				distance = 8;
			else
				distance = 10;
		x = ran.nextInt(MAXZONES);//Zona inicial.
		y = ran.nextInt(MAXZONES);//
		i = 0;
		while (i < distance) {
			doorup = false;
			doorright = false;
			doordown = false;
			doorleft = false;
			start = false;
			end = false;
			incrementx = 0;
			incrementy = 0;
			doorboss = -1;
			//Caso 1. La zona actual no es nula y vuelve a elegir otra zona. No crea zona.
			if (zones[x][y] != null) {
				ban = false;
				//Mientras no sea una zona fuera de la matriz zones...
				while (!ban) {
					incrementx = 0;
					incrementy = 0;
					doorup = false;
					doorright = false;
					doordown = false;
					doorleft = false;
					//Elige la siguiente zona, indicando que puerta deberá haber cuando se cree la zona.
					nextzone = ran.nextInt(4);
					if (nextzone == 0) {
						doorup = true;
						incrementy = -1;
					}
					else {
						if (nextzone == 1) {
							doorright = true;
							incrementx = 1;
						}
						else {
							if (nextzone == 2) {
								doordown = true;
								incrementy = 1;
							}
							else {
								doorleft = true;
								incrementx = -1;
							}
						}
					}
					if ((x+incrementx >= 0) && (x+incrementx < 10) && (y+incrementy >= 0) && (y+incrementy < 10)) 
						ban = true;
				}
				backdoorup = false;
				backdoorright = false;
				backdoordown = false;
				backdoorleft = false;
				//Si la siguiente zona elegida no es nula, entonces en la zona actual setea la puerta para poder entrar a la siguiente zona,
				//dependiendo si es una zona normal o de boss e indicando que puerta de regreso deberá haber cuando se cree la siguiente zona.
				if (zones[x+incrementx][y+incrementy] == null) {
					if (doorup) {
						if (i == distance-2)
							zones[x][y].setBossDoor(0);
						else
							zones[x][y].setDoor(0);
						backdoordown = true;
					}
					if (doorright) {
						if (i == distance-2)
							zones[x][y].setBossDoor(1);
						else
							zones[x][y].setDoor(1);
						backdoorleft = true;
					}
					if (doordown) {
						if (i == distance-2)
							zones[x][y].setBossDoor(2);
						else
							zones[x][y].setDoor(2);
						backdoorup = true;
					}
					if (doorleft) {
						if (i == distance-2)
							zones[x][y].setBossDoor(3);
						else
							zones[x][y].setDoor(0);
						backdoorright = true;
					}
				}
				x = x+incrementx;
				y = y+incrementy;
			}
			else {
				if (i == 0) {
					start = true;
					amountobstacles = 0;
				}
				else
					if (i == distance-1){
						end = true;
						amountobstacles = 0;
					}
					else
						amountobstacles = ran.nextInt(12)+1;
				
				if (i != 0)
					amountmonsters = ran.nextInt(4)+2;
				else
					amountmonsters = 0;
				//Caso 2. La zona actual posee las 4 direcciones nulas y no es el final. Crea zona y elige la siguiente
				//(la siguiente zona elegida siempre será una zona no nula).
				if (allWaysNull(x, y) && !end) {
					ban = false;
					//Mientras no sea una zona fuera de la matriz zones...
					while (!ban) {
						incrementx = 0;
						incrementy = 0;
						doorup = false;
						doorright = false;
						doordown = false;
						doorleft = false;
						//Elige la siguiente zona, indicando que puerta deberá haber cuando se cree la zona.
						nextzone = ran.nextInt(4);
						if (nextzone == 0) {
							doorup = true;
							incrementy = -1;
						}
						else {
							if (nextzone == 1) {
								doorright = true;
								incrementx = 1;
							}
							else {
								if (nextzone == 2) {
									doordown = true;
									incrementy = 1;
								}
								else {
									doorleft = true;
									incrementx = -1;
								}
							}
						}
						if ((x+incrementx >= 0) && (x+incrementx < 10) && (y+incrementy >= 0) && (y+incrementy < 10)) 
							ban = true;
					}
					//Crea la zona
					zones [x][y] = new Zone(amountmonsters, amountobstacles, currentlevel, doorup, doorright, doordown, doorleft, backdoorup, backdoorright, backdoordown, backdoorleft,
							doorboss, start, end, floor, doors, OBSTACLESCOORD, opendoor);
					//Setea en la siguiente zona la puerta de regreso hacia la zona actual (dicha siguiente zona será una zona no nula).
					if (doorup)
						if (!(zones[x+incrementx][y+incrementy].existDoor(2)))
							zones[x+incrementx][y+incrementy].setDoor(2);
					if (doorright)
						if (!(zones[x+incrementx][y+incrementy].existDoor(3)))
							zones[x+incrementx][y+incrementy].setDoor(3);
					if (doordown)
						if (!(zones[x+incrementx][y+incrementy].existDoor(0)))
							zones[x+incrementx][y+incrementy].setDoor(0);
					if (doorleft)
						if (!(zones[x+incrementx][y+incrementy].existDoor(1)))
							zones[x+incrementx][y+incrementy].setDoor(1);
					x = x+incrementx;
					y = y+incrementy;
					i++;
				}
				else { //Caso 3. La zona actual es nula y no posee las 4 direcciones nulas. Crea zona.
					if (!end) {
						ban = false;
						//Mientras no sea una zona fuera de la matriz zones y no sea nula...
						while (!ban) {
							incrementx = 0;
							incrementy = 0;
							doorup = false;
							doorright = false;
							doordown = false;
							doorleft = false;
							//Elige la siguiente zona, indicando que puerta deberá haber cuando se cree la zona.
							nextzone = ran.nextInt(4);
							if (nextzone == 0) {
								doorup = true;
								incrementy = -1;
							}
							else {
								if (nextzone == 1) {
									doorright = true;
									incrementx = 1;
								}
								else {
									if (nextzone == 2) {
										doordown = true;
										incrementy = 1;
									}
									else {
										doorleft = true;
										incrementx = -1;
									}
								}
							}
							if ((x+incrementx >= 0) && (x+incrementx < 10) && (y+incrementy >= 0) && (y+incrementy < 10) && (zones[x+incrementx][y+incrementy] == null)) 
								ban = true;
						}
					}
					//Indica la dirección de la puerta del boss, si se encuentra en la zona anterior al boss.
					if (i == distance-2) {
						if (incrementx == 1)
							doorboss = 1;
						if (incrementx == -1)
							doorboss = 3;
						if (incrementy == 1)
							doorboss =2;
						if (incrementy == -1)
							doorboss = 0;
					}
					//Crea la zona.
					zones [x][y] = new Zone(amountmonsters, amountobstacles, currentlevel, doorup, doorright, doordown, doorleft, backdoorup, backdoorright, backdoordown, backdoorleft,
											doorboss, start, end, floor, doors, OBSTACLESCOORD, opendoor);
					//Lo agrega a la lista de zonas usadas para la generacion de zonas alternativas.
					if (!end)
						usedzones.add(new Point(x,y));
					if (i == 0)
						currentzone =new Point(x, y);
					backdoorup = false;
					backdoorright = false;
					backdoordown = false;
					backdoorleft = false;
					//Puertas de regreso que tendrá la siguiente zona a crear.
					if (doorup)
						backdoordown = true;
					if (doorright)
						backdoorleft = true;
					if (doordown)
						backdoorup = true;
					if (doorleft)
						backdoorright = true;
					x = x+incrementx;
					y = y+incrementy;
					i++;						
				}
			}	
		}
	}
	
	//Genera zonas aleatorias a partir de zonas que no tengas las 4 direcciones nulas.
	private void generateAlternativesZones() throws SlickException {
		int distance, doorboss, amountobstacles, amountmonsters, x, y, incrementx = 0, incrementy = 0, nextzone, j, i = 0;
		boolean doorup, doorleft, doorright, doordown, backdoorup, backdoorleft, backdoorright, backdoordown;
		boolean start, end;
		boolean ban;
		Random ran = new Random();
		removeAllWaysNullZones();
		//La cantidad de zonas aleatorias que se agregarán dependen del nivel.
		if (currentlevel == 0) 
			distance = 3;
		else
			if (currentlevel == 1)
				distance = 5;
			else
				distance = 7;	
		while ( (i < distance) && (usedzones.size() != 0)) {
			start = false;
			end = false;
			doorboss = -1;
			doorup = false;
			doorright = false;
			doordown = false;
			doorleft = false;
			backdoorup = false;
			backdoorright = false;
			backdoordown = false;
			backdoorleft = false;
			j = ran.nextInt(usedzones.size()); //Elige la zona de la cual parte para crear la nueva zona.
			x = (int) usedzones.get(j).getX();
			y = (int) usedzones.get(j).getY();
			amountobstacles = ran.nextInt(12)+1;
			amountmonsters = ran.nextInt(4)+2;
			ban = false;
			//Mientras la posición elegida no se encuentre en el rango de la matriz zones o la zona elegida no sea nula...
			while (!ban) {
				incrementx = 0;
				incrementy = 0;
				nextzone = ran.nextInt(4); //Mediante un random se elige en que dirección se creará la zona.
				if (nextzone == 0)
					incrementy = -1;
				else 
					if (nextzone == 1) 
						incrementx = 1;
					else 
						if (nextzone == 2) 
							incrementy = 1;
						else 
							incrementx = -1;	
				if ((x+incrementx >= 0) && (x+incrementx < 10) && (y+incrementy >= 0) && (y+incrementy < 10)
				&& zones[x+incrementx][y+incrementy] == null) { 
					ban = true;
					//Dependiendo la dirección elegida, se setea la puerta de ida en la zona que parte
					//y se pone la variable en true que será la puerta de vuelta.
					if (nextzone == 0) {
						zones[x][y].setDoor(0);
						doordown = true;
					}
					else
						if (nextzone == 1) {
							zones[x][y].setDoor(1);
							doorleft = true;
						}
						else
							if (nextzone == 2) {
								zones[x][y].setDoor(2);
								doorup = true;
							}
							else
								if (nextzone == 3) {
									zones[x][y].setDoor(3);
									doorright = true;
								}
				}	
			}
			zones [x+incrementx][y+incrementy] = new Zone(amountmonsters, amountobstacles, currentlevel, doorup, doorright, doordown, doorleft, backdoorup, backdoorright, backdoordown, backdoorleft,
					doorboss, start, end, floor, doors, OBSTACLESCOORD, opendoor);
			usedzones.add(new Point(x+incrementx,y+incrementy));
			removeAllWaysNullZones();
			i++;			
		}	
	}
	
	//Verifica si las zonas en las 4 direcciones de la zona pasada por parametro son nulas o estan ocupadas.
	private boolean allWaysNull(int x, int y) {
		boolean ban = true;
		if ((x+1 >= 0) && (x+1 < 10) && zones[x+1][y] == null ) 
			ban = false;
		if ((y+1 >= 0) && (y+1 < 10) && zones[x][y+1] == null ) 
			ban = false;
		if ((x-1 >= 0) && (x-1 < 10) && zones[x-1][y] == null ) 
			ban = false;
		if ((y-1 >= 0) && (y-1 < 10) && zones[x][y-1] == null ) 
			ban = false;	
		return ban;
	}
	
	//Elimina las zonas que posean las 4 dirreciones invalidas.
	private void removeAllWaysNullZones() {
		int k = 0;
		while (k < usedzones.size()){
			if (allWaysNull((int) usedzones.get(k).getX(), (int) usedzones.get(k).getY()))
				usedzones.remove(k);
			else
				k++;
		}
	}
	
	public void draw() {
		drawmap();
		zones[(int) currentzone.getX()][(int) currentzone.getY()].draw();
	}
	
	public void update(int delta, Rectangle area, Coord player) throws SlickException {
		zones[(int) currentzone.getX()][(int) currentzone.getY()].update(delta, area, player);
	}
	
	//Dibuja el mapa dependiendo del nivel actual.
	private void drawmap() {	
		maps.get(currentlevel).render(0, 80);
	}
	
	public int getCurrentLevel() {
		return currentlevel;
	}
	
	public void setCurrentLevel(int currentlevel) throws SlickException {
		this.currentlevel = currentlevel;
		zones = new Zone[MAXZONES][MAXZONES];
		generateZones();
		generateAlternativesZones();
	}
	
	public Point getCurrentZone() {
		return currentzone;
	}
	
	//Cambia la zona y setea Visited e In para el minimapa.
	public void changeZone(Point currentzone) {
		zones[(int)currentzone.getX()][(int)currentzone.getY()].setVisited(true);
		zones[(int)currentzone.getX()][(int)currentzone.getY()].setIn(true);
		zones[(int)this.currentzone.getX()][(int)this.currentzone.getY()].setIn(false);
		this.currentzone = currentzone;
		if (!zones[(int)currentzone.getX()][(int)currentzone.getY()].isCleared())
			closedoor.play();
	}
	
	public Zone[][] getZones () {
		return zones;
	}

}