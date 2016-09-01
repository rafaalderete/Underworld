package map;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

import drop.Drop;
import drop.Life;
import drop.MaxLife;
import drop.MoreBullets;
import drop.MoreSpeed;
import drop.WeaponUpgrade;
import monsters.Demon;
import monsters.Dragon;
import monsters.Enemy;
import monsters.Ghostlvl1;
import monsters.Ghostlvl2;
import monsters.GiantSlime;
import monsters.SkullPillar;
import monsters.Slime;
import monsters.Zombielvl1_lvl3;
import monsters.Zombielvl2;

//Asigna los monstruos, puertas, drop y obstaculos entregados por la clase Map.
public class Zone {
	
	private static final int MAXX = 640, MINX = 160, MAXY = 180, MINY = 210, MAXLEVELS = 2, LADDERSPRITE = 2, AMOUNTPOSITIONS = 31, OBSTACLEDIMENSION = 80;
	private static final float DROPPROB = 0.7f, BULLET_BOSSDROPPROB = 0.5f, HEARTDROPPROB = 0.95f ;
	private static final int[] INVALIDPOSX = new int[] {240, 320, 400, 480, 560, 640, 720, 800}; // Posiciones que los monstruos
	private static final int[] INVALIDPOSY = new int[] {240, 320, 400}; 						 // no pueden ocupar inicialmente, 
																								 //	ya que no pueden ser controladas por el PathFindingMapPositionPixel.
	private static final Point LADDERPOS = new Point(80, 160);
	private boolean visited, cleared, in, start, end;
	private SpriteSheet floor, doors;
	private ArrayList<Obstacle> obstacles;
	private Rectangle ladder;
	private ArrayList<Enemy> monsters;
	private ArrayList<Integer> usedobstacles;
	private ArrayList<Door> listdoor; 
	private PathfindingMap tilemap;
	private Drop drop;
	private Sound opendoor;

	public Zone(int amountmonsters, int amountobstacles, int currentlevel, boolean doorup, boolean doorright, boolean doordown, boolean doorleft,
			boolean backdoorup, boolean backdoorright, boolean backdoordown, boolean backdoorleft, int doorboss, boolean start, boolean end, SpriteSheet floor, SpriteSheet doors,
			ObstaclePosition[] obstaclecoord, Sound opendoorsound) throws SlickException {			
		obstacles = new ArrayList<>();
		monsters = new ArrayList<Enemy>();
		usedobstacles = new ArrayList<Integer>();
		listdoor= new ArrayList<>();
		tilemap = new PathfindingMap();
		this.visited = false;
		this.start = start;
		this.end = end;
		this.opendoor = opendoorsound;
		if (start)
			this.in = true;
		if (backdoorup)
			doorup = true;
		if (backdoorright)
			doorright = true;
		if (backdoordown)
			doordown = true;
		if (backdoorleft)
			doorleft = true;
		this.floor = floor;
		this.doors = doors;
		//Dependiendo la zona pone monstruos o el boss.
		if (!end)
			putMonsters(currentlevel,amountmonsters);
		else
			putBoss(currentlevel);
		putObstacles(amountobstacles, obstaclecoord);
		if (!end) {
			if (doorup) {
				if (doorboss == 0)
					listdoor.add(new Door(new Point (2,0), new Point (2,1), 0));
				else
					listdoor.add(new Door(new Point (2,2), new Point (2,3), 0));
			}
			if (doorright)
				if (doorboss == 1)
					listdoor.add(new Door(new Point (3,0), new Point (3,1), 1));
				else
					listdoor.add(new Door(new Point (3,2), new Point (3,3), 1));
			if (doordown)
				if (doorboss == 2)
					listdoor.add(new Door(new Point (0,0), new Point (0,1), 2));
				else
					listdoor.add(new Door(new Point (0,2), new Point (0,3), 2));
			if (doorleft)
				if (doorboss == 3)
					listdoor.add(new Door(new Point (1,0), new Point (1,1), 3));
				else
					listdoor.add(new Door(new Point (1,2), new Point (1,3), 3));	
		}
		else {
			if (doorup)
				listdoor.add(new Door(new Point (2,0), new Point (2,1), 0));
			if (doorright)
				listdoor.add(new Door(new Point (3,0), new Point (3,1), 1));
			if (doordown)
				listdoor.add(new Door(new Point (0,0), new Point (0,1), 2));
			if (doorleft)
				listdoor.add(new Door(new Point (1,0), new Point (1,1), 3));				
		}
		if (start) {
			visited = true;
			in = true;
			cleared = true;
		}
		else {
			visited = false;
			in = false;
			cleared = false;
		}
		if (end)
			//Para cambiar de nivel.
			ladder = new Rectangle(LADDERPOS.getX(), LADDERPOS.getY(), floor.getSprite(LADDERSPRITE, 0).getWidth(), floor.getSprite(LADDERSPRITE, 0).getHeight());
		//Si es el boss final no setea drop.
		if (!start && !(currentlevel == MAXLEVELS && end))
			setDrop();
		if (!end)
			sortMonsters();
	}
	
