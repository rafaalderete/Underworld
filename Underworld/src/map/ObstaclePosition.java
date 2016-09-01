package map;
import org.newdawn.slick.geom.Point;

//Contiene la posicioón en pixeles y en el arreglo del tilemap para el pathfinding.
public class ObstaclePosition {
	
	private Point pixelposition, arrayposition;

	public ObstaclePosition(Point pixelposition, Point arrayposition) {
		this.pixelposition = pixelposition;
		this.arrayposition = arrayposition;
	}
	
	public Point getPixelposition() {
		return pixelposition;
	}

	public Point getArrayposition() {
		return arrayposition;
	}

}