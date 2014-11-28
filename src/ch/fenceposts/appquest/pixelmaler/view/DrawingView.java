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
import android.view.MotionEvent;
import android.view.View;

/**
 * Die DrawingView ist für die Darstellung und Verwaltung der Zeichenfläche
 * zuständig.
 */
public class DrawingView extends View {

	private int gridSizeX;
	private int gridSizeY;
	private int stepSize;
	private int[][] grid;
	private Map<Point, Paint> pointsGrid;
	private Path drawPath = new Path();
	private Paint drawPaint = new Paint();
	private Paint linePaint = new Paint();
	private boolean isErasing = false;

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(20);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);

		linePaint.setColor(0xFF666666);
		linePaint.setAntiAlias(true);
		linePaint.setStrokeWidth(1.0f);
		linePaint.setStyle(Paint.Style.STROKE);
		
//		this.gridSizeX = gridSizeX;
//		this.gridSizeY = gridSizeY;
		this.gridSizeX = 12;
		this.gridSizeY = 8;
		this.grid = new int[gridSizeX][gridSizeY];
		this.pointsGrid = new HashMap<>();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		final int maxX = canvas.getWidth();
		final int maxY = canvas.getHeight();

		int stepSizeX = (int) Math.ceil((double) maxX / gridSizeX);
		int stepSizeY = (int) Math.ceil((double) maxY / gridSizeY);
		stepSize = Math.max(stepSizeX, stepSizeY);

		// TODO Zeichne das Gitter
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		int startX;
		int stopX;
		int startY;
		int stopY;
		
		// draw lines vertical
		startY = 0;
		stopY = maxY;
		for (int i = 0; i < gridSizeX; i++) {
			startX = (i * stepSize);
			stopX = startX;
			canvas.drawLine(startX, startY, stopX, stopY, paint);
		}
		
		// draw lines horizontal
		startX = 0;
		stopX = maxX;
		for (int i = 0; i < gridSizeY; i++) {
			startY = (i * stepSize);
			stopY = startY;
			canvas.drawLine(startX, startY, stopX, stopY, paint);
		}
		
		// color rectangles
		for (Entry<Point, Paint> entry : pointsGrid.entrySet()) {
			int left = (entry.getKey().x - 1) * stepSize;
			int top = (entry.getKey().y - 1) * stepSize;
			int right = entry.getKey().x * stepSize;
			int bottom = entry.getKey().y * stepSize;
			Rect rect = new Rect(left, top, right, bottom);
			canvas.drawRect(rect, entry.getValue());
		}

		// Zeichnet einen Pfad der dem Finger folgt
		canvas.drawPath(drawPath, drawPaint);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();
		
		List<PointF> pointsMotionEvent = new ArrayList<>();

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
				int gridX = (int) Math.ceil(point.x);
				int gridY = (int) Math.ceil(point.y);
				
				pointsGrid.put(new Point(gridX, gridY), new Paint(drawPaint.getColor()));
			}

			drawPath.reset();
			break;
		default:
			return false;
		}

		invalidate();
		return true;
	}

	public void startNew() {

		// TODO Gitter löschen
		// böh?

		invalidate();
	}

	public void setErase(boolean isErase) {
		isErasing = isErase;
		if (isErasing) {
			drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		} else {
			drawPaint.setXfermode(null);
		}
	}

	public boolean isErasing() {
		return isErasing;
	}

	public void setColor(String color) {
		invalidate();
		drawPaint.setColor(Color.parseColor(color));
	}
}
