package ch.fenceposts.appquest.pixelmaler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import ch.fenceposts.appquest.pixelmaler.listener.MyOnSeekBarChangeListener;
import ch.fenceposts.appquest.pixelmaler.view.DrawingView;

public class MainActivity extends Activity {

	private static final int	RESULT_SETTINGS		= 0;
	private static final int	SEEK_BAR_DEFAULT	= 11;
	private static final String	DEBUG_TAG			= "mydebug";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		OnSeekBarChangeListener onSeekBarChangeListener = new MyOnSeekBarChangeListener(this);
		SeekBar seekBarGridSizeX = (SeekBar) findViewById(R.id.seekBarGridSizeX);
		SeekBar seekBarGridSizeY = (SeekBar) findViewById(R.id.seekBarGridSizeY);

		seekBarGridSizeX.setOnSeekBarChangeListener(onSeekBarChangeListener);
		seekBarGridSizeY.setOnSeekBarChangeListener(onSeekBarChangeListener);
		seekBarGridSizeX.setProgress(SEEK_BAR_DEFAULT);
		seekBarGridSizeY.setProgress(SEEK_BAR_DEFAULT);
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
				Log.d(DEBUG_TAG, "action_settings");
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivityForResult(intent, RESULT_SETTINGS);
				return true;
			case R.id.action_clear:
				Log.d(DEBUG_TAG, "action_clear");
				((DrawingView) findViewById(R.id.drawingView)).clearDrawing();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void imageButtonClicked(View view) {
		if (view == findViewById(R.id.imageButtonEraser)) {
			eraserClicked(view);
		} else {
			colorClicked(view);
		}
	}

	private void eraserClicked(View view) {
		// TODO:
		
	}

	private void colorClicked(View view) {
		// TODO:
		
	}
}
