package map;
//Posiciones maximas y minimas(en pixeles) para calcular la posición dentro del tilemap.
public class PositionPixel {
	
	private int minx, maxx ,miny, maxy;

	public PositionPixel(int minx, int maxx, int miny, int maxy) {
		this.minx = minx;
		this.maxx = maxx;
		this.miny = miny;
		this.maxy = maxy;
	}

	public int getMinx() {
		return minx;
	}

	public int getMaxx() {
		return maxx;
	}

	public int getMiny() {
		return miny;
	}

	public int getMaxy() {
		return maxy;
	}

}