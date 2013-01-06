package org.danielge.doorbells;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.*;
import android.widget.ListView;

public class MessagesActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.messages_bar, menu);
        return true;
    }

    public static class MessagesListFragment extends ListFragment {
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // setListAdapter(...)
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.message_list, container, false);
            return view;
        }
    }

}
