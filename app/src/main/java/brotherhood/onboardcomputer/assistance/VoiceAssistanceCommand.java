package brotherhood.onboardcomputer.assistance;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import brotherhood.onboardcomputer.assistance.commands.ConfirmVoiceCommand;

public abstract class VoiceAssistanceCommand {
    protected String[] runWords;
    protected String[] stopWords;
    protected TextToSpeech speaker;
    private String sentenceAfterRunWords = "";
    private Context context;
    private ConfirmVoiceCommand confirmCommand;

    public VoiceAssistanceCommand(TextToSpeech speaker) {
        this.speaker = speaker;
        initWords();
    }

    public void registerConfirmCommand(ConfirmVoiceCommand confirmCommand) {
        this.confirmCommand = confirmCommand;
    }

    public void process(String sentence) {
        if (confirmCommand != null) {
            confirmCommand.process(sentence);
            this.confirmCommand = null;
        }
        if (recognizeStartCommand(sentence)) {
            onCommandRecognized(sentence);
        } else if (recognizeStopCommand(sentence)) {
            onStopWordRecognized();
        }
    }

    protected abstract void onStopWordRecognized();

    protected boolean recognizeStartCommand(String sentence) {
        if (runWords != null)
            for (String wholeSentence : runWords) {
                if (wholeSentence.charAt(wholeSentence.length() - 1) != ' ') {
                    wholeSentence += ' ';
                }
                wholeSentence = wholeSentence.substring(0, wholeSentence.length() - 1);
                wholeSentence = wholeSentence.toLowerCase();
                sentence = sentence.toLowerCase();

                if (sentence.contains(wholeSentence) || sentence.equals(wholeSentence)) {
                    sentenceAfterRunWords = sentence.substring(sentence.indexOf(wholeSentence) + wholeSentence.length());
                    return true;
                }
            }
        return false;
    }

    public Context getContext() {
        return context;
    }

    public VoiceAssistanceCommand setContext(Context context) {
        this.context = context;
        return this;
    }

    protected String getSentenceAfterRunWords() {
        return sentenceAfterRunWords;
    }

    protected boolean recognizeStopCommand(String sentence) {
        if (stopWords != null)
            for (String wholeSentence : this.stopWords) {
                wholeSentence = wholeSentence.substring(0, wholeSentence.length() - 1);
                wholeSentence = wholeSentence.toLowerCase();
                sentence = sentence.toLowerCase();
                if (sentence.contains(wholeSentence) || sentence.equals(wholeSentence)) {
                    sentenceAfterRunWords = sentence.substring(sentence.indexOf(wholeSentence) + wholeSentence.length());
                    return true;
                }
            }
        return false;
    }

    protected abstract void initWords();

    protected abstract void onCommandRecognized(String sentence);

    public void speak(String textToSpeak) {
        if (textToSpeak != null) {
            speaker.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null);
        }
    }

    protected String[] connectArrays(String[]... arrays) {
        String[] result;
        int size = 0;
        for (String[] array : arrays) {
            size += array.length;
        }
        result = new String[size];

        int i = 0;
        for (String[] array : arrays) {
            for (String string : array) {
                result[i] = string;
                i++;
            }
        }
        return result;
    }

    protected String[] permuteArrayWithString(String[] array, String toPermute) {
        String[] result = new String[array.length];
        int i = 0;
        for (String string : array) {
            result[i] = string + " " + toPermute;
            i++;
        }
        return result;
    }

    protected String[] permuteArrays(String[] array1, String[] array2) {
        String[] result = new String[array1.length * array2.length];
        int i = 0;
        for (String string1 : array1) {
            for (String string2 : array2) {
                result[i] = string1 + " " + string2;
                i++;
            }
        }
        return result;
    }
}
