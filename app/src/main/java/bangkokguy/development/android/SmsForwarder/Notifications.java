package bangkokguy.development.android.SmsForwarder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

/**
 * Service Class to send the android Notification.
 * The Notification will point to Result Activity.
 */
public class Notifications extends Service {

    private final String TAG="Notifications";
    private final boolean DEBUG=true;

    private final Context context = this;

    NotificationManager nm;
    Notification noti;
    Notification.Builder nb;
    Notification.InboxStyle ns;

    /**
     * Constructor with no parameters, with no function
     */
    public Notifications() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        ns = new Notification.InboxStyle();
        nb = new Notification.Builder(context);
        //create notification handler in order to be able to send a notification
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {

        if(DEBUG)Log.d(TAG, "onStartCommand invoked");

        //play the sms sound
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.unlock);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(DEBUG)Log.d(TAG, "release media player");
                mp.release();
            }
        });
        if(DEBUG)Log.d(TAG, "playback started");

        //Get the SMS parameters from the invoking intent
        Bundle bundle = intent.getExtras();
        String msgText = bundle.getString("msgText");
        String msgSenderNumber = bundle.getString("msgSenderNumber");
        ArrayList<String> msgSenderName = bundle.getStringArrayList("msgSenderName");
        ArrayList<String> msgSenderEmail = bundle.getStringArrayList("msgSenderEmail");
        String numberOfContactsFound = bundle.getString("numberOfContactsFound");

        //create intent to get launched when user selects notification
        Intent resultActivityIntent = new Intent(context, ResultActivity.class);

        //pass the SMS parameters to the intent
        Bundle intentBundle = new Bundle();
        intentBundle.putString("msgText", msgText);
        intentBundle.putString("msgSenderNumber", msgSenderNumber);
        intentBundle.putStringArrayList("msgSenderName", msgSenderName);
        intentBundle.putStringArrayList("msgSenderEmail", msgSenderEmail);
        intentBundle.putString("numberOfContactsFound", numberOfContactsFound);

        resultActivityIntent.putExtras(intentBundle);

        PendingIntent pi =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultActivityIntent,
                        PendingIntent.FLAG_ONE_SHOT);

        //create the new notification
        ns
                .addLine(msgText)
                .setBigContentTitle("Forwarded SMS")
                .setSummaryText("click to see all");

        nb
                .setContentTitle("Forwarded SMS")
                .setContentIntent(pi)
                .setSubText("click...")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(ns)
                .setContentText("with the following Texts");

        nm.notify(1, nb.build());

		return Service.START_NOT_STICKY;
	}
}