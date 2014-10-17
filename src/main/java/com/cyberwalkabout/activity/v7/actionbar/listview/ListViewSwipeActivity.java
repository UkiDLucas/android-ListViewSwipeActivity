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
import android.widget.Toast;

/**
 * @author Uki D. Lucas
 *         Abstract support v7 ActionBarActivity that has functionality of ListView
 *         with horizontal swipe detection
 *         and touch/tap on item detection.
 */
public class ListViewSwipeActivity extends ActionBarActivity {

      private static final String TAG = ListViewSwipeActivity.class.getSimpleName();
      private ListView listView;

      private int MIN_HORIZONTAL_SWIPE;
      private int MAX_VERTICAL_SWIPE;
      private int MIN_HORIZONTAL_VELOCITY;
      final boolean RIGHT_SWIPE = true;
      final boolean LEFT_SWIPE = false;

      public ListView getListView() {
            return listView;
      }

      public void getSwipeItem(boolean isRightSwipe, int itemSwiped) {
            Toast.makeText(this,
                  "Detected " + (isRightSwipe ? "right-to-left" : "left-to-right") + " swipe.",
                  Toast.LENGTH_LONG).show();
      }

      public void onItemClickListener(ListAdapter adapter, int position) {

            Toast.makeText(this,
                  "Detected a touch/tap on item in position " + position,
                  Toast.LENGTH_LONG).show();

      }

      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            String tag = TAG + " onCreate()";

            DisplayMetrics dm = getResources().getDisplayMetrics();

            int screenWidth = dm.widthPixels;
            int screenDensity = dm.densityDpi;
            Log.i(tag, "screenDensity = " + screenDensity);
            Log.i(tag, "screenWidth = " + screenWidth);

            MIN_HORIZONTAL_SWIPE = screenWidth / 4; // quarter screen

            MAX_VERTICAL_SWIPE = (int) (dm.densityDpi / 2); // 0.5 inch

            MIN_HORIZONTAL_VELOCITY = (int) (200.0f * dm.densityDpi / 160.0f + 0.5);

            Log.i(tag, "MIN_HORIZONTAL_SWIPE = " + MIN_HORIZONTAL_SWIPE);
            Log.i(tag, "MAX_VERTICAL_SWIPE = " + MAX_VERTICAL_SWIPE);
            Log.i(tag, "MIN_HORIZONTAL_VELOCITY = " + MIN_HORIZONTAL_VELOCITY);
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

      private View.OnTouchListener getOnTouchListener() {
            final GestureDetector gestureDetector = new GestureDetector(this, new TouchAndSwipeGestureListener());

            return new View.OnTouchListener() {
                  public boolean onTouch(View v, MotionEvent event) {
                        return gestureDetector.onTouchEvent(event);
                  }
            };
      }

      private void myOnItemClick(int position) {
            String tag = TAG + "myOnItemClick";
            if (position < 0) {
                  Log.i(tag, "Item clicked is outside ListView area.");
                  return;
            }

            Log.i(tag, "Tap detected on item " + position);
            onItemClickListener(listView.getAdapter(), position);

      }

      private class TouchAndSwipeGestureListener extends SimpleOnGestureListener {

            private int itemTouched = -1;

            // Detect a single-click and call my own handler.
            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                  int pos = listView.pointToPosition((int) e.getX(), (int) e.getY());
                  myOnItemClick(pos);
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
                  String tag = TAG + " onFling";

                  float verticalSwipe = e1.getY() - e2.getY();
                  float horizontalSwipe = e1.getX() - e2.getX();
                  int itemSwiped = listView.pointToPosition((int) e1.getX(), (int) e2.getY());

                  Log.i(tag, "verticalSwipe = " + verticalSwipe);
                  Log.i(tag, "horizontalSwipe = " + horizontalSwipe);
                  Log.i(tag, "horizontalVelocity = " + velocityX);
                  Log.i(tag, "itemTouched = " + itemTouched);
                  Log.i(tag, "itemSwiped = " + itemSwiped);

                  if (Math.abs(verticalSwipe) > MAX_VERTICAL_SWIPE) {
                        Log.i(tag, "Swipe is too long vertical to be considered.");
                        return false;
                  }

                  if (Math.abs(horizontalSwipe) < MIN_HORIZONTAL_SWIPE) {
                        Log.i(tag, "Swipe is too short horizontally to be considered.");
                        return false;
                  }

                  if (Math.abs(velocityX) < MIN_HORIZONTAL_VELOCITY) {
                        Log.i(tag, "Swipe is too slow horizontally to be considered.");
                        return false;
                  }

                  if (itemSwiped < 0) {
                        Log.i(tag, "Swipe outside of the LiftView item.");
                        return false;
                  }

                  if (itemSwiped != itemTouched) {
                        Log.i(tag, "Swipe went outside single item.");
                        return false;
                  }

                  if (verticalSwipe < 0) {
                        getSwipeItem(LEFT_SWIPE, itemSwiped);
                  }
                  else {
                        getSwipeItem(RIGHT_SWIPE, itemSwiped);
                  }

                  return false;
            }

      }
}
