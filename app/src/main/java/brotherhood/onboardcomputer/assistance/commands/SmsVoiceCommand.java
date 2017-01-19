package brotherhood.onboardcomputer.assistance.commands;

import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Map;

import brotherhood.onboardcomputer.assistance.VoiceAssistanceCommand;
import brotherhood.onboardcomputer.assistance.services.SmsReceiver;
import brotherhood.onboardcomputer.assistance.util.ContactsUtil;
import brotherhood.onboardcomputer.assistance.util.Words;
import brotherhood.onboardcomputer.ui.dialogs.PhoneContactChooseDialog;

public class SmsVoiceCommand extends VoiceAssistanceCommand {
    private final static String[] SMS_SENTENCE = new String[]{"sms do", "do", "wiadomość do"};
    private final static String contentSentence = "o treści";

    public SmsVoiceCommand(TextToSpeech speaker) {
        super(speaker);
        SmsReceiver.voiceAssistanceCommand = this;
    }

    @Override
    protected void onStopWordRecognized() {

    }


    @Override
    protected void initWords() {
        runWords = permuteArrays(Words.SEND_COMMANDS, SMS_SENTENCE);
    }

    @Override
    protected void onCommandRecognized(String sentence) {
        if (!getSentenceAfterRunWords().contains(contentSentence)) {
            return;
        }
        String contactName = getSentenceAfterRunWords().substring(0, getSentenceAfterRunWords().indexOf(contentSentence));
        final String message = getSentenceAfterRunWords().substring(getSentenceAfterRunWords().indexOf(contentSentence) + contentSentence.length());
        HashMap<String, String> contacts = ContactsUtil.getContactNumberByName(getContext(), contactName);
        if (contacts.size() == 0) {
            speak("Brak takiego kontaktu");
        } else if (contacts.size() > 1) {
            speak("Wybierz kontakt");
            PhoneContactChooseDialog contactChooseDialog = new PhoneContactChooseDialog(getContext()
                    , new PhoneContactChooseDialog.OnContactChooseListener() {
                @Override
                public void onContactChoose(String contactName) {
                    HashMap<String, String> allContacts = ContactsUtil.getAllContacts(getContext());
                    for (String key : allContacts.keySet()) {
                        if (allContacts.get(key).toLowerCase().equals(contactName.toLowerCase())) {
                            ContactsUtil.sendSMS(SmsVoiceCommand.this, key, message);
                            return;
                        }
                    }
                }
            });
            contactChooseDialog.show(contacts);
        } else {
            speak("Wysyłam wiadomość");
            Map.Entry<String, String> entry = contacts.entrySet().iterator().next();
            System.out.println(entry.getKey());
            ContactsUtil.sendSMS(SmsVoiceCommand.this, entry.getKey(), message);
        }
    }

}
