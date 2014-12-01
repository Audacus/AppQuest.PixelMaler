package ch.fenceposts.appquest.pixelmaler.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Die DrawingView ist für die Darstellung und Verwaltung der Zeichenfläche zuständig.
 */
public class DrawingView extends View {

	private static final String	DEBUG_TAG		= "mydebug";
	private static final int	CANVAS_MARGIN	= 1;
	private int					gridSizeX;
	private int					gridSizeY;
	private int					stepSize;
	// private Paint[][] grid;
	private List<PointF>		pointsMotionEvent;
	private Map<Point, Paint>	pointsGrid;
	private Path				drawPath		= new Path();
	private Paint				paintDraw		= new Paint();
	private Paint				paintLine		= new Paint();
	private boolean				isErasing		= false;

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paintDraw.setAntiAlias(true);
		paintDraw.setStrokeWidth(20);
		paintDraw.setStyle(Paint.Style.STROKE);
		paintDraw.setStrokeJoin(Paint.Join.ROUND);
		paintDraw.setStrokeCap(Paint.Cap.ROUND);

		paintLine.setColor(0xFF666666);
		paintLine.setAntiAlias(true);
		paintLine.setStrokeWidth(1.0f);
		paintLine.setStyle(Paint.Style.STROKE);

		// this.gridSizeX = gridSizeX;
		// this.gridSizeY = gridSizeY;
		this.gridSizeX = 4;
		this.gridSizeY = 2;
		// this.grid = new Paint[gridSizeX][gridSizeY];
		this.pointsGrid = new HashMap<>();
		this.pointsMotionEvent = new ArrayList<>();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		
		Map<Point, Paint> pointsGridTemp = new HashMap<>(pointsGrid);
		for (Entry<Point, Paint> entry : pointsGridTemp.entrySet()) {
			if (entry.getKey().x >= gridSizeX || entry.getKey().y >= gridSizeY) {
				pointsGrid.remove(entry.getKey());
			}
		}

		final int maxX = canvas.getWidth() - CANVAS_MARGIN;
		final int maxY = canvas.getHeight() - CANVAS_MARGIN;

		int stepSizeX = (int) Math.floor((double) maxX / gridSizeX);
		int stepSizeY = (int) Math.floor((double) maxY / gridSizeY);
		stepSize = Math.min(stepSizeX, stepSizeY);
		

		// TODO Zeichne das Gitter
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		int startX;
		int stopX;
		int startY;
		int stopY;

		// draw lines vertical
		startY = 0;
		stopY = gridSizeY * stepSize;
		for (int i = 0; i <= gridSizeX; i++) {
			startX = (i * stepSize);
			stopX = startX;
			canvas.drawLine(startX, startY, stopX, stopY, paint);
		}

		// draw lines horizontal
		startX = 0;
		stopX = gridSizeX * stepSize;
		for (int i = 0; i <= gridSizeY; i++) {
			startY = (i * stepSize);
			stopY = startY;
			canvas.drawLine(startX, startY, stopX, stopY, paint);
		}

		// color rectangles
		for (Entry<Point, Paint> entry : pointsGrid.entrySet()) {
			int left = (entry.getKey().x) * stepSize;
			int top = (entry.getKey().y) * stepSize;
			int right = (entry.getKey().x + 1) * stepSize;
			int bottom = (entry.getKey().y + 1) * stepSize;
			Rect rect = new Rect(left, top, right, bottom);
			// canvas.drawRect(rect, entry.getValue());
			canvas.drawRect(rect, entry.getValue());
		}

		// for (int counterX = 0; counterX < gridSizeX; counterX++) {
		// for (int counterY = 0; counterY < gridSizeY; counterY++) {
		// if (grid[counterX][counterY] != null) {
		// int left = (counterX - 1) * stepSize;
		// int top = (counterY - 1) * stepSize;
		// int right = counterX * stepSize;
		// int bottom = counterY * stepSize;
		//
		// Rect rect = new Rect(left, top, right, bottom);
		// canvas.drawRect(rect, grid[counterX][counterY]);
		// }
		// }
		// }

		// reset motion points
//		 pointsMotionEvent.clear();

		// Zeichnet einen Pfad der dem Finger folgt
		canvas.drawPath(drawPath, paintDraw);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				drawPath.moveTo(touchX, touchY);

				// TODO wir müssen uns die berührten Punkte zwischenspeichern
				pointsMotionEvent.add(new PointF(touchX, touchY));

				break;
			case MotionEvent.ACTION_MOVE:
				drawPath.lineTo(touchX, touchY);

				// TODO wir müssen uns die berührten Punkte zwischenspeichern
				pointsMotionEvent.add(new PointF(touchX, touchY));

				break;
			case MotionEvent.ACTION_UP:

				// TODO Jetzt können wir die zwischengespeicherten Punkte auf das
				// Gitter umrechnen und zeichnen, bzw. löschen, falls wir isErasing
				// true ist (optional)
				for (PointF point : pointsMotionEvent) {
					if (point.x <= (gridSizeX * stepSize) && point.y <= (gridSizeY * stepSize)) {
						int gridX = (int) Math.floor(point.x / stepSize);
						int gridY = (int) Math.floor(point.y / stepSize);
						pointsGrid.put(new Point(gridX, gridY), new Paint(Color.GREEN));
						// grid[gridX][gridY] = new Paint(Color.GREEN);
					}
				}

				drawPath.reset();
				break;
			default:
				return false;
		}

		invalidate();
		return true;
	}

	public void setGridSizeX(int gridSizeX) {
		this.gridSizeX = gridSizeX;
		invalidate();
	}

	public void setGridSizeY(int gridSizeY) {
		this.gridSizeY = gridSizeY;
		invalidate();
	}

	public void setGridSize(int gridSizeX, int gridSizeY) {
		this.gridSizeX = gridSizeX;
		this.gridSizeY = gridSizeY;
		invalidate();
	}
	
	public void clearDrawing() {
		Log.d(DEBUG_TAG, "clearDrawing called");
		pointsMotionEvent.clear();
		pointsGrid.clear();
		invalidate();
	}

	public void startNew() {

		// TODO Gitter löschen
		// böh?

		invalidate();
	}

	public void setErase(boolean isErase) {
		isErasing = isErase;
		if (isErasing) {
			paintDraw.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		} else {
			paintDraw.setXfermode(null);
		}
	}

	public boolean isErasing() {
		return isErasing;
	}

	public void setColor(String color) {
		invalidate();
		paintDraw.setColor(Color.parseColor(color));
	}
}
