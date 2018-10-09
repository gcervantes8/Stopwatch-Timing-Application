package com.example.jerryc.stopwatchtimer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import static java.lang.Thread.sleep;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RemoveFragmentListener} interface
 * to handle interaction events.
 */
public abstract class AbstractTimeFragment extends Fragment {

    /**
     * Listener to be called when fragment should be removed,
     * Calling RemoveFragmentListener.removeFragment(Fragment) notifies activity hosting it to remove the fragment
     * */
    private RemoveFragmentListener removeFragmentListener;

    /**
     * Thread that is used to update the time
     * */
    private Thread timeUpdateThread;

    /**
     * Contains information about the fragment
     * FragmentInfo doesn't update automatically, but it is usually updated before it is going to be destroyed or paused
     * Has a fragmentInfo so it can handle orientation change
     * */
    private FragmentInfo fragmentInfo;


    /**Allows for parsing of time, and formatting to time*/
    private TimeOperations timeOperations = new TimeOperations();

    /**
     * If true will show milliseconds for timers and stopwatches
     * */
    public boolean showMS = false;


    interface RemoveFragmentListener{

    /*
     * Activity hosting the fragments implements this interface.  And the fragment has a reference
     * to RemoveFragmentListener to alert that it should be removed
     * */

        /**
         * Listener that removes the fragment
         * */
        void removeFragment(Fragment fragment);

        /**Moves the fragment up or down relative to other fragments*/
        void moveFragment(Fragment fragment, boolean up);

        /**Modifies the size of the fragment, will also change the size of other fragments
         * If a fragment is made bigger, then other fragments will lower in size to adjust*/
        void modifyFragmentSize(Fragment fragment, float sizeChange);
    }

    /**
     * Sets a new listener for removing the fragment
     * */
    public void setRemoveFragmentListener(RemoveFragmentListener listener){
        removeFragmentListener = listener;
    }

    /**
     * Returns a clickListener containing actions to do if user wants to remove the fragment
     * Stop update thread and calls the remove fragment listener.
     * */
    public View.OnClickListener removeButtonClickListener(final Fragment fragmentToRemove){

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If remove fragment button is clicked, then removes fragment
                if(removeFragmentListener != null){
                    //Stops thread that is updating the time
                    stopUpdatingTime();

                    //Removes the fragment by calling the listener
                    removeFragmentListener.removeFragment(fragmentToRemove);
                }
            }
        };
    }

    /**
     * Returns a clickListener containing actions to do if user wants to remove the fragment
     * Stop update thread and calls the remove fragment listener.
     * */
    public View.OnClickListener moveButtonClickListener(final Fragment fragmentToMove, final boolean up){

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFragmentListener.moveFragment(fragmentToMove, up);
            }
        };
    }

    public void modifyFragmentSize(float sizeChange){
        removeFragmentListener.modifyFragmentSize(this, sizeChange);
    }



    /**
     * Starts a new thread that will be updating the time/textView every 45 ms
     * Time/TextView will be updated based on TimeInterface.format(long,bool)
     * */
    public void startUpdateTimeThread(final TimeInterface timeObject, final TextView textView){

        /*If for some reason there is an update thread that is not null,
        then interrupts it before starting a new one.  Done as a safety precaution
        to ensure 1 fragment can only have 1 other thread updating the time*/
        if(timeUpdateThread != null){
            timeUpdateThread.interrupt();
            timeUpdateThread = null;
        }

        timeUpdateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //noinspection InfiniteLoopStatement
                    while (true) {
                        if (!timeObject.isPaused()) {
                            String formattedTime = timeOperations.formatTime(timeObject.getTime(), showMS, true, false);
                            updateTime(formattedTime, textView);
                        }
                        sleep(45);
                    }
                } catch(InterruptedException e){
                    Log.d("TimeTime", "Interrupted Exception thrown when updating time, timer updating stopped");
                }
            }
        });

        timeUpdateThread.start();
    }




    /**
     * Calls a request on UI Thread to update the time (TextView) being shown
     * @param newTime The time you want to update it to
     * @param timeToUpdate is the textView that is being updated
     * */
    public void updateTime(final String newTime, final TextView timeToUpdate){

        if(getActivity() == null){
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Time is updated to new time given
                timeToUpdate.setText(newTime);
            }
        });
    }

    /**
     * Updates time with the default formatting time settings
     * @param timeToUpdate is the textView that is being updated*/
    public void updateTime(long time, TextView timeToUpdate){

        updateTime(timeOperations.formatTime(time, showMS, true, false), timeToUpdate);
    }

    /**
     * Stops the thread that updates the time by calling an interrupt
     * */
    public void stopUpdatingTime(){
        if(timeUpdateThread != null) {
            timeUpdateThread.interrupt();
            timeUpdateThread = null;
        }
    }

    /**
     * Updates the fragment based on the fragmentInfo
     * Changes all settings of fragment so that they match the fragmentInfo it has
     * */
    public abstract void updateFragment();

    /**
     * Updates textView color and shadow color based on the fragment info
     * */
    public void setTimeColor(TextView time){
        if(fragmentInfo != null) {
            time.setTextColor(fragmentInfo.getTextColor());
            time.setShadowLayer(10, 0, 0, fragmentInfo.getTextShadowColor());
        }
    }

    /**
     * Returns the fragment info of the fragment
     * */
    public FragmentInfo getFragmentInfo(){
        return fragmentInfo;
    }

    /**Updates the fragmentInfo with the given data.
     * Used by the subclasses*/
    public void updateFragmentInfo(boolean isRunning, long timeRunning, long countdownTime){
        fragmentInfo.setTime(timeRunning, countdownTime, isRunning);
    }

    public TimeOperations getTimeOperations(){
        return timeOperations;
    }

    /**
     * Gives the fragment a FragmentInfo
     * Used by the subclasses to initialize fragmentInfo
     * */
    public void setFragmentInfo(FragmentInfo fragmentInfo){
        this.fragmentInfo = fragmentInfo;
    }

    /**
     * Called when fragment is attached to activity
     * If the activity that attached this fragment doesn't implement the removeFragmentListener,
     * then it throws an exception
     * */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RemoveFragmentListener) {
            removeFragmentListener = (RemoveFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        removeFragmentListener = null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopUpdatingTime();
    }
}
