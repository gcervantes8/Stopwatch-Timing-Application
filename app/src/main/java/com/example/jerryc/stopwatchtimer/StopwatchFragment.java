package com.example.jerryc.stopwatchtimer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RemoveFragmentListener} interface
 * to handle interaction events.
 * Use the {@link StopwatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StopwatchFragment extends AbstractTimeFragment {


    /**
     * The time that is being displayed
     * */
    private TextView time;

    /**The button that will be used to start time initially, button behavior will change when clicked
     * Button can change from start to pause and from pause to resume, that behavior is defined in the
     * click listeners fields below*/
    private Button startButton;

    /**The button that will be used to start time initially, button behavior will change when clicked
     * Button can change from stop to reset, that behavior is defined in the
     * click listeners fields below*/
    private Button stopButton;

    /**The stopwatch model.
     * Has operations like: start(), pause(), resume(), reset(), getTime(), formatTime(long)
     * Is used to keep track of how much time has passed*/
    private Stopwatch timer;


    /**
     * Click listeners used to change buttons behavior if clicked
     * */
    private View.OnClickListener startClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timer.start();
            setTimeRunningButtons();
        }
    };

    private View.OnClickListener pauseClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timer.pause();
            updateTime();
            setTimePausedButtons();
        }
    };

    private View.OnClickListener resumeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timer.resume();
            setTimeRunningButtons();
        }
    };

    private View.OnClickListener resetClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timer.reset();
            setResetButtons();

            updateTime();
        }
    };

    public StopwatchFragment() {
        // Required empty public constructor
    }


    /**
     * Updates the fragment based on the fragment info
     * */
    @Override
    public void updateFragment() {
        setTimeColor(time);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StopwatchFragment.
     */
    public static StopwatchFragment newInstance(FragmentInfo fragmentInfo, boolean showMS) {
        //Note at this point, timer instance hasn't been created yet
        StopwatchFragment fragment = new StopwatchFragment();

        //Sets the new fragmentInfo
        fragment.setFragmentInfo(fragmentInfo);
        fragment.showMS = showMS;

        return fragment;
    }

    @Override
    public void onPause(){
        super.onPause();
        updateFragmentInfo(!timer.isPaused(), timer.getTime(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        startButton = (Button) view.findViewById(R.id.start);
        stopButton = (Button) view.findViewById(R.id.stop);
        Button removeButton = (Button) view.findViewById(R.id.removeFragment);
        Button configureButton = (Button) view.findViewById(R.id.configureFragment);

        time = (TextView) view.findViewById(R.id.time);
        setTimeColor(time);

        Button moveUpButton = (Button) view.findViewById(R.id.moveUp);
        Button moveDownButton = (Button) view.findViewById(R.id.moveDown);



        //Sets listener for remove button, so when remove button is clicked, then it removes fragment
        final Fragment thisFragment = this;
        removeButton.setOnClickListener(removeButtonClickListener(thisFragment));
        configureButton.setOnClickListener(configurePopupClickListener(view));

        moveUpButton.setOnClickListener(moveButtonClickListener(thisFragment, true));
        moveDownButton.setOnClickListener(moveButtonClickListener(thisFragment, false));

        //Updates fragmentInfo
        FragmentInfo fragmentInfo = getFragmentInfo();
        setNewStopwatch(new Stopwatch(fragmentInfo.getTime(), fragmentInfo.isRunning()));
        setButtons();

        //Updates time once
        updateTime();
        return view;
    }

    public View.OnClickListener configurePopupClickListener(View fragmentView){
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.configure_stopwatch_menu, popupMenu.getMenu());

                popupMenu.show();
                Menu menu = popupMenu.getMenu();
                MenuItem item = menu.findItem(R.id.ShowMilliseconds);
                item.setChecked(showMS);

                popupMenu.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener(){
                    public boolean onMenuItemClick(MenuItem item){
                        switch (item.getItemId()){
                            case R.id.ShowMilliseconds: showMS = !showMS; updateTime(); break;
                            case R.id.increaseSize: modifyFragmentSize(1.3f); break;
                            case R.id.decreaseSize: modifyFragmentSize(0.666f); break;
                            case R.id.openAdvancedConfigurations: break;
                            default: break;
                        }
                        return true;
                    }
                });
            }
        };
    }

    public void updateTime(){
        updateTime(timer.getTime(), time);
    }

    public void setNewStopwatch(Stopwatch stopwatch){
        stopUpdatingTime();
        timer = null;
        timer = stopwatch;
        startUpdateTimeThread(timer,time);
    }

    /**
     * Changes the text on the 2 buttons and the click listeners for the 2 buttons
     * */
    public void setButtons(String start, String stop, View.OnClickListener startClick, View.OnClickListener stopClick){
        startButton.setText(start);
        startButton.setOnClickListener(startClick);
        stopButton.setText(stop);
        stopButton.setOnClickListener(stopClick);
    }

    /**
     * Sets the button so they are at the reset state
     * */
    public void setResetButtons(){
        setButtons(getString(R.string.start), getString(R.string.reset), startClick, resetClick);
        stopButton.setEnabled(false);
    }

    /**
     * Sets the button so they are at the running state
     * */
    public void setTimeRunningButtons(){
        setButtons(getString(R.string.pause), getString(R.string.reset), pauseClick, resetClick);
        stopButton.setEnabled(false);
    }

    /**
     * Sets the button so they are at the pause state
     * */
    public void setTimePausedButtons(){
        setButtons(getString(R.string.resume), getString(R.string.reset), resumeClick, resetClick);
        stopButton.setEnabled(true);
    }

    private void setButtons(){
        if(timer.getTime() < 10){
            Log.d("TimeTime", "Reset buttons set");


            setResetButtons();
        }
        else{
            if(timer.isPaused()){
                setTimePausedButtons();
                Log.d("TimeTime", "Pause buttons set");
            }
            else{
                setTimeRunningButtons();
                Log.d("TimeTime", "Running buttons set");
            }
        }
    }
}
