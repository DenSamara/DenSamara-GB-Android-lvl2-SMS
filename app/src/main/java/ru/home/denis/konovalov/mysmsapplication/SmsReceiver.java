package ru.home.denis.konovalov.mysmsapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

    public static final String PDUS = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) { // Минимальные проверки
            Object[] pdus = (Object[]) intent.getExtras().get(PDUS);  // Получаем сообщения
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++)
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String smsFromPhone = messages[0].getDisplayOriginatingAddress();
            StringBuilder body = new StringBuilder();
            for (int i = 0; i < messages.length; i++)
                body.append(messages[i].getMessageBody());

            String bodyText = body.toString();
            Global.makeNote(context, new MySMS(smsFromPhone, bodyText, MySMS.InType.In), "2");

            //Можно запустить службу с сохранением

            abortBroadcast();   // Это будет работать только на Андроиде ниже 4.4
        }
    }
}
