package map;
import org.newdawn.slick.geom.Point;

//Arreglo de PositionPixel para calcular la posición dentro del tilemap.
public class PathFindingMapPositionPixels {
	
	private static final int DIMENSION = 80, MAPWIDTH = 13, MAPHEIGHT = 7;
	private PositionPixel[][] pixelmap;

	public PathFindingMapPositionPixels() {
		pixelmap = new PositionPixel[MAPWIDTH][MAPHEIGHT];
		int x = 0, y = DIMENSION;	
		for (int i = 0; i < pixelmap[0].length; i++) {
			for (int j = 0; j < pixelmap.length; j++) {
				pixelmap[j][i] = new PositionPixel(x, x+DIMENSION, y, y+DIMENSION);
				x += DIMENSION;				
			}
			x = 0;
			y += DIMENSION;
		}		
	}
	
	//Calcula la posición dentro del tilemap, si no la encuentra pone una posición invalida.
	public Point tilePosition(float x, float y) {
		for (int i = 1; i < pixelmap[0].length-1; i++) {
			for (int j = 1; j < pixelmap.length-1; j++) {
				if ( (x > pixelmap[j][i].getMinx() && x < pixelmap[j][i].getMaxx()) && (y > pixelmap[j][i].getMiny() && y < pixelmap[j][i].getMaxy()) ) {
					return new Point(j, i);
				}	
			}
		}
		return new Point(-1, -1);
	}
	
}