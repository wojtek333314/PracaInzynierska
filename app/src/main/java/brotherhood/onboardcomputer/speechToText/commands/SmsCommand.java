package brotherhood.onboardcomputer.speechToText.commands;

import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Map;

import brotherhood.onboardcomputer.speechToText.Command;
import brotherhood.onboardcomputer.speechToText.services.SmsReceiver;
import brotherhood.onboardcomputer.speechToText.util.ContactsUtil;
import brotherhood.onboardcomputer.speechToText.util.Words;
import brotherhood.onboardcomputer.ui.dialogs.PhoneContactChooseDialog;

public class SmsCommand extends Command {
    private final static String[] SMS_SENTENCE = new String[]{"sms do", "do", "wiadomość do"};
    private final static String contentSentence = "o treści";

    public SmsCommand(TextToSpeech speaker) {
        super(speaker);
        SmsReceiver.command = this;
    }


    @Override
    protected void initWords() {
        runWords = getPermutedArray(Words.SEND_COMMANDS, SMS_SENTENCE);
    }

    @Override
    protected void onInput(String sentence, boolean firstRun) {
        if (!getSentenceAfterRunWords().contains(contentSentence)) {
            reset();
            return;
        }
        String contactName = getSentenceAfterRunWords().substring(0, getSentenceAfterRunWords().indexOf(contentSentence));
        final String message = getSentenceAfterRunWords().substring(getSentenceAfterRunWords().indexOf(contentSentence) + contentSentence.length());
        HashMap<String, String> contacts = ContactsUtil.getContactNumberByName(getContext(), contactName);
        if (contacts.size() == 0) {
            speak("Brak takiego kontaktu");
            reset();
        } else if (contacts.size() > 1) {
            speak("Wybierz kontakt");
            PhoneContactChooseDialog contactChooseDialog = new PhoneContactChooseDialog(getContext()
                    , new PhoneContactChooseDialog.OnContactChooseListener() {
                @Override
                public void onContactChoose(String contactName) {
                    HashMap<String, String> allContacts = ContactsUtil.getAllContacts(getContext());
                    for (String key : allContacts.keySet()) {
                        if (allContacts.get(key).toLowerCase().equals(contactName.toLowerCase())) {
                            ContactsUtil.sendSMS(SmsCommand.this, key, message);
                            return;
                        }
                    }
                }
            });
            contactChooseDialog.show(contacts);
        } else {
            speak("Wysyłam wiadomość");
            Map.Entry<String, String> entry = contacts.entrySet().iterator().next();
            ContactsUtil.sendSMS(SmsCommand.this, entry.getKey(), message);
        }
    }

    @Override
    protected void cancel() {

    }
}
