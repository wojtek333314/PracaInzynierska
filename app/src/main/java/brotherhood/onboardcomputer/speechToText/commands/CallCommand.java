package brotherhood.onboardcomputer.speechToText.commands;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Map;

import brotherhood.onboardcomputer.speechToText.Command;
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
        if (getContactNumberByName() == null || getContactNumberByName().size() == 0) {
            speak("Nie ma takiego kontaktu w pamięci telefonu");
            reset();
            return;
        }

        if (getContactNumberByName().size() > 1) {
            PhoneContactChooseDialog contactChooseDialog = new PhoneContactChooseDialog(getContext()
                    , new PhoneContactChooseDialog.OnContactChooseListener() {
                @Override
                public void onContactChoose(String contactName) {
                    for (String key : getAllContacts().keySet()) {
                        if (getAllContacts().get(key).toLowerCase().equals(contactName.toLowerCase())) {
                            callTo(key);
                            return;
                        }
                    }
                }
            });
            contactChooseDialog.show(getContactNumberByName());
            waitForConfirmSpecificContact = true;
            return;
        }
        Map.Entry<String, String> entry = getContactNumberByName().entrySet().iterator().next();
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

    public HashMap<String, String> getContactNumberByName() {
        HashMap<String, String> result = new HashMap<>();
        String contactName = getSentenceAfterRunWords().replaceAll("\\s+", "").toLowerCase();
        HashMap<String, String> allContacts = getAllContacts();
        for (String string : allContacts.keySet()) {
            if (allContacts.get(string).toLowerCase().contains(contactName)
                    || contactName.contains(allContacts.get(string).toLowerCase())) {
                result.put(string, allContacts.get(string));
            }
        }
        return result;
    }

    private HashMap<String, String> getAllContacts() {
        HashMap<String, String> allContacts = new HashMap<>();
        Cursor phones = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
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
}
