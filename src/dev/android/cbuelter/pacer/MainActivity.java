package dev.android.cbuelter.pacer;

import java.text.DecimalFormat;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements
		NumberPicker.OnValueChangeListener {

	// These are used as default if there are none yet stored in SharedPrefs
	private double distance = 10.00; // internal unit: kilometers
	private double time = 60.00; // internal unit: minutes
	private double speed = 10.00; // internal unit: km/h
	private double pace = 6.00; // internal unit: min/km

	private DecimalFormat df = new DecimalFormat("##.##"); // for distance,
															// speed, pace

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get initial values from storage
		loadFromSharedPreferences();

		// Event handler for reset button
		final Button buttonReset = (Button) findViewById(R.id.buttonReset);
		buttonReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resetValues();
			}
		});

		// Event handler for lock checkBox
		final CheckBox checkBoxLock = (CheckBox) findViewById(R.id.checkBoxLock);
		checkBoxLock.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (checkBoxLock.isChecked()) {
					((EditText) findViewById(R.id.editTextSpeed))
							.setEnabled(false);
					((EditText) findViewById(R.id.editTextPace))
							.setEnabled(false);
				} else {
					((EditText) findViewById(R.id.editTextSpeed))
							.setEnabled(true);
					((EditText) findViewById(R.id.editTextPace))
							.setEnabled(true);
				}
				storeToSharedPreferences();
			}
		});

		// Event handler for EditTexts
		final EditText editTextDistance = (EditText) findViewById(R.id.editTextDistance);
		final EditText editTextSpeed = (EditText) findViewById(R.id.editTextSpeed);
		final EditText editTextPace = (EditText) findViewById(R.id.editTextPace);
		final EditText editTextTime = (EditText) findViewById(R.id.editTextTime);

		// Set initial values in fields and GUI
		df.setMinimumIntegerDigits(2);
		df.setMinimumFractionDigits(2);
		update_gui_from_fields();

		// These get a dialog with 2 numberpickers
		editTextDistance.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					show_dialog_float(editTextDistance, distance);
				}
			}
		});
		editTextSpeed.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					show_dialog_float(editTextSpeed, speed);
				}
			}
		});
		editTextPace.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					show_dialog_float(editTextPace, pace);
				}
			}
		});

		// This one gets a dialog with 3 numberpickers
		editTextTime.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					show_dialog_hms(editTextTime, time);
				}
			}
		});

		// Event handler for spinners
		final Spinner spinnerAffects = (Spinner) findViewById(R.id.spinnerAffects);
		spinnerAffects.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int pos,
					long id) {
				storeToSharedPreferences();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		// do nothing here
	}

	private void resetValues() {
		distance = 10.00;
		time = 60.00;
		speed = 10.00;
		pace = 6.00;
		update_gui_from_fields();
		storeToSharedPreferences();
	}

	// TODO change to resource strings here as well in the conditional!
	private void storeToSharedPreferences() {
		SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
		editor.putFloat("distance", (float) distance);
		editor.putFloat("time", (float) time);
		editor.putFloat("speed", (float) speed);
		editor.putFloat("pace", (float) pace);

		Spinner spinnerAffects = (Spinner) findViewById(R.id.spinnerAffects);
		editor.putInt("spinnerAffectsIndex",
				spinnerAffects.getSelectedItemPosition());

		CheckBox checkBoxLock = (CheckBox) findViewById(R.id.checkBoxLock);
		editor.putBoolean("checkBoxLockChecked", checkBoxLock.isChecked());

		EditText editTextSpeed = (EditText) findViewById(R.id.editTextSpeed);
		editor.putBoolean("editTextSpeedEnabled", editTextSpeed.isEnabled());

		EditText editTextPace = (EditText) findViewById(R.id.editTextPace);
		editor.putBoolean("editTextPaceEnabled", editTextPace.isEnabled());

		editor.commit();
	}

	// TODO change to resource strings here as well in the conditional!
	private void loadFromSharedPreferences() {
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		distance = (double) prefs.getFloat("distance", (float) distance);
		time = (double) prefs.getFloat("time", (float) time);
		speed = (double) prefs.getFloat("speed", (float) speed);
		pace = (double) prefs.getFloat("pace", (float) pace);

		Spinner spinnerAffects = (Spinner) findViewById(R.id.spinnerAffects);
		spinnerAffects.setSelection(prefs.getInt("spinnerAffectsIndex", 0));

		CheckBox checkBoxLock = (CheckBox) findViewById(R.id.checkBoxLock);
		checkBoxLock.setChecked(prefs.getBoolean("checkBoxLockChecked", true));

		EditText editTextSpeed = (EditText) findViewById(R.id.editTextSpeed);
		editTextSpeed
				.setEnabled(prefs.getBoolean("editTextSpeedEnabled", true));

		EditText editTextPace = (EditText) findViewById(R.id.editTextPace);
		editTextPace.setEnabled(prefs.getBoolean("editTextPaceEnabled", true));
	}

	public void show_dialog_hms(final EditText sender, double initialValue) {
		final Dialog d = new Dialog(MainActivity.this);
		d.setTitle("Set Time");
		d.setContentView(R.layout.numberpicker_dialog_hms);

		final NumberPicker numberPickerHours = (NumberPicker) d
				.findViewById(R.id.numberPickerHours);
		final NumberPicker numberPickerMinutes = (NumberPicker) d
				.findViewById(R.id.numberPickerPre);
		final NumberPicker numberPickerSeconds = (NumberPicker) d
				.findViewById(R.id.numberPickerPost);
		int[] initials = minutes_to_hms(initialValue);
		numberPickerHours.setMaxValue(99);
		numberPickerHours.setValue(initials[0]);
		numberPickerHours.setOnValueChangedListener(this);
		numberPickerMinutes.setMaxValue(59);
		numberPickerMinutes.setValue(initials[1]);
		numberPickerMinutes.setOnValueChangedListener(this);
		numberPickerSeconds.setMaxValue(59);
		numberPickerSeconds.setValue(initials[2]);
		numberPickerSeconds.setOnValueChangedListener(this);

		Button buttonOk = (Button) d.findViewById(R.id.buttonOk);
		Button buttonCancel = (Button) d.findViewById(R.id.buttonCancel);
		buttonOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				update_fields(sender, numberPickerHours.getValue(),
						numberPickerMinutes.getValue(),
						numberPickerSeconds.getValue(), true);
				d.dismiss();
				resetFocus();
			}
		});
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				d.dismiss();
				resetFocus();
			}
		});
		d.show();
	}

	/**
	 * Shows a dialog with two numberpickers.
	 * 
	 * @param initialValue
	 *            : e.g. current value of pace, speed or distance
	 * @param postMaxValue
	 *            : e.g. 99 for speed and distance or 59 for pace
	 */
	public void show_dialog_float(final EditText sender, double initialValue) {
		final Dialog d = new Dialog(MainActivity.this);
		d.setTitle("Set Value");
		d.setContentView(R.layout.numberpicker_dialog_float);

		final NumberPicker numberPickerPre = (NumberPicker) d
				.findViewById(R.id.numberPickerPre);
		final NumberPicker numberPickerPost = (NumberPicker) d
				.findViewById(R.id.numberPickerPost);
		int[] initials = double_to_integers(initialValue);
		numberPickerPre.setMaxValue(999);
		numberPickerPre.setValue(initials[0]);
		numberPickerPre.setOnValueChangedListener(this);
		numberPickerPost.setMaxValue(99);
		numberPickerPost.setValue(initials[1]);
		numberPickerPost.setOnValueChangedListener(this);

		Button buttonOk = (Button) d.findViewById(R.id.buttonOk);
		Button buttonCancel = (Button) d.findViewById(R.id.buttonCancel);
		buttonOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				update_fields(sender, numberPickerPre.getValue(),
						numberPickerPost.getValue(), -1, true);
				d.dismiss();
				resetFocus();
			}
		});
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				d.dismiss();
				resetFocus();
			}
		});
		d.show();
	}

	// -------------------------------------------------------------------------------------
	// Some conversion methods to transform the internal values into something
	// that can be displayed in the edittext fields and vice versa
	// -------------------------------------------------------------------------------------

	/**
	 * Splits a double into two integer numbers that we can use in two
	 * numberpickers.
	 * 
	 * @param value
	 * @return
	 */
	private int[] double_to_integers(double value) {
		// Restrict to 2 decimals to apply in numberpickers
		String temp = String.valueOf(df.format(value));
		String[] tokens = new String[2];
		if (temp.indexOf(".") != -1) {
			tokens = temp.split("\\.");
		} else if (temp.indexOf(",") != -1) {
			tokens = temp.split(",");
		}
		int[] result = new int[] { Integer.valueOf(tokens[0]),
				Integer.valueOf(tokens[1]) };
		return result;
	}

	private double integers_to_double(int[] values) {
		String temp = String.format("%02d", values[0]) + "."
				+ String.format("%02d", values[1]);
		double result = Double.valueOf(temp);
		return result;
	}

	private int[] minutes_to_hms(double minutes) {
		int hrs = (int) minutes / 60;
		double remainingMinutes = minutes % 60;
		int mins = (int) remainingMinutes;
		double remainingFraction = remainingMinutes - mins;
		int secs = (int) (60 * remainingFraction);
		int[] result = new int[] { hrs, mins, secs };
		return result;
	}

	private double hms_to_minutes(int[] hms) {
		int hrs = hms[0];
		int mins = hms[1];
		int secs = hms[2];
		double minutes = hrs * 60.0 + mins + secs / 60.0;
		return minutes;
	}

	private String hms_to_string(int[] hms) {
		int hrs = hms[0];
		int mins = hms[1];
		int secs = hms[2];
		String result = String.format("%02d", hrs) + ":"
				+ String.format("%02d", mins) + ":"
				+ String.format("%02d", secs);
		return result;
	}

	// TODO HIER STRING RESSOURCEN AUSWECHSELN/INTEGRIEREN IN DIE ABFRAGE
	/**
	 * Updates the field that corresponds to the given edittext by converting
	 * the numberpicker values. Updates all other related fields as well.
	 * 
	 * @param sender
	 * @param np1_value
	 * @param np2_value
	 * @param np3_value
	 */
	private void update_fields(final EditText sender, int np1_value,
			int np2_value, int np3_value, boolean updateGui) {

		Spinner spinnerAffects = (Spinner) findViewById(R.id.spinnerAffects);

		if (sender == (EditText) findViewById(R.id.editTextDistance)) {
			distance = (integers_to_double(new int[] { np1_value, np2_value }));
			if (((CheckBox) findViewById(R.id.checkBoxLock)).isChecked()) {
				time = calcTime();
			} else {
				speed = calcSpeed();
				pace = calcPace();
			}
		}
		if (sender == (EditText) findViewById(R.id.editTextTime)) {
			time = hms_to_minutes(new int[] { np1_value, np2_value, np3_value });
			if (((CheckBox) findViewById(R.id.checkBoxLock)).isChecked()) {
				distance = calcDistance();
			} else {
				speed = calcSpeed();
				pace = calcPace();
			}
		}
		if (sender == (EditText) findViewById(R.id.editTextSpeed)) {
			speed = integers_to_double(new int[] { np1_value, np2_value });
			pace = calcPaceUsingSpeed();
			if (spinnerAffects.getSelectedItem().toString().equals("Distance")) {
				distance = calcDistance();
			} else {
				time = calcTime();
			}
		}
		if (sender == (EditText) findViewById(R.id.editTextPace)) {
			pace = integers_to_double(new int[] { np1_value, np2_value });
			if (spinnerAffects.getSelectedItem().toString().equals("Distance")) {
				distance = calcDistance();
			} else {
				time = calcTime();
			}
			speed = calcSpeed();
		}

		storeToSharedPreferences();

		if (updateGui) {
			update_gui_from_fields();
		}
	}

	private void update_gui_from_fields() {
		final EditText editTextDistance = (EditText) findViewById(R.id.editTextDistance);
		final EditText editTextSpeed = (EditText) findViewById(R.id.editTextSpeed);
		final EditText editTextPace = (EditText) findViewById(R.id.editTextPace);
		final EditText editTextTime = (EditText) findViewById(R.id.editTextTime);
		editTextDistance.setText(df.format((distance)));
		editTextSpeed.setText(df.format(speed));
		editTextPace.setText(df.format(pace));
		editTextTime.setText(hms_to_string(minutes_to_hms(time)));

		// Update predictions
		final TextView textViewValue5k = (TextView) findViewById(R.id.textViewValue5k);
		final TextView textViewValue10k = (TextView) findViewById(R.id.textViewValue10k);
		final TextView textViewValueHm = (TextView) findViewById(R.id.textViewValueHm);
		final TextView textViewValueM = (TextView) findViewById(R.id.textViewValueM);
		textViewValue5k.setText(predictTime(distance, time, 5.0));
		textViewValue10k.setText(predictTime(distance, time, 10.0));
		textViewValueHm.setText(predictTime(distance, time, 21.0975));
		textViewValueM.setText(predictTime(distance, time, 42.195));
	}

	private void resetFocus() {
		LinearLayout linearLayoutFocusAttractor = (LinearLayout) findViewById(R.id.linearLayoutFocusAttractor);
		linearLayoutFocusAttractor.requestFocus();
	}

	private double calcPace() {
		double pace = time / distance;
		return pace;
	}

	private double calcPaceUsingSpeed() {
		double pace = 60.0 / speed;
		return pace;
	}

	private double calcSpeed() {
		double speed = distance / (time / 60.0);
		return speed;
	}

	private double calcTime() {
		double time = distance * pace;
		return time;
	}

	private double calcDistance() {
		double distance = time / pace;
		return distance;
	}

	/**
	 * Based on the formula here: http://de.wikipedia.org/wiki/Laufsport
	 * 
	 * @param t1
	 *            : known time
	 * @param s1
	 *            : known distance
	 * @param s2
	 *            : new distance
	 * @return
	 */
	private String predictTime(double s1, double t1, double s2) {
		double t2 = t1 * Math.pow((s2 / s1), 1.07);
		int[] hms = minutes_to_hms(t2);
		String result = hms_to_string(hms);
		return result;
	}
}
