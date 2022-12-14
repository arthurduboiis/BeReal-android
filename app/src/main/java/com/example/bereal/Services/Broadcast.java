package com.example.bereal.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.bereal.MainActivity;
import com.example.bereal.R;

public class Broadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "broadcast", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(context.getApplicationContext(), NotificationService.class);
        context.getApplicationContext().startService(i);
        createNotification(context, "Time to take a BeReal", "⚠️ 2 min to take a BeReal", "It's time to take a BeReal");
    }

    private void createNotification(Context context, String title, String description, String a) {

        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int NOTIFICATION_ID = 234;
        String CHANNEL_ID ="";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CHANNEL_ID = "my_channel_01";
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);

            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notif_icon)
                .setContentTitle(title)
                .setTicker(a)
                .setContentText(description);

        builder.setContentIntent(notificationIntent);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setAutoCancel(true);


        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}