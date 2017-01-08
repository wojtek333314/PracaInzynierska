package brotherhood.onboardcomputer.assistance.commands;

import android.content.Intent;
import android.net.Uri;
import android.speech.tts.TextToSpeech;

import brotherhood.onboardcomputer.assistance.Command;
import brotherhood.onboardcomputer.assistance.util.Words;

public class NavigationCommand extends Command {
    private static final String[] START_NAVIGATION
            = new String[]{"nawiguj do", "nawigacja do", "nawigacja", "nawiguj do", "prowadź do"};


    public NavigationCommand(TextToSpeech speaker) {
        super(speaker);
    }

    @Override
    protected void onStopWordRecognized() {

    }

    @Override
    protected void initWords() {
        runWords = connectArrays(getPermutedArray(Words.START_COMMANDS, "nawigację"), START_NAVIGATION);
    }

    @Override
    protected void onCommandRecognized(String sentence) {
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

}
