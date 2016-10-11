package brotherhood.onboardcomputer.speechToText.commands;

import android.content.Intent;
import android.net.Uri;
import android.speech.tts.TextToSpeech;

import brotherhood.onboardcomputer.speechToText.Command;
import brotherhood.onboardcomputer.speechToText.util.Words;

public class NavigationCommand extends Command {
    private static final String[] START_NAVIGATION
            = new String[]{"nawiguj do", "nawigacja do", "nawigacja", "nawiguj do", "prowadź do"};


    public NavigationCommand(TextToSpeech speaker) {
        super(speaker);
    }

    @Override
    protected void initWords() {
        runWords = connectArrays(getPermutedArray(Words.START_COMMANDS, "nawigację"), START_NAVIGATION);
    }

    @Override
    protected void onInput(String sentence, boolean firstRun) {
        System.out.println("ss:" + getSentenceAfterRunWords());
        if (getSentenceAfterRunWords() != null || !getSentenceAfterRunWords().equals("")) {
            speak("Uruchamiam nawigację do " + getSentenceAfterRunWords());
        } else {
            speak("Uruchamiam nawigację");
        }
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + getSentenceAfterRunWords()));
        intent.setPackage("com.google.android.apps.maps");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    @Override
    protected void cancel() {

    }
}
