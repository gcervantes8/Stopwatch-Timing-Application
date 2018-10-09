package com.example.jerryc.stopwatchtimer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;



public class MainActivity extends AppCompatActivity implements AbstractTimeFragment.RemoveFragmentListener {


    /**
     *The layout that will contain all the added layouts and fragments of stopwatches and timer
     * */
    private LinearLayout mainLayout;

    /**
     * Used to generate the colors, and come up with what the next color will be
     * */
    private ColorHelper colorHelper = new ColorHelper();


    /**
     * Variable is used for gyroscope detection
     * */
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private DetectShake mShakeDetector;


    public static final String SHARED_PREFERENCES = "Settings";


    /**
     * Contains a list of FragmentHandlers that are being shown.
     * */
    private List<FragmentHandler> fragments = new LinkedList<>();


    /**Private inner class so that fragments and associated layouts they are on are saved together*/
    private class FragmentHandler implements Serializable{

        private LinearLayout fragmentLayout;
        private AbstractTimeFragment fragment;
        private FragmentInfo fragmentInfo;

        FragmentHandler(LinearLayout layout, AbstractTimeFragment f, FragmentInfo fInfo){
            fragmentLayout = layout;
            fragment = f;
            fragmentInfo = fInfo;
        }

        AbstractTimeFragment getFragment(){
            return fragment;
        }
        LinearLayout getFragmentLayout(){
            return fragmentLayout;
        }
        FragmentInfo getFragmentInfo(){
            return fragmentInfo;
        }
    }

    /**From the fragment, returns it's fragmentHandeler, which contains the Layout and FragmentInfo*/
    public FragmentHandler getFragmentHandler(Fragment fragment){
        for (FragmentHandler fragmentHandler : fragments){
            if (fragmentHandler.getFragment() == fragment) {
                return fragmentHandler;
            }
        }
        return null;
    }

    /**
     * Moves the given fragment up or down relative to the other fragments
     * */
    public void moveFragment(Fragment fragment, boolean up){

        FragmentHandler fragmentHandler = getFragmentHandler(fragment);

        if(fragmentHandler == null){
            return;
        }

        LinearLayout layout = fragmentHandler.getFragmentLayout();
        int currentIndex = mainLayout.indexOfChild(layout);

        boolean isValidMove = (up && currentIndex >= 1) || (!up && currentIndex < fragments.size()-1);
        if (isValidMove) { //If fragment is not on top already and moving up
            mainLayout.removeViewAt(currentIndex);
            fragments.remove(currentIndex);

            currentIndex = up ? currentIndex-1 : currentIndex+1;

            mainLayout.addView(layout, currentIndex);
            fragments.add(currentIndex, fragmentHandler);
        }
    }

    /**
     * @param fragment is the fragment you want to change size of
     * @param sizeChange is a float containing how much bigger or smaller you want fragment to be
     * sizeChange values of 2 is twice as big, 0.5 is half as big
     * */
    public void modifyFragmentSize(Fragment fragment, float sizeChange){
        if(fragment == null || sizeChange <= 0){
            return;
        }
        FragmentHandler fragmentHandler = getFragmentHandler(fragment);
        if(fragmentHandler == null){
            return;
        }
        LinearLayout layout = fragmentHandler.getFragmentLayout();
        if(layout == null){
            return;
        }
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) params;
        layoutParams.weight *= sizeChange;

