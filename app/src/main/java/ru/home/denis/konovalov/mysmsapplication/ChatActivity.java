package ru.home.denis.konovalov.mysmsapplication;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.home.denis.konovalov.mysmsapplication.adapter.MySmsAdapter;
import ru.home.denis.konovalov.mysmsapplication.model.MySms;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ChatActivity.class.getSimpleName();

    public static final String EXTRA_NUMBER = "ChatActivity.phoneNumber";
    public static final String EXTRA_MESSAGES = "ChatActivity.conversation";

    private EditText editText;
    private ImageButton btSend;
    private String number;

    private ArrayList<MySms> conversation;
    private MySmsAdapter adapter;

    private PendingIntent piSending;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        number = getIntent().getStringExtra(EXTRA_NUMBER);
        setTitle(number);

        editText = findViewById(R.id.editText);
        btSend = findViewById(R.id.btSend);
        btSend.setOnClickListener(this);
        RecyclerView listView = findViewById(R.id.lv_messages);

        conversation = getIntent().getParcelableArrayListExtra(EXTRA_MESSAGES);
        if (conversation == null){
            conversation = new ArrayList<>();
        }
        adapter = new MySmsAdapter(conversation);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);

        piSending = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btSend:
                String txt = editText.getText().toString();
                if (!TextUtils.isEmpty(txt)){
                    MySms sms = new MySms(number, txt, System.currentTimeMillis(), MySms.InType.Out);
                    sendMessage(sms, piSending, null);

                    conversation.add(sms);
                    adapter.setItems(conversation);
                    //TODO перенести в обработку подтверждения отправки
                    editText.setText("");
                    Global.toast(this, getString(R.string.sended));
                }
                break;
        }
    }

    public static void sendMessage(MySms sms, PendingIntent sending, PendingIntent receiving) {
        SmsManager manager = SmsManager.getDefault();

        manager.sendTextMessage(sms.getPhone(), null, sms.getMessage(), sending, receiving);
    }

    private class LoadSms extends AsyncTask<String, Void, String> {

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_THREAD_ID = "thread_id";
        public static final String COLUMN_BODY = "body";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_ADDRESS = "address";
        public static final String TYPE_INBOX = "1";

        private String number;

        public LoadSms(String number){
            this.number = number;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            conversation = new ArrayList<>();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            try {
                Uri uriInbox = Uri.parse("content://sms/inbox");

                String where = String.format("%s IS NOT NULL AND %s = '%s'", COLUMN_ADDRESS, COLUMN_ADDRESS, number);
                Cursor inbox = getContentResolver().query(uriInbox, null, where, null, null);
                Uri uriSent = Uri.parse("content://sms/sent");

                Cursor sent = getContentResolver().query(uriSent, null, where, null, null);
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
                        conversation.add(sms);

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
            conversation.trimToSize();
            adapter.setItems(conversation);
        }
    }
}
