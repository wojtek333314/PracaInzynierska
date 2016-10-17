package brotherhood.onboardcomputer.speechToText.commands;

import android.speech.tts.TextToSpeech;

import brotherhood.onboardcomputer.speechToText.Command;

public class ConfirmCommand extends Command {
    private final static String CONFIRM_SENTENCES[] = new String[]{"tak"};
    private final static String DISAGREE_SENTENCES[] = new String[]{"nie"};

    private ConfirmListener confirmListener;

    public ConfirmCommand(TextToSpeech speaker, ConfirmListener confirmListener) {
        super(speaker);
        this.confirmListener = confirmListener;
    }

    @Override
    protected void initWords() {
        runWords = CONFIRM_SENTENCES;
        stopWords = DISAGREE_SENTENCES;
    }

    @Override
    protected void onInput(String sentence, boolean firstRun) {
        confirmListener.onConfirm();
    }

    @Override
    protected void cancel() {
        confirmListener.onDisagree();
    }

    public interface ConfirmListener {
        void onConfirm();
        void onDisagree();
    }
}
