package ru.home.denis.konovalov.mysmsapplication;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Function;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private SmsReceiver receiver;
    private ArrayList<MySMS> messages;
    private MySMSAdapter adapter;
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

                        MySMS sms = new MySMS(phone, msg, Long.parseLong(timestamp), type.equalsIgnoreCase(TYPE_INBOX) ? MySMS.InType.In : MySMS.InType.Out);
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

            adapter = new MySMSAdapter(messages);
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            recyclerView.setAdapter(adapter);
        }
    }
}
