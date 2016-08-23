package brotherhood.onboardcomputer.speechToText.commands;

import android.speech.tts.TextToSpeech;

import brotherhood.onboardcomputer.speechToText.Command;
import brotherhood.onboardcomputer.speechToText.services.SpeechToTextService;

/**
 * Created by Wojtas on 2016-08-20.
 */
public class HelloCommand extends Command {

    public HelloCommand(SpeechToTextService speechToTextService) {
        super(speechToTextService);
    }

    public HelloCommand(TextToSpeech speaker) {
        super(speaker);
    }

    @Override
    protected void initWords() {
        runWords = new String[][]{{"hello"}, {"cześć"}};
    }

    @Override
    protected void onInput(String sentence, boolean firstRun) {
        if (firstRun)
            speak("witam");
    }

    @Override
    protected void cancel() {

    }


}
