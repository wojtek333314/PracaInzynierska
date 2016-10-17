package brotherhood.onboardcomputer.speechToText.commands;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;

import brotherhood.onboardcomputer.speechToText.Command;
import brotherhood.onboardcomputer.speechToText.util.Words;
import brotherhood.onboardcomputer.utils.Helper;

public class SmsCommand extends Command {
    private final static String[] SMS_SENTENCE = new String[]{"sms do"};

    public SmsCommand(TextToSpeech speaker) {
        super(speaker);
    }

    @Override
    protected void initWords() {
        runWords = getPermutedArray(Words.SEND_COMMANDS, SMS_SENTENCE);
    }

    @Override
    protected void onInput(String sentence, boolean firstRun) {
        sendSMS("666109334", getSentenceAfterRunWords());
    }

    @Override
    protected void cancel() {

    }

    private void sendSMS(final String phoneNumber, String message) {
        String SENT = "SMS_SENT_ACTION";
        String DELIVERED = "SMS_DELIVERED_ACTION";

        SmsManager sms = SmsManager.getDefault();
        PendingIntent sentPI = PendingIntent.getBroadcast(
                getContext().getApplicationContext(), 0, new Intent(SENT),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Intent deliveryIntent = new Intent(DELIVERED);
        PendingIntent deliverPI = PendingIntent.getBroadcast(
                getContext().getApplicationContext(), 0, deliveryIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        getContext().registerReceiver(new BroadcastReceiver() {

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

                Helper.showToast(getContext(),result);
            }

        }, new IntentFilter(SENT));
     /* Register for Delivery event */
        getContext().registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Helper.showToast(getContext(),"deliveered");
            }

        }, new IntentFilter(DELIVERED));

        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliverPI);
    }

}
