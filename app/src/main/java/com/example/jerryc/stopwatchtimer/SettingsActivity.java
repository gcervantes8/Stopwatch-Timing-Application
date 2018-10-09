package com.example.jerryc.stopwatchtimer;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.LinkedList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {


    /**
     * True if the activity was just called
     * Used to differentiate start up calls from calls after start time
     * Used due to spinners onItemSelectedListener being called when a spinner initializes
     */
    private boolean initialDisplay = true;

    /**
     * A mapping from a color's name to it's color from the Color class
     */
    private class ColorNameMap {
        String colorName;
        Integer color;

        private ColorNameMap(String colorString, Integer color) {
            this.colorName = colorString;
            this.color = color;
        }
    }

    /**
     * This is where we will store the settings information
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Spinner backgroundColorSpinner = (Spinner) findViewById(R.id.colorSpinnerBackground);
        Spinner textColorSpinner = (Spinner) findViewById(R.id.colorSpinnerText);
        Spinner textShadowColorSpinner = (Spinner) findViewById(R.id.colorSpinnerTextShadow);
        CheckedTextView showMSCheckBox = (CheckedTextView) findViewById(R.id.checkedTextViewMS);
        Switch randomizeDesignSwitch = (Switch) findViewById(R.id.randomizeDesignSwitch);
        Switch alternatingColorsSwitch = (Switch) findViewById(R.id.alternatingColorsSwitch);
        Switch timerNotificationsSwitch = (Switch) findViewById(R.id.timerNotificationsSwitch);


        //Gives click listeners to all the switches
        setRandomizeSwitchClickListener(randomizeDesignSwitch);
        setAlternatingColorsClickListener(alternatingColorsSwitch);
        setTimerNotificationClickListener(timerNotificationsSwitch);

        setCheckBoxClickListener(showMSCheckBox);
        setSpinnerClickListeners(backgroundColorSpinner, textColorSpinner, textShadowColorSpinner);
        setSpinnerAdapters(createSpinnerList(textColorSpinner, textShadowColorSpinner));
        backgroundColorSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, createColorStringList(getString(R.string.random_color))));


        //Gives initial values to checkbox, if we had them on settings as true, then make them checked
        randomizeDesignSwitch.setChecked(Settings.randomizeOnShake);
        alternatingColorsSwitch.setChecked(Settings.alternatingColors);
        timerNotificationsSwitch.setChecked(Settings.timerNotifications);
        initializeButtons(showMSCheckBox, backgroundColorSpinner, textColorSpinner, textShadowColorSpinner);




        //allows Up arrow on top of activity, so user can go back to previous activity
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Saves the options selected
        saveSharedPreferences();
    }

    /**
     * Loads settings saves on phone from shared preferences
     */
    public void saveSharedPreferences() {
        new Thread(new Runnable() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                if (Settings.bgColor != null) {
                    editor.putInt("bgColor", Settings.bgColor);
                }
                if (Settings.textColor != null) {
                    editor.putInt("textColor", Settings.textColor);
                }
                if (Settings.textShadowColor != null) {
                    editor.putInt("textShadowColor", Settings.textShadowColor);
                }
                editor.putBoolean("showMS", Settings.showMS);
                editor.putBoolean("randomizeOnShake", Settings.randomizeOnShake);
                editor.putBoolean("timerNotification", Settings.timerNotifications);
                editor.putBoolean("alternatingColors", Settings.alternatingColors);
                editor.commit();
            }
        }).start();
    }

    /**
     * Returns a list of all the colors that can be used
     *
     * @param additionalItem appears at the front of the list if not null
     */
    private List<String> createColorStringList(String additionalItem) {
        List<String> colors = new LinkedList<>();
        if (additionalItem != null) {
            colors.add(additionalItem);
        }
        for (ColorNameMap colorMap : createColorList()) {
            if (colorMap.colorName != null) {
                colors.add(colorMap.colorName);
            }
        }
        return colors;
    }

    /**
     * Gives the switch an action listener for randomize switch
     */
    private void setRandomizeSwitchClickListener(final Switch randomizeSwitch) {
        randomizeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Settings.randomizeOnShake = randomizeSwitch.isChecked();
            }
        });
    }

    /**
     * Gives the switch an action listener for alternating color
     */
    private void setAlternatingColorsClickListener(final Switch alternatingColorsSwitch) {
        alternatingColorsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Settings.alternatingColors = alternatingColorsSwitch.isChecked();
            }
        });
    }

    /**
     * Gives the switch an action listener for timer notification
     */
    private void setTimerNotificationClickListener(final Switch timerNotificationSwitch) {
        timerNotificationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Settings.timerNotifications = timerNotificationSwitch.isChecked();
            }
        });
    }

    /**
     * Initializes the buttons so they have all the previous settings set
     */
    public void initializeButtons(CheckedTextView showMSCheckBox, Spinner s1, Spinner s2, Spinner s3) {

        showMSCheckBox.setChecked(Settings.showMS);

        setSpinner(s1, Settings.bgColor);
        setSpinner(s2, Settings.textColor);
        setSpinner(s3, Settings.textShadowColor);
    }

    /**
     * Initializes the spinner to be selecting the color given
     *
     * @param colorSelected color that you want the spinner to be selecting
     *                      Only a supports the amount of colors that can be selected
     */
    private void setSpinner(Spinner spinner, Integer colorSelected) {
        if (colorSelected == null) {
            return;
        }
        List<ColorNameMap> colorList = createColorList();
        for (int i = 0; i < colorList.size(); i++) {
            if (colorSelected.equals(colorList.get(i).color)) {
                spinner.setSelection(i + 1);
            }
        }
    }

    /**
     * Given the color string, returns the actual color, for example red returns the color red
     *
     * @return null if found no matching color
     */
    private Integer getColor(String color) {
        if (color == null) {
            return null;
        }
        for (ColorNameMap colorMap : createColorList()) {
            if (color.equals(colorMap.colorName)) {
                return colorMap.color;
            }
        }
        return null; //default color
    }

    /**
     * Creates a list of the colors that are supported
     */
    private List<ColorNameMap> createColorList() {
        List<ColorNameMap> colorList = new LinkedList<>();
        colorList.add(new ColorNameMap(getString(R.string.red), Color.RED));
        colorList.add(new ColorNameMap(getString(R.string.green), Color.GREEN));
        colorList.add(new ColorNameMap(getString(R.string.blue), Color.BLUE));
        colorList.add(new ColorNameMap(getString(R.string.yellow), Color.YELLOW));
        colorList.add(new ColorNameMap(getString(R.string.purple), Color.argb(255, 158, 66, 244)));
        colorList.add(new ColorNameMap(getString(R.string.black), Color.BLACK));
        colorList.add(new ColorNameMap(getString(R.string.white), Color.WHITE));
        return colorList;
    }

    /**
     * Gives the checkbox a listener so that it toggles when clicked
     * Checkbox also controls whether showMS is checked or not
     */
    private void setCheckBoxClickListener(final CheckedTextView showMS) {
        showMS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                boolean wasChecked = showMS.isChecked();
                showMS.setChecked(!wasChecked);
                Settings.showMS = !wasChecked;
            }
        });
    }

    /**
     * Returns a list of the given spinners
     */
    private List<Spinner> createSpinnerList(final Spinner s1, final Spinner s2) {
        List<Spinner> spinnerList = new LinkedList<>();
        spinnerList.add(s1);
        spinnerList.add(s2);
        return spinnerList;
    }

    /**
     * Gives the List of spinner a spinner adapter with all the colors
     */
    private void setSpinnerAdapters(List<Spinner> spinners) {
        ArrayAdapter<String> colorDefaultAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, createColorStringList(getString(R.string.default_color)));
        for (Spinner spinner : spinners) {
            spinner.setAdapter(colorDefaultAdapter);
        }
    }

    /**
     * Given the three spinner, sets them a listener.
     */
    private void setSpinnerClickListeners(Spinner backgroundColor, Spinner textColor, Spinner textShadowColor) {

        backgroundColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //Prevents spinner from being called at startup
                if (initialDisplay) {
                    initialDisplay = false;
                    return;
                }
                Settings.bgColor = getColor((String) adapterView.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Settings.bgColor = null;
            }
        });

        textColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //Prevents spinner from being called at startup
                if (initialDisplay) {
                    initialDisplay = false;
                    return;
                }
                Settings.textColor = getColor((String) adapterView.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Settings.textColor = null;
            }
        });

        textShadowColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //Prevents spinner from being called at startup
                if (initialDisplay) {
                    initialDisplay = false;
                    return;
                }
                Settings.textShadowColor = getColor((String) adapterView.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Settings.textShadowColor = null;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this); //better design but not currently working
                saveSharedPreferences(); //Done to save all the settings
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
