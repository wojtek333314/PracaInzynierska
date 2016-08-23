package brotherhood.onboardcomputer.speechToText.commands;

import android.app.Activity;
import android.speech.tts.TextToSpeech;

import brotherhood.onboardcomputer.speechToText.Command;
import brotherhood.onboardcomputer.speechToText.services.SpeechToTextService;

/**
 * Created by Wojtas on 2016-08-23.
 */
public class CloseAppCommand extends Command {
    public CloseAppCommand(SpeechToTextService speechToTextService) {
        super(speechToTextService);
    }

    public CloseAppCommand(TextToSpeech speaker) {
        super(speaker);
    }

    @Override
    protected void initWords() {
        runWords = new String[][]{{"zamknij aplikację"}, {"zamknij aplikacje"}, {"close app"}, {"close application"}};
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
