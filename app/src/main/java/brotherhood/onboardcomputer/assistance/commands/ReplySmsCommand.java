package brotherhood.onboardcomputer.assistance.commands;

import android.speech.tts.TextToSpeech;

import brotherhood.onboardcomputer.assistance.Command;
import brotherhood.onboardcomputer.assistance.util.ContactsUtil;

public class ReplySmsCommand extends Command {
    public static String lastSenderNumber;
    private final static String[] SMS_SENTENCE = new String[]{"odpisz"};

    public ReplySmsCommand(TextToSpeech speaker) {
        super(speaker);
    }

    @Override
    protected void onStopWordRecognized() {

    }

    @Override
    protected void initWords() {
        runWords = SMS_SENTENCE;
    }

    @Override
    protected void onCommandRecognized(String sentence) {
        if (lastSenderNumber != null) {
            String message = getSentenceAfterRunWords();
            ContactsUtil.sendSMS(ReplySmsCommand.this, lastSenderNumber, message);
            lastSenderNumber = null;
        } else {
            speak("Nie rozumiem");
        }
    }

}
