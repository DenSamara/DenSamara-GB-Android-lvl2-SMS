package ru.home.denis.konovalov.mysmsapplication;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_NUMBER = "ChatActivity.phoneNumber";
    public static final String EXTRA_MESSAGES = "ChatActivity.conversation";

    private EditText editText;
    private ImageButton btSend;
    private String number;

    private ArrayList<MySMS> conversation;

    private PendingIntent piSending;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        number = getIntent().getStringExtra(EXTRA_NUMBER);
        setTitle(number);

        conversation = getIntent().getParcelableArrayListExtra(EXTRA_MESSAGES);
        if (conversation == null){
            conversation = new ArrayList<>();
        }

        editText = findViewById(R.id.editText);
        btSend = findViewById(R.id.btSend);
        btSend.setOnClickListener(this);

        piSending = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btSend:
                String txt = editText.getText().toString();
                if (!TextUtils.isEmpty(txt)){
                    MySMS sms = new MySMS(number, txt, MySMS.InType.Out);
                    sendMessage(sms, piSending, null);

                    conversation.add(sms);
                    //TODO перенести в обработку подтверждения отправки
                    editText.setText("");
                    Global.toast(this, getString(R.string.sended));
                }
                break;
        }
    }

    public static void sendMessage(MySMS sms, PendingIntent sending, PendingIntent receiving) {
        SmsManager manager = SmsManager.getDefault();

        manager.sendTextMessage(sms.getPhone(), null, sms.getMessage(), sending, receiving);
    }
}
