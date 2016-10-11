package brotherhood.onboardcomputer.speechToText.commands;

import android.app.Activity;
import android.speech.tts.TextToSpeech;

import brotherhood.onboardcomputer.speechToText.Command;
import brotherhood.onboardcomputer.speechToText.util.Words;

public class CloseAppCommand extends Command {
    private final static String[] APP = new String[]{"aplikację", "aplikacje", "app", "application"};

    public CloseAppCommand(TextToSpeech speaker) {
        super(speaker);
    }

    @Override
    protected void initWords() {
        runWords = getPermutedArray(Words.STOP_COMMANDS, APP);
    }

    @Override
    protected void onInput(String sentence, boolean firstRun) {
        speak("Zamykanie aplikacji", new SpeakListener() {
            @Override
            public void onFinish() {
                ((Activity) getContext()).finish();
            }
        });
    }

    @Override
    protected void cancel() {

    }
}
