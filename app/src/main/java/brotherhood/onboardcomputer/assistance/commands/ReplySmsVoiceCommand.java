package brotherhood.onboardcomputer.assistance.commands;

import android.speech.tts.TextToSpeech;

import brotherhood.onboardcomputer.assistance.VoiceAssistanceCommand;
import brotherhood.onboardcomputer.assistance.util.ContactsUtil;

public class ReplySmsVoiceCommand extends VoiceAssistanceCommand {
    public static String lastSenderNumber;
    private final static String[] SMS_SENTENCE = new String[]{"odpisz"};

    public ReplySmsVoiceCommand(TextToSpeech speaker) {
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
            ContactsUtil.sendSMS(ReplySmsVoiceCommand.this, lastSenderNumber, message);
            lastSenderNumber = null;
        } else {
            speak("Nie rozumiem");
        }
    }

}
