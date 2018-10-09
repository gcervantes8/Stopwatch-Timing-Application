package com.example.jerryc.stopwatchtimer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by Gerardo C on 1/27/2018.
 */

public class StopwatchSplit extends AbstractTimeFragment {


    /**
     * The time that is being displayed
     * */
    private TextView time;

    /**The button that will be used to start time initially, button behavior will change when clicked
     * Button can change from start to pause and from pause to resume, that behavior is defined in the
     * click listeners fields below*/
    private Button startButton;

    /**The button that be pressed by user when they want to split*/
    private Button splitButton;

    /**The button that be pressed by user when they want to undo a split*/
    private Button unsplitButton;

    /**The button that will be used to start time initially, button behavior will change when clicked
     * Button can change from stop to reset, that behavior is defined in the
     * click listeners fields below*/
    private Button stopButton;

    /**The stopwatch model.
     * Has operations like: start(), pause(), resume(), reset(), getTime(), formatTime(long)
     * Is used to keep track of how much time has passed*/
    private Stopwatch timer;

    private LinearLayout vertical_split_layout;

    private SegmentsSplit segmentsTracker = new SegmentsSplit();

    /**
     * Click listeners used to change buttons behavior if clicked
     * */
    private View.OnClickListener startClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timer.start();
            setTimeRunningButtons();
            segmentsTracker.updateCurrentSplitColor();
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
            segmentsTracker.unsplitAllSegments(showMS);
            updateTime();
        }
    };

    private View.OnClickListener splitClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            segmentsTracker.splitNextSegment(timer.getTime(), showMS);
            //Is was the last split, then also stop timer
            if(segmentsTracker.getNextUnsplitSegment() == null){
                pauseClick.onClick(null);
                startButton.setEnabled(false);
                splitButton.setEnabled(false);
            }
            segmentsTracker.updateCurrentSplitColor();

        }
    };

    private View.OnClickListener unsplitClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean unsplitLastSegment = segmentsTracker.unsplitLastSplitSegment(showMS);
            if(unsplitLastSegment){
                resumeClick.onClick(null);
                splitButton.setEnabled(true);
                Log.d("TimeTime", "Unsplit last segment");
            }
            segmentsTracker.updateCurrentSplitColor();
        }
    };

    public View.OnClickListener addSegment(final int addIndex){
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                TextView segmentView = new TextView(getContext());
                segmentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
                segmentView.setBackgroundColor(Color.WHITE);
                segmentView.setPadding(20,4,20,4);
                vertical_split_layout.addView(segmentView, addIndex, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                segmentsTracker.addSegment(addIndex, 0, segmentView, showMS);
                segmentsTracker.updateCurrentSplitColor();
                Log.d("TimeTime", "Added textview" + vertical_split_layout.getChildCount());
            }
        };
    }


    public View.OnClickListener removeSegment(final int removeIndex){
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                //Only removes if there is more than 1 segment
                if(segmentsTracker.getSegmentCount() != 1) {
                    TextView segTextView = segmentsTracker.getSegmentView(removeIndex);
                    vertical_split_layout.removeView(segTextView);
                    segmentsTracker.removeSegment(removeIndex);

                    segmentsTracker.updateCurrentSplitColor();
                    Log.d("TimeTime", "Removed textview" + vertical_split_layout.getChildCount());
                }
            }
        };
    }

    private void removeLastSegment(){
        int segmentCount = segmentsTracker.getSegmentCount();
        int segmentIndex = segmentCount - 1;
        removeSegment(segmentIndex).onClick(null);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StopwatchFragment.
     */
    public static StopwatchSplit newInstance(FragmentInfo fragmentInfo, boolean showMS) {
        //Note at this point, timer instance hasn't been created yet
        StopwatchSplit fragment = new StopwatchSplit();

        //Sets the new fragmentInfo
        fragment.setFragmentInfo(fragmentInfo);
        fragment.showMS = showMS;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_stopwatch_split, container, false);
        vertical_split_layout = (LinearLayout) view.findViewById(R.id.vertical_split_layout);
        startButton = (Button) view.findViewById(R.id.start);
        splitButton = (Button) view.findViewById(R.id.split);
        unsplitButton = (Button) view.findViewById(R.id.unsplit);
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

        //Adds segments when is first made
        int nInitialSegments = 3;
        for(int i = 0; i < nInitialSegments; i++) {
            addSegment(0).onClick(null);
        }

        //Updates fragmentInfo
        FragmentInfo fragmentInfo = getFragmentInfo();
        setNewStopwatch(new Stopwatch(fragmentInfo.getTime(), fragmentInfo.isRunning()));
        setButtons();

        splitButton.setOnClickListener(splitClick);
        unsplitButton.setOnClickListener(unsplitClick);

        //Updates time once
        updateTime();
        return view;
    }


    /**Sets a new stopwatch time, this will stop any lingering threads from previous stopwatches and reset the timer*/
    public void setNewStopwatch(Stopwatch stopwatch){
        stopUpdatingTime();
        timer = null;
        timer = stopwatch;
        startUpdateTimeThread(timer,time);
    }

    /**
     * Updates the fragment based on the fragment info
     * */
    @Override
    public void updateFragment() {
        setTimeColor(time);
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
        startButton.setEnabled(true);
        splitButton.setEnabled(true);
    }

    /**
     * Sets the button so they are at the running state
     * */
    public void setTimeRunningButtons(){
        setButtons(getString(R.string.pause), getString(R.string.reset), pauseClick, resetClick);
        stopButton.setEnabled(false);
        startButton.setEnabled(true);
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

    public void updateTime(){
        updateTime(timer.getTime(), time);
    }

    public View.OnClickListener configurePopupClickListener(View fragmentView){
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.configure_stopwatch_split_menu, popupMenu.getMenu());

                popupMenu.show();
                Menu menu = popupMenu.getMenu();
                MenuItem item = menu.findItem(R.id.ShowMilliseconds);
                item.setChecked(showMS);

                popupMenu.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener(){
                    public boolean onMenuItemClick(MenuItem item){
                        switch (item.getItemId()){
                            case R.id.addSegment: addSegment(0).onClick(null); break;
                            case R.id.removeSegment: removeLastSegment(); break;
                            case R.id.saveSegments: segmentsTracker.saveSegments(); break;
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
}
