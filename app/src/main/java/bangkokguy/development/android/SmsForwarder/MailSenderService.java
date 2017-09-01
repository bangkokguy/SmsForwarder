package bangkokguy.development.android.SmsForwarder;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import bangkokguy.java.mail.Mail;

/**
 * Intent service to send emails from android app.
 * Uses java classes provided by 3rd party.
 * Author bangkokguy
 * Version 1.0
 */
public class MailSenderService extends IntentService {

    final static String TAG = "MailSenderService";
    final static boolean DEBUG = true;
    Context context = this;

    /**
     * Constructor
     */
    public MailSenderService() {
        super("MyCustomService");
    }

    /**
     * Procedure called from inside the class;
     * @param textBody is the String to be sent as email text body;
     *        It comes from the intent extra data;
     */
    static void mySendMail(String textBody, ArrayList<String> sendTo, String subject) {
        //First log in to our email
        Mail m = new Mail("laszlo.g.gergely@gmail.com", "Idefix#12");
        if (DEBUG)Log.d(TAG, "connected to google");

        //Than initialize our email fields
        //String Array to hold the "to" addresses
        m.setTo(sendTo.toArray(new String[0]));  if (DEBUG)Log.d(TAG, "sendTo="+sendTo.toString());
        //String to hold the "email from" address
        m.setFrom("laszlo.g.gergely@gmail.com");
        //String to hold the subject
        m.setSubject("SMS Horse Ear: "+subject);

        if (textBody == null) {
            m.setBody("no text my dear :))"); // no Text now (should not happen)
        } else {
            m.setBody(textBody);} // Text is present

        try { // try to send the mail
            // m.addAttachment("/sdcard/filelocation");  // For attachment
            if(m.send()) {
                if (DEBUG)Log.d(TAG, "---email was sent ok");
            } else {
                Log.e(TAG, "email was not sent, send() returned false");}
        } catch(Exception e) {
            Log.e(TAG, "could not send email, catch path", e);
        }
    }

    @Override
    public void onCreate () {
        super.onCreate();
        if(DEBUG)Log.d(TAG, "onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    /**
     * Prepare mail parameters, send the mail and launch the android notification
     * @param bundle the Bundle containing all the necessary parameters
     */
    protected void prepareMailSending(Bundle bundle) {

        String textBody;
        String subject;
        String phoneNumber;
        String numberOfContactsFound = "null";
        ArrayList<String> senderNames = new ArrayList<>();

        ArrayList<String> sendTo=new ArrayList<>();

        if (bundle != null) {
            textBody = bundle.getString("MSG");
            phoneNumber = bundle.getString("phoneNumber");
            subject = textBody;

            Cursor resultSet = findContactByNumber (context, phoneNumber); Log.d(TAG, Integer.toString(resultSet.getCount()));

            //first try to find a contact with the given phone number
            Log.d(TAG, "trying to find the contact to the phone number");
            if (resultSet.getCount() != 0) { //Contact found!
                numberOfContactsFound = Integer.toString(resultSet.getCount());
                senderNames = getContacts(resultSet);

                Log.d(TAG, "trying to find the groups associated with the contact");
                List<String> list = getGroupsTitle(context, resultSet);

                for (String element : list) {
                    Log.d(TAG, "group names=" + element);

                    if (element.equals("Forward")) {
                        resultSet.moveToFirst();
                        Log.d(TAG, "trying to find the emails associated with the contact");
                        List<String[]> listarr = getEmails(context, resultSet);
                        sendTo = new ArrayList<>();

                        for (String[] element1 : listarr) {
                            Log.d(TAG, "email=" + element1[0]+"----"+element1[1]);
                            if (element1[1]!=null && element1[1].equals("FORWARD"))sendTo.add(element1[0]);
                        } // end for

                        Log.d(TAG, "send mail if everything ok");
                        mySendMail(textBody, sendTo, subject);
                    } // end if

                } // end for

            } // end if
        } else {// bundle is null
            Log.d(TAG, "send mail if bundle is null");
            textBody = "bundle\r\n=\r\nNULL\r\n-->\r\nmail\r\nsend\r\nservice\r\ncalled\r\nfrom\r\nmain\r\nactivitybundle\r\n" +
                    "=\r\n" +
                    "NULL\r\n" +
                    "-->\r\n" +
                    "mail\r\n" +
                    "send\r\n" +
                    "service\r\n" +
                    "called\r\n" +
                    "from\r\n" +
                    "main\r\n" +
                    "activity";
            sendTo.add("laszlo.g.gergely@gmail.com");
            sendTo.add("laszlo.g.gergely@gmail.com");
            sendTo.add("laszlo.g.gergely@gmail.com");
            senderNames.add("Horse Ear");
            senderNames.add("Horse Ear");
            phoneNumber = "000"; //dummy value; probably not necessary
            subject = ":)";
            mySendMail(textBody, sendTo, subject);
        }
        SetMyNotification (context, textBody, phoneNumber, senderNames, sendTo, numberOfContactsFound);
    }

    @Override
    protected void onHandleIntent(Intent arg0) {
        // TODO Auto-generated method stub
        if(DEBUG)Log.d(TAG, "onHandleIntent");
        prepareMailSending(arg0.getExtras());
    }

    /**
     * Returns a Cursor object that can be used to find out further
     * details about the contact located by its number.
     * The phoneNumber argument must specify a full or a part phone number.
     * @param   phoneNumber is the search string to find
     * @return  Cursor the cursor with all the rows matching phoneNumber
     */
    private Cursor findContactByNumber(Context context, String phoneNumber) {

        return context.getContentResolver().query(
            Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber)), // uri
            new String[] {  // projection
                ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.DATA_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
            ContactsContract.PhoneLookup.IN_VISIBLE_GROUP + " = '" + ("1") + "'", //selection
            null,           //selectionArgs
            null);          //sortOrder);

    }

    /**
     * Returns a List object containing the Group Titles
     * associated with the contact we are using
     * @param   contact tbd
     * @return  List<String>
     */
    private ArrayList<String> getContacts(Cursor contact) {

        ArrayList<String> listContacts = new ArrayList<>();
        Cursor cursorContactId = contact;

        if (cursorContactId.moveToFirst()) { // get the contact_name from the first row/third column
            listContacts.add(cursorContactId.getString(2));
        }

        while (cursorContactId.moveToNext()) {
            listContacts.add(cursorContactId.getString(2));
        }

        return listContacts;
    }

    /**
     * Returns a List object containing the Group Titles
     * associated with the contact we are using
     * This method always returns immediately, whether or not the
     * List contains any data.
     * @param   context tbd
     * @param   contact tbd
     * @return  List<String>
     */
    private List<String> getGroupsTitle(Context context, Cursor contact) {

        List<String> groupsTitle = new ArrayList<>();
        String contactId = null;
        Cursor cursorContactId = contact;

        if (cursorContactId.moveToFirst()) { // get the contact_id from the first row/first column
            contactId = cursorContactId.getString(0);
        }

        //cursorContactId.close(); // cursor is not needed any more

        if (contactId == null)
            return null;
        List<String> groupIdList = new ArrayList<>();

        Cursor cursorGroupId = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.DATA1},
                String.format("%s=? AND %s=?", ContactsContract.Data.CONTACT_ID, ContactsContract.Data.MIMETYPE),
                new String[]{contactId, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE},
                null);

