package com.cyberwalkabout.activity.v7.actionbar.listview;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends ListViewSwipeActivity {

      ListView listView;

      @Override public ListView getListView() {

            return listView;
      }

      @Override public void onSwipeDetected(boolean isRightSwipe, int itemSwiped) {

            //Toast.makeText(this, "Detected " + (isRightSwipe ? "right-to-left" : "left-to-right") + " swipe.", Toast.LENGTH_LONG).show();
      }

      @Override public void onItemClickListener(ListAdapter adapter, int position) {
            //Toast.makeText(this, "Detected a touch/tap on item in position " + position, Toast.LENGTH_LONG).show();
      }

      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.list_activity_main);
      }

      @Override
      public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
      }

      @Override
      public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                  return true;
            }
            return super.onOptionsItemSelected(item);
      }
}
