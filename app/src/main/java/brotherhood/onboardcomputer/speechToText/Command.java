package brotherhood.onboardcomputer.speechToText;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Random;

import brotherhood.onboardcomputer.speechToText.services.SpeechToTextService;

/**
 * Created by Wojtas on 2016-07-05.
 */
public abstract class Command {
    protected static Random random = new Random();
    protected String[][] runWords;
    protected String[][] stopWords;
    protected boolean isRunning;
    protected TextToSpeech speaker;
    private String wordAfterRunWords = "";
    private Context context;

    public Command(SpeechToTextService speechToTextService) {
        this.speaker = speechToTextService.getSpeaker();
        initWords();
    }

    public Command(TextToSpeech speaker) {
        this.speaker = speaker;
        initWords();
    }

    public void process(String sentence) {
        if (!isRunning) {
            if (recognizeStartCommand(sentence)) {
                onInput(sentence, true);
                isRunning = true;
            }
        } else {
            if (recognizeStopCommand(sentence)) {
                cancel();
                isRunning = false;
            }
            recognizeStartCommand(sentence); //refresh wordAfterRunWords
            onInput(sentence, false);
        }
    }

    protected boolean recognizeStartCommand(String sentence) {
        if (runWords != null)
            for (String[] runWord : runWords) {
                String wholeSentence = "";
                for (String str1 : runWord)
                    wholeSentence += str1 + " ";
                wholeSentence = wholeSentence.substring(0, wholeSentence.length() - 1);
                wholeSentence = wholeSentence.toLowerCase();
                sentence = sentence.toLowerCase();
                System.out.println("Command:    `"
                        + sentence
                        + "` contains `"
                        + wholeSentence + "` ? "
                        + (sentence.contains(wholeSentence) || sentence.equals(wholeSentence)));

                if (sentence.contains(wholeSentence) || sentence.equals(wholeSentence)) {
                    wordAfterRunWords = sentence.substring(sentence.indexOf(wholeSentence) + wholeSentence.length());
                    return true;
                }
            }
        return false;
    }

    public Context getContext() {
        return context;
    }

    public Command setContext(Context context) {
        this.context = context;
        return this;
    }

    protected String getSentenceAfterRunWords() {
        return wordAfterRunWords;
    }

    protected boolean recognizeStopCommand(String sentence) {
        if (stopWords != null)
            for (String[] stopWord : this.stopWords) {
                String wholeSentence = "";
                for (String str1 : stopWord)
                    wholeSentence += str1 + " ";
                wholeSentence = wholeSentence.substring(0, wholeSentence.length() - 1);
                wholeSentence = wholeSentence.toLowerCase();
                sentence = sentence.toLowerCase();
                if (sentence.contains(wholeSentence) || sentence.equals(wholeSentence)) {
                    wordAfterRunWords = sentence.substring(sentence.indexOf(wholeSentence) + wholeSentence.length());
                    return true;
                }
            }
        return false;
    }

    protected abstract void initWords();

    protected abstract void onInput(String sentence, boolean firstRun);

    protected abstract void cancel();

    protected void speak(String textToSpeak) {
        if (textToSpeak != null)
            speaker.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    protected void speak(String textToSpeak, final SpeakListener speakListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean working = true;
                boolean isSpeaking = false;
                while (working) {
                    if (!isSpeaking && speaker.isSpeaking())
                        isSpeaking = speaker.isSpeaking();
                    else if (isSpeaking && !speaker.isSpeaking()) {
                        isSpeaking = false;
                        working = false;
                        speakListener.onFinish();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        speak(textToSpeak);

    }

    public interface SpeakListener {
        void onFinish();
    }
}
