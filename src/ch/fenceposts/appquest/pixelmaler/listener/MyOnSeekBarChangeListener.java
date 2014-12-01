package ch.fenceposts.appquest.pixelmaler.listener;

import android.app.Activity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import ch.fenceposts.appquest.pixelmaler.R;
import ch.fenceposts.appquest.pixelmaler.view.DrawingView;

public class MyOnSeekBarChangeListener implements OnSeekBarChangeListener {

	private View		drawingView;
	private SeekBar		seekBarGridSizeX;
	private SeekBar		seekBarGridSizeY;
	private TextView	textViewGridSizeXValue;
	private TextView	textViewGridSizeYValue;

	public MyOnSeekBarChangeListener(Activity activity) {
		this.drawingView = (DrawingView) activity.findViewById(R.id.drawingView);
		this.seekBarGridSizeX = (SeekBar) activity.findViewById(R.id.seekBarGridSizeX);
		this.seekBarGridSizeY = (SeekBar) activity.findViewById(R.id.seekBarGridSizeY);
		this.textViewGridSizeXValue = (TextView) activity.findViewById(R.id.textViewGridSizeXValue);
		this.textViewGridSizeYValue = (TextView) activity.findViewById(R.id.textViewGridSizeYValue);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (progress < 1) {
			seekBar.setProgress(1);
		}
		if (seekBar == seekBarGridSizeX) {
			textViewGridSizeXValue.setText(String.valueOf(progress));
			((DrawingView) drawingView).setGridSizeX(progress);
		} else if (seekBar == seekBarGridSizeY) {
			textViewGridSizeYValue.setText(String.valueOf(progress));
			((DrawingView) drawingView).setGridSizeY(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}
}