	public void draw() {
		drawObstacles();
		drawDoors();
		drawLadder();
	}
	
	public void update(int delta, Rectangle area, Coord player) throws SlickException {
		for (int i = 0; i < monsters.size(); i++) {
			monsters.get(i).update(this.tilemap, delta, area, obstacles, player);
			}
		if(!end)
			sortMonsters();
	}
	
	//Dibuja todos los obstaculos contenidas en la lista de obstaculos.
	private void drawObstacles() {		
		for (int i = 0; i < obstacles.size(); i++) {
		floor.getSprite(obstacles.get(i).getSprite(), 0).draw(obstacles.get(i).getRectangle().getX(), obstacles.get(i).getRectangle().getY());
		}
	}
	
	//Dibuja las puertas contenidas en la lista de puertas.
	private void drawDoors() {
		for (int i = 0; i < listdoor.size(); i++) 
			if (!cleared) 
				doors.getSprite((int)listdoor.get(i).getDoorClose().getX(), (int)listdoor.get(i).getDoorClose().getY()).draw(listdoor.get(i).getPos().getX(), listdoor.get(i).getPos().getY());
			else
				doors.getSprite((int)listdoor.get(i).getDoorOpen().getX(), (int)listdoor.get(i).getDoorOpen().getY()).draw(listdoor.get(i).getPos().getX(), listdoor.get(i).getPos().getY());
	}
	
	//Dibuja la escalera.
	private void drawLadder() {
		if (cleared && end)
			floor.getSprite(LADDERSPRITE, 0).draw(ladder.getX(), ladder.getY());
	}
	
	//Dibuja el drop de la zona.
	public void drawDrop(Graphics g) {
		if ((drop != null) && cleared && !isStart() && !drop.isObtained()) {
			drop.draw(g);
		}
	}
		
	//Añade los monstruos controlando que no se utilicen ciertas posiciones.
	private void putMonsters(int currentlevel, int amountmonsters) throws SlickException {
		Random ran = new Random();
		int i = 1, selected = -1;
		Enemy nextadd = null;
		while (i <= amountmonsters) {
			//Los monstruos dependen del nivel.
			if (currentlevel == 0) {
				//En caso de que el mosntruo haya caido en una posición invalida,
				//vuelve a buscar una nueva posición sin hacer nuevamente el random.
				if (selected == -1) 
					selected = ran.nextInt(3);
				switch (selected) {
				case 0:
					nextadd = new Zombielvl1_lvl3(0, ran.nextInt(MAXX)+MINX, ran.nextInt(MAXY)+MINY);
					break;
				case 1:
					nextadd = new Slime(ran.nextInt(MAXX)+MINX, ran.nextInt(MAXY)+MINY);
					break;	
				case 2:
					nextadd = new Ghostlvl1(ran.nextInt(MAXX)+MINX, ran.nextInt(MAXY)+MINY);
					break;		
				}
			}
			else {
				if (currentlevel == 1) {
					if (selected == -1) 
						selected = ran.nextInt(4);
					switch (selected) {
					case 0:
						nextadd = new Zombielvl1_lvl3(0, ran.nextInt(MAXX)+MINX, ran.nextInt(MAXY)+MINY);
						break;
					case 1:
						nextadd = new Slime(ran.nextInt(MAXX)+MINX, ran.nextInt(MAXY)+MINY);
						break;
					case 2:
						nextadd = new Ghostlvl2(ran.nextInt(MAXX)+MINX, ran.nextInt(MAXY)+MINY);
						break;	
					case 3:
						nextadd = new Zombielvl2(ran.nextInt(MAXX)+MINX, ran.nextInt(MAXY)+MINY);
						break;
					}
				}
				else {
					if (selected == -1) 
						selected = ran.nextInt(3);
					switch (selected) {
					case 0:
						nextadd = new Zombielvl2(ran.nextInt(MAXX)+MINX, ran.nextInt(MAXY)+MINY);
						break;
					case 1:
						nextadd = new Zombielvl1_lvl3(1, ran.nextInt(MAXX)+MINX, ran.nextInt(MAXY)+MINY);
						break;
					case 2:
						nextadd = new SkullPillar(ran.nextInt(MAXX)+MINX, ran.nextInt(MAXY)+MINY);
						break;
					}
				}
			}
			if (!isMonsterInvalidPosition((int)nextadd.getCoord().getHitZone().getCenterX(), (int)nextadd.getCoord().getHitZone().getCenterY())) {
				monsters.add(nextadd);
				selected = -1;
				i++;
			}
		}
	}
	
