package bangkokguy.development.android.SmsForwarder;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    private final static boolean DEBUG = true;
    private final static String TAG = "main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(DEBUG)Log.d(TAG, "onCreate");

        // use this to start and trigger a service
        Intent bazdmeg= new Intent(this, MailSenderService.class);
        startService(bazdmeg);
        if(DEBUG)Log.d(TAG, "service bazdmeg started");
    }
}