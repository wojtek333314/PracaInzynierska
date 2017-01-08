package brotherhood.onboardcomputer.assistance.commands;

import android.speech.tts.TextToSpeech;

import brotherhood.onboardcomputer.assistance.VoiceAssistanceCommand;

public class ConfirmVoiceAssistanceCommand extends VoiceAssistanceCommand {
    private final static String CONFIRM_SENTENCES[] = new String[]{"tak", "nie"};

    private ConfirmListener confirmListener;

    public ConfirmVoiceAssistanceCommand(TextToSpeech speaker, ConfirmListener confirmListener) {
        super(speaker);
        this.confirmListener = confirmListener;
    }

    @Override
    protected void onStopWordRecognized() {

    }

    @Override
    protected void initWords() {
        runWords = CONFIRM_SENTENCES;
    }

    @Override
    protected void onCommandRecognized(String sentence) {
        if (sentence.contains(CONFIRM_SENTENCES[0])) {
            confirmListener.onConfirm();
        } else if (sentence.contains(CONFIRM_SENTENCES[1])) {
            confirmListener.onConfirm();
        }
    }

    public interface ConfirmListener {
        void onConfirm();

        void onDisagree();
    }
}
