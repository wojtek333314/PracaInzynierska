package brotherhood.onboardcomputer.assistance.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.SmsManager;

import java.util.HashMap;

import brotherhood.onboardcomputer.assistance.Command;
import brotherhood.onboardcomputer.utils.Helper;

public class ContactsUtil {
    //key - number, value - name
    public static HashMap<String, String> getContactNumberByName(Context context, String contactName) {
        HashMap<String, String> result = new HashMap<>();
        contactName = contactName.replaceAll("\\s+", "").toLowerCase();
        HashMap<String, String> allContacts = getAllContacts(context);
        for (String string : allContacts.keySet()) {
            if (allContacts.get(string).toLowerCase().contains(contactName)
                    || contactName.contains(allContacts.get(string).toLowerCase())) {
                result.put(string, allContacts.get(string));
            }
        }
        return result;
    }

    public static HashMap<String, String> getContactNameByNumber(Context context, String number) {
        HashMap<String, String> result = new HashMap<>();
        number = number.replaceAll("\\s+", "").toLowerCase();
        HashMap<String, String> allContacts = getAllContacts(context);
        for (String string : allContacts.keySet()) {
            if (string.contains(number) || string.equals(number)) {
                result.put(string, allContacts.get(string));
            }
        }
        return result;
    }

    public static HashMap<String, String> getAllContacts(Context context) {
        HashMap<String, String> allContacts = new HashMap<>();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (phones != null) {
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                allContacts.put(phoneNumber, name);
            }
            phones.close();
        }
        return allContacts;
    }

    public static void sendSMS(final Command command, String phoneNumber, String message) {
        String SENT = "SMS_SENT_ACTION";
        String DELIVERED = "SMS_DELIVERED_ACTION";

        SmsManager sms = SmsManager.getDefault();
        PendingIntent sentPI = PendingIntent.getBroadcast(
                command.getContext().getApplicationContext(), 0, new Intent(SENT),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Intent deliveryIntent = new Intent(DELIVERED);
        PendingIntent deliverPI = PendingIntent.getBroadcast(
                command.getContext().getApplicationContext(), 0, deliveryIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        command.getContext().registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String result = "";

                switch (getResultCode()) {

                    case Activity.RESULT_OK:
                        result = "Transmission successful";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        result = "Transmission failed";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        result = "Radio off";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        result = "No PDU defined";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        result = "No service";
                        break;
                }

                Helper.showToast(context, result);
            }

        }, new IntentFilter(SENT));

        command.getContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                command.speak("Wiadomość dostarczono");
            }
        }, new IntentFilter(DELIVERED));

        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliverPI);
    }
}