	//Controla si el mosntruo esta en las posiciones prohibidas.
	private boolean isMonsterInvalidPosition(int x, int y) {
		boolean ban = false;
		for (int j = 0; j < INVALIDPOSX.length; j++)
			if (x == INVALIDPOSX[j])
				ban = true;
		for (int j = 0; j < INVALIDPOSY.length; j++)
			if (y == INVALIDPOSY[j])
				ban = true;
		return ban;
	}
	
	private void putBoss(int currentlevel) throws SlickException {
		//Los bosses dependen del nivel
		if (currentlevel == 0)
			monsters.add(new GiantSlime());	
		else 
			if (currentlevel == 1) 
				monsters.add(new Dragon());
			else 
				monsters.add(new Demon());
	}
	
	//Añade los obstaculos comprobando que la posición elegida no este ocupada por un monstruo y marca esa posición en el tilemap como bloqueado. 
	private void putObstacles(int amountobstacles, ObstaclePosition[] obstaclecoord) {
		Random ran = new Random();
		int j, i = 1;
		boolean used, ocuppied;
		Rectangle nextobstacle;
		while (i <= amountobstacles) {
			used = false;
			ocuppied = false;
			j = ran.nextInt(AMOUNTPOSITIONS); //Cantidad de posiciones que hay.			
			if (usedobstacles.size() != 0) {
				//Verifica si la posición elegida no está ocupada.
				for (int k = 0; k < usedobstacles.size(); k++){
					if (usedobstacles.get(k) == j)
						used = true;
				}
				if (!used) {
					nextobstacle = new Rectangle(obstaclecoord[j].getPixelposition().getX(), obstaclecoord[j].getPixelposition().getY(), OBSTACLEDIMENSION, OBSTACLEDIMENSION);
					//Verifica si la posicion elegida colisiona con algún mosntruo.
					for (int k = 0; k < monsters.size(); k++) {				
						if (monsters.get(k).getPos().getHitZone().intersects(nextobstacle)) 
							ocuppied = true;
					}
					//Si no estaba ocupada entonces agrega el obstaculo, lo agrega a la lista de ocupados y setea la posicion en el tilemap como ocupado.
					if (!ocuppied) {
						usedobstacles.add(j);
						tilemap.setMapObstacle((int)obstaclecoord[j].getArrayposition().getX(), (int)obstaclecoord[j].getArrayposition().getY());
						obstacles.add(new Obstacle(ran.nextInt(2), new Point(obstaclecoord[j].getPixelposition().getX(), obstaclecoord[j].getPixelposition().getY())));
						i++;
					}
				}
			}
			else {
				//Si la lista de zonas ocupadas estaba vacia entonces solo verifica la colision con los monstruos.
				nextobstacle = new Rectangle(obstaclecoord[j].getPixelposition().getX(), obstaclecoord[j].getPixelposition().getY(), 80, 80);
				for (int k = 0; k < monsters.size(); k++) {		
					if (monsters.get(k).getPos().getHitZone().intersects(nextobstacle)) 
						ocuppied = true;
				}
				if (!ocuppied) {
					usedobstacles.add(j);
					tilemap.setMapObstacle((int)obstaclecoord[j].getArrayposition().getX(), (int)obstaclecoord[j].getArrayposition().getY());
					obstacles.add(new Obstacle(ran.nextInt(2), new Point(obstaclecoord[j].getPixelposition().getX(), obstaclecoord[j].getPixelposition().getY())));
					i++;
				}
			}
		}	
	}
	
