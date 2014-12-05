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
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import ch.fenceposts.appquest.pixelmaler.R;

/**
 * Die DrawingView ist für die Darstellung und Verwaltung der Zeichenfläche zuständig.
 */
public class DrawingView extends View {

	private static final String	DEBUG_TAG		= "mydebug";
	private static final int	CANVAS_MARGIN	= 1;
	private boolean				isErasing		= false;
	private int					gridSizeX;
	private int					gridSizeY;
	private int					stepSize;
	private List<PointF>		pointsMotionEvent;
	private Map<Point, Paint>	pointsGrid;

	private Path				drawPath		= new Path();
	private Paint				paintDraw		= new Paint();

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// paintDraw.setAntiAlias(true);
		paintDraw.setStrokeWidth(20);
		paintDraw.setStyle(Paint.Style.STROKE);
		paintDraw.setStrokeJoin(Paint.Join.ROUND);
		paintDraw.setStrokeCap(Paint.Cap.ROUND);

		gridSizeX = getResources().getInteger(R.integer.grid_size_default);
		gridSizeY = getResources().getInteger(R.integer.grid_size_default);

		pointsGrid = new HashMap<>();
		pointsMotionEvent = new ArrayList<>();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		Map<Point, Paint> pointsGridTemp = new HashMap<>(pointsGrid);
		for (Entry<Point, Paint> entry : pointsGridTemp.entrySet()) {
			if ((entry.getKey().x >= gridSizeX || entry.getKey().y >= gridSizeY) && pointsGrid.get(entry.getKey()) != null) {
				pointsGrid.remove(entry.getKey());
			}
		}

		final int maxX = canvas.getWidth() - CANVAS_MARGIN;
		final int maxY = canvas.getHeight() - CANVAS_MARGIN;

		int stepSizeX = (int) Math.floor((double) maxX / gridSizeX);
		int stepSizeY = (int) Math.floor((double) maxY / gridSizeY);
		stepSize = Math.min(stepSizeX, stepSizeY);

		// TODO Zeichne das Gitter
		Paint paintGridLines = new Paint();
		paintGridLines.setColor(Color.BLACK);
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
			canvas.drawLine(startX, startY, stopX, stopY, paintGridLines);
		}

		// draw lines horizontal
		startX = 0;
		stopX = gridSizeX * stepSize;
		for (int i = 0; i <= gridSizeY; i++) {
			startY = (i * stepSize);
			stopY = startY;
			canvas.drawLine(startX, startY, stopX, stopY, paintGridLines);
		}

		// color rectangles
		for (Entry<Point, Paint> entry : pointsGrid.entrySet()) {
			int left = (entry.getKey().x) * stepSize + 1;
			int top = (entry.getKey().y) * stepSize + 1;
			int right = (entry.getKey().x + 1) * stepSize;
			int bottom = (entry.getKey().y + 1) * stepSize;
			Rect rect = new Rect(left, top, right, bottom);
			canvas.drawRect(rect, entry.getValue());
		}
		Log.d(DEBUG_TAG, "pointsGrid size:" + String.valueOf(pointsGrid.size()));

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

				for (PointF pointMotion : pointsMotionEvent) {
					if (pointMotion.x <= (gridSizeX * stepSize) && pointMotion.y <= (gridSizeY * stepSize)) {
						int gridX = (int) Math.floor(pointMotion.x / stepSize);
						int gridY = (int) Math.floor(pointMotion.y / stepSize);
						if (isErasing()) {
							Point point = new Point(gridX, gridY);
							if (pointsGrid.containsKey(point)) {
								pointsGrid.remove(point);
							}
						} else {
							Paint paintRectangle = new Paint();
							paintRectangle.setColor(paintDraw.getColor());
							pointsGrid.put(new Point(gridX, gridY), paintRectangle);
						}
					}
				}
				pointsMotionEvent.clear();

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

	public void resetGridSize() {
		int seekBarProgressDefault = getResources().getInteger(R.integer.grid_size_default);
		setGridSize(seekBarProgressDefault, seekBarProgressDefault);
		invalidate();
	}

	public void startNew() {
		resetGridSize();
		clearDrawing();
	}

	public void setErase(boolean isErase) {
		isErasing = isErase;
	}

	public boolean isErasing() {
		return isErasing;
	}

	public void setColor(String color) {
		Log.d(DEBUG_TAG, "setColor(" + color + ")");
		paintDraw.setColor(Color.parseColor(color));
		invalidate();
	}

	public Map<Point, Paint> getPointsGrid() {
		return pointsGrid;
	}

}
