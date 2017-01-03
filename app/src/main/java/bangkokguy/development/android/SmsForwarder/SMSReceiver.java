package bangkokguy.development.android.SmsForwarder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * BroadcastReceiver to receive SMS messages.
 * <p>
 * The received SMS will be passed to the {@link MailSenderService} service class.
 */
public class SMSReceiver extends BroadcastReceiver {

    final static String TAG = "SMSReceiver";
    final static boolean DEBUG = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        final Intent mySendMail = new Intent(context, MailSenderService.class);

        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    if(DEBUG)Log.d(TAG, "phoneNumber: "+ phoneNumber + "; message: " + message);

                    mySendMail.putExtra("phoneNumber", phoneNumber);
                    mySendMail.putExtra("MSG", message);
                    context.startService(mySendMail);

                    if(DEBUG)Log.d(TAG, "mail sender started");
                } // end for
            } // end-if bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
        } // end try
    } // end onReceive
}