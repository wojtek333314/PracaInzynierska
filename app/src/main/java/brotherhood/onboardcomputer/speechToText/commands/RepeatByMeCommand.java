package brotherhood.onboardcomputer.speechToText.commands;

import android.speech.tts.TextToSpeech;

import brotherhood.onboardcomputer.speechToText.Command;

public class RepeatByMeCommand extends Command {

    public RepeatByMeCommand(TextToSpeech speaker) {
        super(speaker);
    }

    @Override
    protected void initWords() {
        runWords = new String[]{"powtórz", "repeat"};
    }

    @Override
    protected void onInput(String sentence, boolean firstRun) {
        if (!isRunning)
            speak(getSentenceAfterRunWords(), new SpeakListener() {
                @Override
                public void onFinish() {
                    isRunning = false;
                }
            });
    }

    @Override
    protected void cancel() {

    }
}