        //Manual refresh of views by removing and adding back to same place, invalidate did not work
        int currentIndex = mainLayout.indexOfChild(layout);
        mainLayout.removeViewAt(currentIndex);
        mainLayout.addView(layout, currentIndex);
        Log.d("TimeTime", "Size change requested");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);

        //Shake detector
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mShakeDetector = new DetectShake();
        mShakeDetector.setOnShakeListener(new DetectShake.OnShakeListener() {

            @Override
            public void onShake() {
                Log.d("TimeTime", "Detected phone shake");
                //If randomize on shake if enabled

                if(Settings.randomizeOnShake){
                    randomizeAllFragmentsDesign();
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mSensor,	SensorManager.SENSOR_DELAY_UI);
        //Loads shared preferences
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        loadSharedPreferences(preferences);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }


    /**
     * Saves all the fragments that are being shown onto the bundle
     * */
    @Override
    public void onSaveInstanceState (Bundle outState){
        FragmentInfo[] fragmentInfos = getFragmentInfos();
        outState.putParcelableArray("Fragments", fragmentInfos);
    }

    /**
     * Restores all the fragments that were saved and restores them by adding them into the activity again
     * Fragments are saved during the onSaveInstanceState overriden method
     * */
    @Override
    public void onRestoreInstanceState (Bundle savedInstanceState){
        if(savedInstanceState == null || savedInstanceState.getParcelableArray("Fragments") == null || !(savedInstanceState.getParcelableArray("Fragments") instanceof FragmentInfo[]) ){
            return;
        }
        FragmentInfo[] fragmentInfos = (FragmentInfo[]) savedInstanceState.getParcelableArray("Fragments");
        if(fragmentInfos == null){
            return;
        }
        for(FragmentInfo fragmentInfo : fragmentInfos) {
            addFragment(fragmentInfo);
        }
    }

    /**
     * Loads settings saves on phone from shared preferences
     * */
    public void loadSharedPreferences(SharedPreferences preferences){
        if(preferences == null){
            return;
        }

        int savedColor = preferences.getInt("bgColor", -1);
        if(savedColor != -1){
            Settings.bgColor = savedColor;
        }

        savedColor = preferences.getInt("textColor", -1);
        if(savedColor != -1){
            Settings.textColor = savedColor;
        }

        savedColor = preferences.getInt("textShadowColor", -1);
        if(savedColor != -1){
            Settings.textShadowColor = savedColor;
        }

        Settings.showMS = preferences.getBoolean("showMS", Settings.showMS);
        Settings.randomizeOnShake = preferences.getBoolean("randomizeOnShake", Settings.randomizeOnShake);
        Settings.timerNotifications = preferences.getBoolean("timerNotification", Settings.timerNotifications);
        Settings.alternatingColors = preferences.getBoolean("alternatingColors", Settings.alternatingColors);
    }

    /**
     * Randomizes the design of all the fragments being shown
     * Randomizes it's layout color, text color, and text shadow color
     * */
    public void randomizeAllFragmentsDesign(){

        for(FragmentHandler fragmentHandler : fragments){
            FragmentInfo fragmentInfo = fragmentHandler.getFragmentInfo();
            LinearLayout layout = fragmentHandler.getFragmentLayout();
            AbstractTimeFragment fragment = fragmentHandler.getFragment();

            int[] colors = colorHelper.generateRandomColors();
            fragmentInfo.setColor(colors[0], colors[1], colors[2]);
            layout.setBackgroundColor(fragmentInfo.getLayoutColor());
            fragment.updateFragment();
        }
    }

    /**
     * Returns an array of all the fragmentInfo. All the fragmentInfo being returned
     * are from all the fragments that are being shown.
     * */
    public FragmentInfo[] getFragmentInfos(){

        FragmentInfo[] fragmentInfos = new FragmentInfo[fragments.size()];
        for(int i = 0; i < fragments.size(); i++){
            fragmentInfos[i] = fragments.get(i).getFragmentInfo();
        }
        return fragmentInfos;
    }

    /**
     * Creates and returns a new LinearLayout
     * Created linear layout contains given layout parameters and background color
     * The layout is also vertical
     * */
    private LinearLayout createLayout(ViewGroup.LayoutParams params, int bgColor){
        LinearLayout layout = new LinearLayout(this);
        layout.setBackgroundColor(bgColor);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.setLayoutParams(params);
        layout.setId(generateViewId());
        return layout;
    }

    /**
     * Creates and returns a LinearLayout that has a width of match parent, height of 0dp with a weight of 1
     * */
    private ViewGroup.LayoutParams verticalItemParams(){
        return  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);

    }

    /**
     * Adds fragment to the activity.
     * Builds fragment from the fragmentInfo
     * */
    public void addFragment(FragmentInfo fragmentInfo){

        LinearLayout layout = addNewLayout(fragmentInfo.getLayoutColor());

        AbstractTimeFragment fragment;
        if(fragmentInfo.getType().equals(Timer.name)){
            fragment = TimerFragment.newInstance(fragmentInfo, Settings.showMS);
        }
        else if(fragmentInfo.getType().equals(Stopwatch.name)){
            fragment = StopwatchFragment.newInstance(fragmentInfo, Settings.showMS);
        }
        else{
            Log.d("TimeTime", "Fragment not added because type wasn't found");
            return;
        }

        fragment.setRemoveFragmentListener(this);
        addTimeFragment(fragment, layout, fragmentInfo);
    }

    /**
     * Creates a LinearLayout and a StopwatchFragment, adds stopwatchfragment to the linearlayout and
     * then adds the layout on the activity
     * */
    private void addTimer(){
        Integer[] settingColors = colorHelper.getSettingColors();
        int[] colors = colorHelper.generateColors(settingColors);

        Integer bgColor = colors[0];
        Integer textColor = colors[1];
        Integer textShadowColor = colors[2];

        final long oneMinuteInMS = 60000;

        LinearLayout layout = addNewLayout(bgColor);

        //Sets up fragmentInfo
        FragmentInfo fragmentInfo = new FragmentInfo(bgColor, Timer.name, textColor, textShadowColor);
        fragmentInfo.setTime(oneMinuteInMS); //Sets initial time to 1 minute
        fragmentInfo.setCountdownTime(oneMinuteInMS); //Sets countdown to 1 minute so after timer finishes and is reset, it's defaulted to 1 minute


        TimerFragment timer = TimerFragment.newInstance(fragmentInfo, Settings.showMS);
        timer.setRemoveFragmentListener(this);


        addTimeFragment(timer, layout, fragmentInfo);
    }



    /**
     * Creates a LinearLayout and a StopwatchFragment, adds stopwatchfragment to the linearlayout and
     * then adds the layout on the activity
     * */
    private void addStopwatch(){
        //Creates default colors, background color is random
        Integer[] settingColors = colorHelper.getSettingColors();
        int[] colors = colorHelper.generateColors(settingColors);

        int bgColor = colors[0];
        int textColor = colors[1];
        int textShadowColor = colors[2];

        LinearLayout layout = addNewLayout(bgColor);
        FragmentInfo fragmentInfo = new FragmentInfo(bgColor, Stopwatch.name, textColor, textShadowColor);
        fragmentInfo.setTime(0); //Stopwatch starts at 0

        StopwatchFragment stopwatch = StopwatchFragment.newInstance(fragmentInfo, Settings.showMS);
        stopwatch.setRemoveFragmentListener(this);


        addTimeFragment(stopwatch, layout, fragmentInfo);
    }

    /**
     * Creates a LinearLayout and a StopwatchFragment, adds stopwatchfragment to the linearlayout and
     * then adds the layout on the activity
     * */
    private void addStopwatchSplit(){
        //Creates default colors, background color is random
        Integer[] settingColors = colorHelper.getSettingColors();
        int[] colors = colorHelper.generateColors(settingColors);

        int bgColor = colors[0];
        int textColor = colors[1];
        int textShadowColor = colors[2];

        LinearLayout layout = addNewLayout(bgColor);
        FragmentInfo fragmentInfo = new FragmentInfo(bgColor, Stopwatch.name, textColor, textShadowColor);
        fragmentInfo.setTime(0); //Stopwatch starts at 0

        StopwatchSplit stopwatchSplit = StopwatchSplit.newInstance(fragmentInfo, Settings.showMS);
        stopwatchSplit.setRemoveFragmentListener(this);


        addTimeFragment(stopwatchSplit, layout, fragmentInfo);
    }

    /**
     * Creates a new layout ands adds it to the mainlayout of the activity
     * */
    private LinearLayout addNewLayout(int color){
        LinearLayout stopwatchLayout = createLayout(verticalItemParams(), color);
        mainLayout.addView(stopwatchLayout);
        return stopwatchLayout;
    }

    /**
     *
     * @param fragment we are going to add to layout and to embed into the activity
     * @param layout is the layout we are adding the fragment to
     * */
    private void addTimeFragment(AbstractTimeFragment fragment, LinearLayout layout, FragmentInfo fragmentInfo){

        //Used to keep track of fragments and layouts they are on, will be used when removing fragment we can also remove layout
        fragments.add(new FragmentHandler(layout, fragment, fragmentInfo));

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(layout.getId(), fragment);
        transaction.commit();
    }

    /**
     * Removes the given fragment from view, also removes the layout it's on
     * */
    public void removeFragment(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        FragmentHandler fragmentHandler = findFragmentHandler(fragment);
        if(fragmentHandler != null) {
            LinearLayout layout = fragmentHandler.getFragmentLayout();
            mainLayout.removeView(layout);
            fragments.remove(fragmentHandler);
        }

        transaction.remove(fragment);
        transaction.commit();
    }

    /**
     * Given a fragment, finds if the fragment is in the FragmentHandler list, if it is, returns the fragmentHandler
     * */
    private FragmentHandler findFragmentHandler(Fragment fragment){
        for(FragmentHandler fragmentHandler : fragments){
            if(fragmentHandler.getFragment() == fragment){
                return fragmentHandler;
            }
        }
        return null;
    }

    /**
     * Starts the settings activity
     * */
    public void startSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //Adds a stopwatch
            case R.id.addStopwatch:
                addStopwatch();
                return true;
            case R.id.addTimer:
                addTimer();
                return true;
            case R.id.addStopwatchSplit:
                addStopwatchSplit();
                return true;
            case R.id.settings:
                startSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**NOTE, IMPLEMENTATION OF GENERATE VIEW ID WAS TAKEN FROM ANDROID STUDIO, was taken so that a lower API phone could still use this method
     * to be able to dynamically add fragments by being able to generate R.id for the layouts that fragments will be in.*/
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in setID.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }


}
