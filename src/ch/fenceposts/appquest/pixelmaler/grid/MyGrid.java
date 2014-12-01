package ch.fenceposts.appquest.pixelmaler.grid;

import java.util.Map;

import android.graphics.Paint;
import android.graphics.Point;

public class MyGrid {

	private int					gridSizeX;
	private int					gridSizeY;
	private int					pixelSize;
	public Map<Point, Paint>	pixels;
	
	public MyGrid(int gridSizeX, int gridSizeY) {
		this.gridSizeX = gridSizeX;
		this.gridSizeY = gridSizeY;
		
	}
}
