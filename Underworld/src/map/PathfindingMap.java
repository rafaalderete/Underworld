package map;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

//Tilemap para el pathfinding.
public class PathfindingMap implements TileBasedMap {
	
	private final static int WIDTH = 13, HEIGHT = 7;
	private final static int[] BORDER = new int[] {1,1,1,1,1,1,1,1,1,1,1,1,1};
	private final static int[] INTERIOR = new int[] {1,0,0,0,0,0,0,0,0,0,0,0,1};
	private int[][] tilemap;

	public PathfindingMap() {
		loadMap();
	}

	//Posicion no accesible.
	@Override
	public boolean blocked(PathFindingContext context, int tx, int ty) {
		if (tilemap[tx][ty] == 1)
			return true;
		else
			return false;	
	}

	@Override
	public float getCost(PathFindingContext context, int tx, int ty) {
		return 1;
	}

	@Override
	public int getHeightInTiles() {
		return HEIGHT;
	}

	@Override
	public int getWidthInTiles() {		
		return WIDTH;
	}

	@Override
	public void pathFinderVisited(int x, int y) {		
	}
	
	//Carga el tilemap inicial el cual luego se modificará mediante el metodo setMapObstacle para cada zona.
	private void loadMap() {
		int i;
		tilemap = new int[WIDTH][HEIGHT];
		 for (int y = 0; y < HEIGHT; y++) {
			 i = 0;
	         for (int x = 0; x < WIDTH; x++) {
	        	 if ( (y == 0) || (y == HEIGHT-1) )
	        		 tilemap[x][y] = BORDER[i];
	        	 else
	        		 tilemap[x][y] = INTERIOR[i];
	        	 i++;
	         }
		 }
	}
	
	public void setMapObstacle (int x, int y) {
		tilemap[x][y] = 1;
	}

}