        while (cursorGroupId.moveToNext()) {
            String groupId = cursorGroupId.getString(0);
            groupIdList.add(groupId);
        }
        cursorGroupId.close();

        Cursor cursorGroupTitle = context.getContentResolver().query(
            ContactsContract.Groups.CONTENT_URI, new String[]{ContactsContract.Groups.TITLE},
            ContactsContract.Groups._ID + " IN (" + TextUtils.join(",", groupIdList) + ")",
            null,
            null);

        while (cursorGroupTitle.moveToNext()) {
            String groupName = cursorGroupTitle.getString(0);
            groupsTitle.add(groupName);
        }
        cursorGroupTitle.close();

        return groupsTitle;
    }

    /**
     * get email addresses
     * @param context The applications Context
     * @param contact The Cursor containing the Contacts matching with the SMS number
     * @return List<String> The Email List associated with the first Contact
     */
    private List<String[]> getEmails(Context context, Cursor contact) {
        List<String[]> emailList = new ArrayList<>();

        String contactId = null;
        Cursor cursorContactId = contact;

        if (cursorContactId.moveToFirst()) { // get the contact_id from the first row/first column
            contactId = cursorContactId.getString(0);
        }

        cursorContactId.close(); // cursor is not needed any more

        if (contactId == null)
            return null;

        Cursor emailCur = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{contactId},
                null);
        while (emailCur.moveToNext()) {
            String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            String label = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA3));
            String[] arr=new String[]{email, label};
            emailList.add(arr); // Here you will get list of email
        }
        emailCur.close();

        return emailList;
    }

    /**
     * Prepare and send the android Notification
     *
     * @param   context the applications context
     * @param   msgText message text string
     * @param   msgSenderNumber the sender's phone number in string format
     * @param   msgSenderName List containing all the contact names associated with the sending phone number
     * @param   msgSenderEmail List containing all the emails associated with the sending contact
     * @param   numberOfContactsFound The count in String format of all the contacts found associated with the sending phone number
     */
    public void SetMyNotification (
            Context context,
            String msgText,
            String msgSenderNumber,
            ArrayList<String> msgSenderName,
            ArrayList<String> msgSenderEmail,
            String numberOfContactsFound) {

        Intent intent=new Intent(context, Notifications.class);
        Bundle bundle = new Bundle();
        bundle.putString("msgText", msgText);
        bundle.putString("msgSenderNumber", msgSenderNumber);
        bundle.putStringArrayList("msgSenderName", msgSenderName);
        bundle.putStringArrayList("msgSenderEmail", msgSenderEmail);
        bundle.putString("numberOfContactsFound", numberOfContactsFound);
        intent.putExtras(bundle);
        startService(intent);
    }
}