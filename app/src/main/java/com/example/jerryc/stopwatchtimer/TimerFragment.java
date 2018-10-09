package com.example.jerryc.stopwatchtimer;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RemoveFragmentListener} interface
 * to handle interaction events.
 * Use the {@link TimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerFragment extends AbstractTimeFragment implements Timer.NotificationListener {



    /**The button that will be used to start time initially, button behavior will change when clicked
     * Button can change from start to pause and from pause to resume, that behavior is defined in the
     * click listeners fields below*/
    private Button startButton;

    /**The button that will be used to start time initially, button behavior will change when clicked
     * Button can change from stop to reset, that behavior is defined in the
     * click listeners fields below*/
    private Button stopButton;


    /**
     * Will be used so that user can change the time that the timer starts ticking down from
     * Is also a subclass of TextView
     * */
    private EditText timeEdit;

    private Timer timer;


    /**
     * Click listeners used to change buttons behavior if clicked
     * */
    private View.OnClickListener startClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            resumeTimer();
            setTimeRunningButtons();
        }
    };

    private View.OnClickListener pauseClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timer.pause();
            setTimePausedButtons();
            updateTime(true);
        }
    };

    private View.OnClickListener resumeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            resumeTimer();
            setTimeRunningButtons();
        }
    };

    private View.OnClickListener resetClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timer.reset();

            setResetButtons();
            updateTime(true);
            timeEdit.setEnabled(true);
        }
    };

    public TimerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment TimerFragment.
     */
    public static TimerFragment newInstance(FragmentInfo fragmentInfo, boolean showMS) {

        TimerFragment fragment = new TimerFragment();

        //Sets the new fragmentInfo
        fragment.setFragmentInfo(fragmentInfo);
        fragment.showMS = showMS; //Updates showMS setting
        //Note at this point, timer instance hasn't been created yet
        return fragment;
    }

    /**
     * If timer has has finished (Reached 0:00) then this method is called by timer listener
     * */
    public void notifyTimerFinished(){
        timer.reset();
        setResetButtons();
        if(timer.hasFinished() || !Settings.timerNotifications){
           return;
        }

        Log.d("TimeTime", "Notification sound plays");
        if(getActivity() == null){
            return;
        }
        Log.d("TimeTime", "Notification played");
        NotificationManager notificationManager = (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity().getApplicationContext())

                .setContentTitle("Timer Alert!")
                .setContentText("Timer finished")
                .setSmallIcon(R.drawable.notification_time)
                .setSound(soundUri); //This sets the sound to play

        notificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void onPause(){
        super.onPause();

        //Saves information about whether time is running onto the fragmentInfo
        updateFragmentInfo(!timer.isPaused(), timer.getTime(), timer.getCountdownTime());
        getFragmentInfo().setHasFinished(timer.hasFinished());
        Log.d("TimeTime", "Saved hasFinished, saved: " + timer.hasFinished());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        startButton = (Button) view.findViewById(R.id.start);
        stopButton = (Button) view.findViewById(R.id.stop);
        Button removeButton = (Button) view.findViewById(R.id.removeFragment);
        timeEdit = (EditText) view.findViewById(R.id.timeEdit);


        //Sets listener for remove button, so when remove button is clicked, then it removes the fragment
        final Fragment fragmentToRemove = this;
        removeButton.setOnClickListener(removeButtonClickListener(fragmentToRemove));


        //Uses fragmentInfo given when instance was created to create the timer
        FragmentInfo fragmentInfo = getFragmentInfo();
        setNewTimer(new Timer(fragmentInfo.getTime(), fragmentInfo.getCountdownTime(), fragmentInfo.isRunning(), fragmentInfo.hasFinished(), this));

        updateTime(true);

        setTimeColor(timeEdit);

        //If timer is paused, then it's okay to change to reset buttons
        if(timer.isPaused()) {
            setResetButtons();
        }
        else{
            setTimeRunningButtons();
        }
        // Inflate the layout for this fragment
        return view;
    }

    /**Updates fragment based on the fragmentInfo, so fragmentInfo text color and text shadow color
     * are reapplied to the fragment*/
    @Override
    public void updateFragment() {
        setTimeColor(timeEdit);
    }

    public void setNewTimer(Timer newTimer){
        stopUpdatingTime();
        timer = null;
        timer = newTimer;
        startUpdateTimeThread(timer,timeEdit);
    }

    public void updateTime(boolean useDecimalForMS){
        String formattedString = getTimeOperations().formatTime(timer.getTime(), showMS, useDecimalForMS, false);
        updateTime(formattedString, timeEdit);
    }

    /**
     * Customizes the buttons by giving them a new Strings and click listeners
     * */
    public void setButtons(final String start, final String stop, final View.OnClickListener startClick, final View.OnClickListener stopClick){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startButton.setText(start);
                startButton.setOnClickListener(startClick);
                stopButton.setText(stop);
                stopButton.setOnClickListener(stopClick);
            }
        });
    }

    /**
     * Sets the button so they are at the reset state
     * */
    public void setResetButtons(){
        setButtons(getString(R.string.start), getString(R.string.reset), startClick, resetClick);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopButton.setEnabled(false);
                timeEdit.setEnabled(true);
            }
        });
    }

    /**
     * Sets the buttons so they are the running state
     * */
    public void setTimeRunningButtons(){
        setButtons(getString(R.string.pause), getString(R.string.reset), pauseClick, resetClick);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timeEdit.setEnabled(false);
                stopButton.setEnabled(false);
            }
        });
    }

    public void resumeTimer(){
        String timeInputted = timeEdit.getText().toString();
        long timeMS = getTimeOperations().parseTimeInput(timeInputted, showMS);
        String formattedTime = getTimeOperations().formatTime(timeMS, showMS, false, false);
        timeEdit.setText(formattedTime);
        timeEdit.setEnabled(false);
        timer.start(timeMS);
    }

    /**
     * Sets the buttons so they are the paused state
     * */
    public void setTimePausedButtons(){
        setButtons(getString(R.string.resume), getString(R.string.reset), resumeClick, resetClick);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timeEdit.setEnabled(true);
                stopButton.setEnabled(true);
            }
        });
    }
}
