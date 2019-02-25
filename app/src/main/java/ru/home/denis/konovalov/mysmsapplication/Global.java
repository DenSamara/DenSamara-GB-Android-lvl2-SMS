package ru.home.denis.konovalov.mysmsapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class Global {

    private static boolean showDebugInfo = true;

    // вывод уведомления в строке состояния
    public static void makeNote(Context context, MySMS message, String channellID) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channellID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(String.format("Sms [%s]", message.getPhone()))
                .setContentText(message.getMessage());
        Intent resultIntent = new Intent(context, SmsReceiver.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(message.getID(), builder.build());
    }

    public static void toast(Context ctx, String txt){
        Toast.makeText(ctx, txt, Toast.LENGTH_LONG).show();
    }

    public static void logE(String tag, String message) {
        if (showDebugInfo)
            Log.e(tag, message);
    }
}
