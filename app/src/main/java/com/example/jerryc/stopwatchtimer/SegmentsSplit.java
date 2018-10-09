package com.example.jerryc.stopwatchtimer;

import android.graphics.Color;
import android.widget.TextView;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Gerardo C on 1/28/2018.
 *
 * Keeps track of all the segments that a segmented stopwatch will have
 * Has operations like split, unsplit, skip split, and saving the segment time
 *
 */

public class SegmentsSplit {


    /**
     * Contains a list of segments for the stopwatch.
     * */
    private List<Segment> segments = new LinkedList<>();

    /**
     * Allows for parsing of time, and formatting to time
     * */
    private TimeOperations timeOperations = new TimeOperations();

    private class Segment implements Serializable {

        /**
         * Is the segment time
         */
        private long segmentTime;

        /**
         * Is the view of the segment
         */
        private TextView segmentView;

        /**
         * Split time saves the time when the split button was pressed for this segment.
         * Will be null if segment has not been split
         */
        private Long splitTime;

        Segment(long segTime, TextView segView) {
            segmentTime = segTime;
            segmentView = segView;
        }

        private void split(long time, boolean showMS){
            splitTime = time;

            //If Segment has previous saved value
            if(segmentTime != 0){
                long offset = segmentTime - time;
                boolean isBehind = offset <= 0;
                long absOffset = Math.abs(offset);

                String offSetSymbol = isBehind ? "+" : "-";
                String timeOffsetAsString = timeOperations.formatTime(absOffset, showMS, true, true);

                String splitTime = offSetSymbol + timeOffsetAsString;
                segmentView.setText(splitTime);
            }
            else {
                segmentView.setText(timeOperations.formatTime(time, showMS, true, false));
            }
        }

        private void unsplit(boolean showMS){
            splitTime = null;
            //Show saved split (segment) time
            segmentView.setText(timeOperations.formatTime(segmentTime, showMS, true, false));
        }

        boolean isSegmentSplitted(){
            return splitTime != null;
        }

        void saveSegment(){
            if (splitTime != null){
                segmentTime = splitTime;
            }
        }

        TextView getSegmentView(){
            return segmentView;
        }

    }

    Segment getNextUnsplitSegment(){
        for(Segment segment : segments){
            if(!segment.isSegmentSplitted()){
                return segment;
            }
        }
        return null;
    }

    private void split(Segment segment, long splitTime, boolean showMS){
        if (segment != null) {
            segment.split(splitTime, showMS);
        }
    }

    TextView getSegmentView(int atIndex){
        return segments.get(atIndex).getSegmentView();
    }

    int getSegmentCount(){
        return segments.size();
    }

    void removeSegment(int atIndex){
        segments.remove(atIndex);
    }

    public void updateCurrentSplitColor(){

        //Removes background colors of all views
        for(Segment segment : segments){
            TextView textView = segment.getSegmentView();
            textView.setBackgroundColor(Color.TRANSPARENT);
        }

        //Finds the current segment we're in and gives it a background color
        Segment currentSegment = getNextUnsplitSegment();

        TextView textView = null;

        if(currentSegment != null){
            textView = currentSegment.getSegmentView();
        }
        else{
            Segment lastSegment = getLastSplitSegment();
            if(lastSegment != null){
                textView = lastSegment.getSegmentView();
            }

        }


        if(textView != null){
            textView.setBackgroundColor(Color.WHITE);
        }
    }

    void splitNextSegment(long splitTime, boolean showMS){
        Segment segmentToSplit = getNextUnsplitSegment();
        split(segmentToSplit, splitTime, showMS);
    }

    public void unsplit(Segment segment, boolean showMS){
        if (segment != null) {
            segment.unsplit(showMS);
        }
    }

    private Segment getLastSplitSegment(){
        ListIterator<Segment> li = segments.listIterator(segments.size());

        // Iterate in reverse.
        while(li.hasPrevious()) {
            Segment segment = li.previous();
            if(segment.isSegmentSplitted()){
                return segment;
            }
        }
        return null;
    }

    /**
     * Unsplits the last segment that was split
     * @return returns true if it unsplit the last segment.
     * */
    boolean unsplitLastSplitSegment(boolean showMS){
        Segment lastSplitSegment = getLastSplitSegment();
        if(lastSplitSegment != null) {
            lastSplitSegment.unsplit(showMS);
        }
        return isLastSplit(lastSplitSegment);
    }

    boolean isLastSplit(Segment segment){
        return segments.indexOf(segment) == segments.size()-1;
    }

    void addSegment(int segmentIndex, long segmentTime, TextView segmentView, boolean showMS){
        segments.add(segmentIndex, new SegmentsSplit.Segment(segmentTime, segmentView));
        segmentView.setText(timeOperations.formatTime(0L, showMS, true, false));
    }

    public void saveSegments(){
        for(Segment segment : segments) {
            segment.saveSegment();
        }
    }

    void unsplitAllSegments(boolean showMS){
        for(Segment segment : segments){
            segment.unsplit(showMS);
        }
    }
}
