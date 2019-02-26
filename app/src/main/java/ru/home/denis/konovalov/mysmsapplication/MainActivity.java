package ru.home.denis.konovalov.mysmsapplication;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.selection.OnDragInitiatedListener;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.home.denis.konovalov.mysmsapplication.adapter.MySmsAdapter;
import ru.home.denis.konovalov.mysmsapplication.adapter.MySmsKeyProvider;
import ru.home.denis.konovalov.mysmsapplication.adapter.MySmsLookup;
import ru.home.denis.konovalov.mysmsapplication.model.MySms;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String MY_SELECTION_ID = "my-selection-id";

    private SmsReceiver receiver;
    private ArrayList<MySms> messages;
    private MySmsAdapter adapter;
    private MenuItem selectedItemCount;
    private SelectionTracker mTracker;
    private ActionMode actionMode;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startChatActivity = new Intent(MainActivity.this, ChatActivity.class);
                startChatActivity.putExtra(ChatActivity.EXTRA_NUMBER, "+79879118324");
                startChatActivity.putExtra(ChatActivity.EXTRA_MESSAGES, messages);
                startActivity(startChatActivity);
            }
        });

        setupReceivers();

        recyclerView = findViewById(R.id.listView);
        messages = new ArrayList<>();
        LoadSms task = new LoadSms();
        task.execute();

        if (savedInstanceState != null){
            mTracker.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mTracker.onSaveInstanceState(outState);
    }

    /**
     * При получении будем выводить нотификацию
     */
    private void setupReceivers() {
        receiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("Telephony.Sms.Intents.SMS_DELIVER_ACTION");//SMS
        filter.addAction("Telephony.Sms.Intents.WAP_PUSH_DELIVER_ACTION");//MMS
        registerReceiver(receiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        selectedItemCount = menu.findItem(R.id.action_item_count);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_clear:
                //Убираем выделение
                mTracker.clearSelection();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    class LoadSms extends AsyncTask<String, Void, String> {

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_THREAD_ID = "thread_id";
        public static final String COLUMN_BODY = "body";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_ADDRESS = "address";
        public static final String TYPE_INBOX = "1";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            try {
                Uri uriInbox = Uri.parse("content://sms/inbox");

                Cursor inbox = getContentResolver().query(uriInbox, null, "address IS NOT NULL) GROUP BY (thread_id", null, null);
                Uri uriSent = Uri.parse("content://sms/sent");

                Cursor sent = getContentResolver().query(uriSent, null, "address IS NOT NULL) GROUP BY (thread_id", null, null);
                Cursor c = new MergeCursor(new Cursor[]{inbox,sent}); // Attaching inbox and sent sms

                if (c.moveToFirst()) {
                    for (int i = 0; i < c.getCount(); i++) {
                        String name = null;
                        String phone = "";
                        String _id = c.getString(c.getColumnIndexOrThrow(COLUMN_ID));
                        String thread_id = c.getString(c.getColumnIndexOrThrow(COLUMN_THREAD_ID));
                        String msg = c.getString(c.getColumnIndexOrThrow(COLUMN_BODY));
                        String type = c.getString(c.getColumnIndexOrThrow(COLUMN_TYPE));
                        String timestamp = c.getString(c.getColumnIndexOrThrow(COLUMN_DATE));
                        phone = c.getString(c.getColumnIndexOrThrow(COLUMN_ADDRESS));

                        MySms sms = new MySms(phone, msg, Long.parseLong(timestamp), type.equalsIgnoreCase(TYPE_INBOX) ? MySms.InType.In : MySms.InType.Out);
                        messages.add(sms);

                        c.moveToNext();
                    }
                }
                c.close();

            }catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                Global.logE(TAG, e.toString());
            }

            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            messages.trimToSize();

            setupAdapter();
        }
    }

    private void setupAdapter() {
        adapter = new MySmsAdapter(messages);
        mTracker = new SelectionTracker.Builder<>(
                MY_SELECTION_ID,
                recyclerView,
                new MySmsKeyProvider(1, messages),
                new MySmsLookup(recyclerView),
                StorageStrategy.createLongStorage()
        ).withOnDragInitiatedListener(new OnDragInitiatedListener() {
            @Override
            public boolean onDragInitiated(@NonNull MotionEvent e) {
                Global.logE(TAG, "onDragInitiated");
                return true;
            }
        }).build();

        mTracker.addObserver(new SelectionTracker.SelectionObserver() {
                                 @Override
                                 public void onSelectionChanged() {
                                     super.onSelectionChanged();
                                     if (mTracker.hasSelection() && actionMode == null) {
                                         actionMode = startSupportActionMode(new ActionModeController(MainActivity.this, mTracker));
                                         setMenuItemTitle(mTracker.getSelection().size());
                                     } else if (!mTracker.hasSelection() && actionMode != null) {
                                         actionMode.finish();
                                         actionMode = null;
                                     } else {
                                         setMenuItemTitle(mTracker.getSelection().size());
                                     }
                                     Iterator<MySms> itemIterable = mTracker.getSelection().iterator();
                                     while (itemIterable.hasNext()) {
                                         Global.logE(TAG, String.format("id=%d, number=%s", itemIterable.next().getID(), itemIterable.next().getPhone()));
                                     }
                                 }
                             });

        adapter.setSelectionTracker(mTracker);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);
    }

    public void setMenuItemTitle(int selectedItemSize) {
        selectedItemCount.setTitle("" + selectedItemSize);
    }
}
