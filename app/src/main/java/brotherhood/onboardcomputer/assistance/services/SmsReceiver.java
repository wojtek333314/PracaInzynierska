package brotherhood.onboardcomputer.assistance.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.HashMap;

import brotherhood.onboardcomputer.assistance.Command;
import brotherhood.onboardcomputer.assistance.commands.ConfirmCommand;
import brotherhood.onboardcomputer.assistance.commands.ReplySmsCommand;
import brotherhood.onboardcomputer.assistance.util.ContactsUtil;

public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    public static Command command;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }
                String sender = messages[0].getOriginatingAddress();
                final String message = sb.toString();
                String senderTemp = sender;
                sender = "";
                int counter = 0;
                for (Character character : senderTemp.toCharArray()) {
                    sender += character;
                    if (counter == 2) {
                        sender += " ";
                        counter = 0;
                    } else {
                        counter++;
                    }
                }
                HashMap senderName = ContactsUtil.getContactNameByNumber(command.getContext(), sender);
                command.speak("Otrzymałeś wiadomość od:" + (senderName.keySet().size() > 0 ? senderName : sender) + ".Odczytać?");
                ReplySmsCommand.lastSenderNumber = sender;
                command.registerConfirmCommand(new ConfirmCommand(null, new ConfirmCommand.ConfirmListener() {
                    @Override
                    public void onConfirm() {
                        command.speak(message);
                    }

                    @Override
                    public void onDisagree() {
                    }
                }));
                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();
            }
        }
    }
}