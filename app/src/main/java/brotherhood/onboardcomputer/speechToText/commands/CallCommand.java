package brotherhood.onboardcomputer.speechToText.commands;

import android.content.Intent;
import android.net.Uri;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Map;

import brotherhood.onboardcomputer.speechToText.Command;
import brotherhood.onboardcomputer.speechToText.util.ContactsUtil;
import brotherhood.onboardcomputer.ui.dialogs.PhoneContactChooseDialog;

public class CallCommand extends Command {
    private final static String[] CALL_SENTENCES = new String[]{"zadzwoń do", "zatelefonuj do", "dzwoń do", "nawiąż połączenie z"};
    private boolean waitForConfirmSpecificContact;

    public CallCommand(TextToSpeech speaker) {
        super(speaker);
    }

    @Override
    protected void initWords() {
        runWords = CALL_SENTENCES;
    }

    @Override
    protected void onInput(String sentence, boolean firstRun) {
        if (waitForConfirmSpecificContact) {
            return;
        }
        HashMap<String, String> searchedNumbers = ContactsUtil.getContactNumberByName(getContext(), getSentenceAfterRunWords());
        if (searchedNumbers == null || searchedNumbers.size() == 0) {
            speak("Nie ma takiego kontaktu w pamięci telefonu");
            reset();
            return;
        }

        if (searchedNumbers.size() > 1) {
            PhoneContactChooseDialog contactChooseDialog = new PhoneContactChooseDialog(getContext()
                    , new PhoneContactChooseDialog.OnContactChooseListener() {
                @Override
                public void onContactChoose(String contactName) {
                    HashMap<String, String> allContacts = ContactsUtil.getAllContacts(getContext());
                    for (String key : allContacts.keySet()) {
                        if (allContacts.get(key).toLowerCase().equals(contactName.toLowerCase())) {
                            callTo(key);
                            return;
                        }
                    }
                }
            });
            contactChooseDialog.show(searchedNumbers);
            waitForConfirmSpecificContact = true;
            return;
        }
        Map.Entry<String, String> entry = searchedNumbers.entrySet().iterator().next();
        callTo(entry.getKey());
    }

    private void callTo(String number) {
        if (number.length() > 9) {
            number = "+" + number;
        }
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        getContext().startActivity(intent);
        reset();
    }

    @Override
    protected void cancel() {

    }


}
