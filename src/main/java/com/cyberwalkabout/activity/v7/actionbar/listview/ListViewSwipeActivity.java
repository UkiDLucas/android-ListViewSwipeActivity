package com.cyberwalkabout.activity.v7.actionbar.listview;

/**
 * @author Uki D. Lucas uki@CyberWalkAbout.com (c)2014
 * You are free to reuse this class as long as you keep this entire comment.
 */

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * @author Uki D. Lucas
 *         Abstract support v7 ActionBarActivity that has functionality of ListView
 *         with horizontal swipe detection
 *         and touch/tap on item detection.
 */
public abstract class ListViewSwipeActivity extends ActionBarActivity {

    private static final String TAG = ListViewSwipeActivity.class.getSimpleName();
    final boolean RIGHT_SWIPE = true; //TODO rename
    final boolean LEFT_SWIPE = false;
    private ListView listView;
    private int MIN_HORIZONTAL_SWIPE;
    private int MAX_VERTICAL_SWIPE;
    private int MIN_HORIZONTAL_VELOCITY;

    /**
     * Implement this method to return your Activity ListView.
     */
    public abstract ListView getListView();

    /**
     * Anyone using ListViewSwipeActivity in their code can call this method to detect single clicks.
     *
     * @param adapter
     * @param position
     */
    public abstract void onItemClickListener(ListAdapter adapter, int position);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String tag = TAG + " onCreate()";
        setDeviceDimensions(tag);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listView = getListView();
        if (listView == null) {
            new Throwable("ListView is not provided.");
        }
        listView.setOnTouchListener(getOnTouchListener());
    }

    /**
     * This needs to happen only once, so we will call it in onCreate().
     */
    private void setDeviceDimensions(String tag) {
        DisplayMetrics dm = getResources().getDisplayMetrics();

        int screenWidth = dm.widthPixels;
        int screenDensity = dm.densityDpi;
        Log.i(tag, "screenDensity = " + screenDensity);
        Log.i(tag, "screenWidth = " + screenWidth);

        // if you swipe less than a quarter of screen width, I will not consider it a swipe
        MIN_HORIZONTAL_SWIPE = (int) screenWidth / 4; // quarter screen

        // if you scroll MORE than 0.5 inch I will not consider is a swipe
        MAX_VERTICAL_SWIPE = (int) (screenDensity / 2); // 0.5 inch

        //TODO need a little more study
        MIN_HORIZONTAL_VELOCITY = (int) (200.0f * screenDensity / 160.0f + 0.5);

        Log.i(tag, "MIN_HORIZONTAL_SWIPE = " + MIN_HORIZONTAL_SWIPE);
        Log.i(tag, "MAX_VERTICAL_SWIPE = " + MAX_VERTICAL_SWIPE);
        Log.i(tag, "MIN_HORIZONTAL_VELOCITY = " + MIN_HORIZONTAL_VELOCITY);
    }

    private View.OnTouchListener getOnTouchListener() {
        final GestureDetector gestureDetector = new GestureDetector(this, new TouchAndSwipeGestureListener());

        return new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
    }

    /**
     * Implement this method to handle single touch events.
     */
    private void onItemTap(int itemTouched) {
        String tag = TAG + "onItemTap";
        if (itemTouched < 0) {
            // I am not sure in what scenario this would happen
            Log.i(tag, "Item clicked is outside ListView area.");
            return;
        }
        Log.i(tag, "Tap detected on item " + itemTouched);
        onItemClickListener(listView.getAdapter(), itemTouched);
    }

    /**
     * Implement this method to handle detected swipe events.
     */
    public abstract void onSwipeRightToLeft(int itemSwiped);

    /**
     * could use it for loading pages of database data
     */
    public abstract void onVerticalSwipeUp();

    /**
     * could use it for loading pages of database data
     */
    public abstract void onVerticalSwipeDown();

    /**
     * Implement this method to handle detected swipe events.
     */
    public abstract void onSwipeLeftToRight(int itemSwiped);

    private class TouchAndSwipeGestureListener extends SimpleOnGestureListener {

        private int itemTouched = -1;

        /**
         * User touched the screen, but from here they may continue to other gesture.
         *
         * @param e
         * @return
         */

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            // translate position of the finger (the event) to item number of the ListView
            int pos = listView.pointToPosition((int) e.getX(), (int) e.getY());
            onItemTap(pos);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            String tag = TAG + " onDown";
            itemTouched = listView.pointToPosition((int) e.getX(), (int) e.getY());
            Log.i(tag, "Item touched down " + itemTouched);
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            String tag = TAG + ".onFling()";

            int verticalSwipe = (int) (e1.getY() - e2.getY()); // gives us some amount of pixels
            int horizontalSwipe = (int) (e1.getX() - e2.getX()); // gives us some amount of pixels

            // detecting which ListView item we are on on the END of the fling (e1).
            int itemSwipeEnd = listView.pointToPosition((int) e2.getX(), (int) e2.getY());

            Log.d(tag, "verticalSwipe = " + verticalSwipe);
            Log.d(tag, "horizontalSwipe = " + horizontalSwipe);
            Log.d(tag, "horizontalVelocity = " + velocityX);
            Log.d(tag, "itemTouched = " + itemTouched);
            Log.d(tag, "itemSwipeEnd = " + itemSwipeEnd);

            if (Math.abs(verticalSwipe) > MAX_VERTICAL_SWIPE) {
                Log.i(tag, "Swipe is too long vertically to be considered.");
                if (verticalSwipe > 0) {
                    onVerticalSwipeUp();
                } else {
                    onVerticalSwipeDown();
                }
                return false;
            }

            if (Math.abs(horizontalSwipe) < MIN_HORIZONTAL_SWIPE) {
                Log.i(tag, "Swipe is too short horizontally to be considered.");

                //TODO implement onShortSwipe();
                return false;
            }

            if (Math.abs(velocityX) < MIN_HORIZONTAL_VELOCITY) {
                Log.i(tag, "Swipe is too slow horizontally to be considered.");
                return false;
            }

            if (itemSwipeEnd < 0) {
                Log.i(tag, "Swipe outside of the ListView item.");
                return false;
            }

            if (itemTouched != itemSwipeEnd) {
                Log.i(tag, "Swipe went outside single item. It would be ambiguous otherwise.");
                return false;
            }

            if (verticalSwipe > 0) {
                onSwipeLeftToRight(itemSwipeEnd);
            } else {
                onSwipeRightToLeft(itemSwipeEnd);
            }
            // If you got this far it means Swipe/Fling was detected.
            return true;
        }

    }


}
