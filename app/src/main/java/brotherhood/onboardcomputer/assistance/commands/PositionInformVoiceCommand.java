package brotherhood.onboardcomputer.assistance.commands;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.speech.tts.TextToSpeech;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import brotherhood.onboardcomputer.assistance.VoiceAssistanceCommand;
import brotherhood.onboardcomputer.assistance.services.GPSTracker;
import brotherhood.onboardcomputer.assistance.util.ContactsUtil;
import brotherhood.onboardcomputer.ui.dialogs.PhoneContactChooseDialog;

public class PositionInformVoiceCommand extends VoiceAssistanceCommand {
    private static final String[] SEND = new String[]{"wyslij", "wyślij"};
    private static final String[] POSITION = new String[]{"moja pozycje do", "moja pozycja", "moją pozycję do",
            "moją lokalizację do", "pozycję do", "lokalizację do"};
    private String number;
    private GPSTracker gpsTracker;

    public PositionInformVoiceCommand(TextToSpeech speaker) {
        super(speaker);
    }

    @Override
    protected void onStopWordRecognized() {

    }

    @Override
    public VoiceAssistanceCommand setContext(Context context) {
        return super.setContext(context);
    }

    @Override
    protected void initWords() {
        runWords = permuteArrays(SEND, POSITION);
    }

    @Override
    protected void onCommandRecognized(String sentence) {
        HashMap<String, String> searchedNumbers = ContactsUtil.getContactNumberByName(getContext(), getSentenceAfterRunWords());
        if (searchedNumbers == null || searchedNumbers.size() == 0) {
            speak("Nie ma takiego kontaktu w pamięci telefonu");
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
                            getNavigationAndSendMessage(key);
                            return;
                        }
                    }
                }
            });
            contactChooseDialog.show(searchedNumbers);
            return;
        }
        Map.Entry<String, String> entry = searchedNumbers.entrySet().iterator().next();
        getNavigationAndSendMessage(entry.getKey());
    }

    private void getNavigationAndSendMessage(String number) {
        this.number = number;
        gpsTracker = new GPSTracker(getContext());
        if (gpsTracker.canGetLocation()) {
            gpsTracker.stopUsingGPS();
            onLocationUpdate(gpsTracker.getLocation());
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void onLocationUpdate(Location location) {
        if (number != null) {
            speak("Wysyłam obecną lokalizację");
            try {
                Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String city = address.getLocality();
                    String knownName = address.getFeatureName();

                    String textToSend = " ";
                    textToSend += city != null ? city + "," : "";
                    textToSend += knownName != null ? knownName : "";

                    ContactsUtil.sendSMS(this, number, "Znajduję się w " + textToSend);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
