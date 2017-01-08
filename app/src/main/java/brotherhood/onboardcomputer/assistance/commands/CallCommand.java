package brotherhood.onboardcomputer.assistance.commands;

import android.content.Intent;
import android.net.Uri;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Map;

import brotherhood.onboardcomputer.assistance.Command;
import brotherhood.onboardcomputer.assistance.util.ContactsUtil;
import brotherhood.onboardcomputer.ui.dialogs.PhoneContactChooseDialog;

public class CallCommand extends Command {
    private final static String[] CALL_SENTENCES = new String[]{"zadzwoń do", "zatelefonuj do", "dzwoń do", "nawiąż połączenie z"};
    private boolean waitForConfirmSpecificContact;

    public CallCommand(TextToSpeech speaker) {
        super(speaker);
    }

    @Override
    protected void onStopWordRecognized() {

    }

    @Override
    protected void initWords() {
        runWords = CALL_SENTENCES;
    }

    @Override
    protected void onCommandRecognized(String sentence) {
        if (waitForConfirmSpecificContact) {
            return;
        }
        HashMap<String, String> searchedNumbers = ContactsUtil.getContactNumberByName(getContext(), getSentenceAfterRunWords());
        if (searchedNumbers == null || searchedNumbers.size() == 0) {
            speak("Nie ma takiego kontaktu w pamięci telefonu");
            return;
        }

        if (searchedNumbers.size() > 1) {
            PhoneContactChooseDialog contactChooseDialog = new PhoneContactChooseDialog(getContext().getApplicationContext()
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
        number = number.replaceAll("-", "");
        number = number.replaceAll("\\+", "");
        if (number.length() > 9) {
            number = "+" + number;
        }
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }


}
