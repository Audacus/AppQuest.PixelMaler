package ch.fenceposts.appquest.pixelmaler;

import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import ch.fenceposts.appquest.pixelmaler.listener.MyOnSeekBarChangeListener;
import ch.fenceposts.appquest.pixelmaler.view.DrawingView;

public class MainActivity extends Activity {

	private static final int	RESULT_SETTINGS	= 0;
	private static final String	DEBUG_TAG		= "mydebug";
	private static final int	MINIMUM_PIXEL	= 5;
	private View				drawingView;
	private ImageButton			currentBrush;
	private SeekBar				seekBarGridSizeX;
	private SeekBar				seekBarGridSizeY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		drawingView = (DrawingView) findViewById(R.id.drawingView);
		currentBrush = (ImageButton) findViewById(R.id.imageButtonColor1);

		currentBrush.setImageDrawable(getResources().getDrawable(R.drawable.selected));
		((DrawingView) drawingView).setColor(currentBrush.getTag().toString());

		OnSeekBarChangeListener onSeekBarChangeListener = new MyOnSeekBarChangeListener(this);
		seekBarGridSizeX = (SeekBar) findViewById(R.id.seekBarGridSizeX);
		seekBarGridSizeY = (SeekBar) findViewById(R.id.seekBarGridSizeY);
		seekBarGridSizeX.setOnSeekBarChangeListener(onSeekBarChangeListener);
		seekBarGridSizeY.setOnSeekBarChangeListener(onSeekBarChangeListener);

		((TextView) findViewById(R.id.textViewGridSizeXValue)).setText(String.valueOf(seekBarGridSizeX.getProgress()));
		((TextView) findViewById(R.id.textViewGridSizeYValue)).setText(String.valueOf(seekBarGridSizeY.getProgress()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
			case R.id.action_settings:
				actionSettings();
				return true;
			case R.id.action_clear_drawing:
				actionClearDrawing();
				return true;
			case R.id.action_reset_grid_size:
				actionResetGridSize();
				return true;
			case R.id.action_new_drawing:
				actionNewDrawing();
				return true;
			case R.id.action_log:
				actionLog();
				return true;

		}
		return super.onOptionsItemSelected(item);
	}

	public void actionSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivityForResult(intent, RESULT_SETTINGS);
	}

	public void actionClearDrawing() {
		((DrawingView) drawingView).clearDrawing();
	}

	public void actionNewDrawing() {
		AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
		newDialog.setTitle("New Drawing");
		newDialog.setMessage("Start a new drawing?");
		newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				resetSeekBars();
				((DrawingView) drawingView).startNew();
				dialog.dismiss();
			}
		});
		newDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		newDialog.show();
	}

	public void actionLog() {
		final Map<Point, Paint> pointsGrid = ((DrawingView) drawingView).getPointsGrid();
		if (pointsGrid.size() < MINIMUM_PIXEL) {
			AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
			newDialog.setTitle("log?");
			newDialog.setMessage("Do you really want to log your drawing?");
			newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					log(pointsGrid);
				}
			});
			newDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			newDialog.show();
		} else {
			log(pointsGrid);
		}
	}

	private void log(Map<Point, Paint> pointsGrid) {
		JSONArray jsonArray = new JSONArray();
		try {
			for (Entry<Point, Paint> entry : pointsGrid.entrySet()) {
				JSONObject jsonPixel = new JSONObject();
				jsonPixel.put("x", entry.getKey().x);
				jsonPixel.put("y", entry.getKey().y);
				jsonPixel.put("color", "#" + Integer.toHexString(entry.getValue().getColor()).substring(2));
				jsonArray.put(jsonPixel);
			}
		} catch (JSONException jsone) {
			Log.d(DEBUG_TAG, "JSONException occured on logging!");
			jsone.printStackTrace();
		}
		Log.d(DEBUG_TAG, "jsonArray:" + jsonArray.toString());

		Intent intent = new Intent("ch.appquest.intent.LOG");

		if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
			Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
			return;
		}

		intent.putExtra("ch.appquest.taskname", "PixelMaler");

		// Achtung, je nach App wird etwas anderes eingetragen (siehe Tabelle ganz unten):
		intent.putExtra("ch.appquest.logmessage", jsonArray.toString());
		startActivity(intent);
	}

	private void resetSeekBars() {
		seekBarGridSizeX.setProgress(getResources().getInteger(R.integer.grid_size_default));
		seekBarGridSizeY.setProgress(getResources().getInteger(R.integer.grid_size_default));
	}

	public void actionResetGridSize() {
		resetSeekBars();
		((DrawingView) drawingView).resetGridSize();
	}

	public void colorClicked(View view) {
		if (view != currentBrush) {
			ImageButton imageButtonColor = (ImageButton) view;
			imageButtonColor.setImageDrawable(getResources().getDrawable(R.drawable.selected));
			currentBrush.setImageDrawable(null);
			currentBrush = (ImageButton) view;
		}
		((DrawingView) drawingView).setColor(view.getTag().toString());
		if (view == findViewById(R.id.imageButtonEraser)) {
			((DrawingView) drawingView).setErase(true);
		} else {
			((DrawingView) drawingView).setErase(false);
		}
	}
}