	//Setea el drop que tendrá la zona dependiendo de si es o no la zona del boss.
	private void setDrop() throws SlickException {
		Random ran = new Random();
		float j;
		if (!end) { //Drop de zonas noarmales.
			j = ran.nextFloat();
			if (j <= DROPPROB) {
			     j = ran.nextFloat();
				if (j <= BULLET_BOSSDROPPROB) 
					drop = new MoreBullets();
				else
					if ( (j > BULLET_BOSSDROPPROB) && (j <= HEARTDROPPROB))
						drop = new Life();
					else
						drop = new MoreSpeed();
			}
			else
				drop = null;
		}
		else { //Drop de zona de boss.
			j = ran.nextFloat();
			if (j <= BULLET_BOSSDROPPROB)
				drop = new MaxLife();
			else
				drop = new WeaponUpgrade();
		}			
	}
	
	//Si la vida de los monstruos es menor igual a 0, los setea muertos.
	public void checkMonstersLife() {
		for (int i = 0; i < monsters.size(); i++) {
			if ( (!monsters.get(i).isDead()) && (monsters.get(i).getLife() <= 0) )
				monsters.get(i).setDead(true);
		}
		checkCleared();		
	}
	
	//Elimina a los monstruos de la lista al cambiar de zona.
	public void removeMonsters() {
		monsters.clear();
	}
	
	//Verifica si todos los monstruos de la zona estan muertos, sin importar si termino la animación del humo.
	private void checkCleared() {
		boolean ban = true;
		for (int i = 0; i < monsters.size(); i++) {
			if (!monsters.get(i).isDead())
				ban = false;
		}
		if (ban){
			cleared = true;
			opendoor.play();
		}
	}	
	
	//Ordena el arreglo de monstruos mediante la posición Y de menor a mayor.
	private void sortMonsters() {
		Enemy compare;
		if (monsters.size() > 1){
			for (int i = 0; i < monsters.size()-i; i++) {
				for (int j = 0; j < monsters.size()-1; j++) {
					if (monsters.get(j).getPos().getHitZone().getCenterY() > monsters.get(j+1).getPos().getHitZone().getCenterY()) {
						compare = monsters.get(j);
						monsters.set(j, monsters.get(j+1));
						monsters.set(j+1, compare);
					}				
				}
			}
		}
	}
	
	public boolean existDoor (int doordirection) {
		boolean ban = false;
		for (int i = 0; i < listdoor.size(); i++) {
			if (listdoor.get(i).getDoorDirection() == doordirection)
				ban = true;
		}
		return ban;
	}
	
	public void setBossDoor(int doordirection) {
		if (doordirection == 0)
			listdoor.add(new Door(new Point (2,0), new Point (2,1), 0));
		else
			if (doordirection == 1)
				listdoor.add(new Door(new Point (3,0), new Point (3,1), 1));
			else
				if (doordirection == 2)
					listdoor.add(new Door(new Point (0,0), new Point (0,1), 2));
				else
					if (doordirection == 3)
						listdoor.add(new Door(new Point (1,0), new Point (1,1), 3));					
	}
	
	public void setDoor(int doordirection) {
		if (doordirection == 0)
			listdoor.add(new Door(new Point (2,2), new Point (2,3), 0));
		else
			if (doordirection == 1)
				listdoor.add(new Door(new Point (3,2), new Point (3,3), 1));
			else
				if (doordirection == 2)
					listdoor.add(new Door(new Point (0,2), new Point (0,3), 2));
				else
					if (doordirection == 3)
						listdoor.add(new Door(new Point (1,2), new Point (1,3), 3));						
	}
	
	public ArrayList<Enemy> getMonsters () {
		return monsters;
	}
	
	public ArrayList<Door> getDoors () {
		return listdoor;
	}
	
	public ArrayList<Obstacle> getObstacles () {
		return obstacles;
	}
	
	public Drop getDrop() {
		return drop;
	}
	
	public Rectangle getLadder() {
		return ladder;
	}
	
	public PathfindingMap getTileMap() {
		return tilemap;
	}
	
	public boolean isVisited() {
		return visited;
	}

	public boolean isIn() {
		return in;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public void setIn(boolean in) {
		this.in = in;
	}

	public boolean isCleared() {
		return cleared;
	}

	public boolean isStart() {
		return start;
	}

	public boolean isEnd() {
		return end;
	}

}