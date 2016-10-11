package brotherhood.onboardcomputer.views.recognizeButton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Locale;

import brotherhood.onboardcomputer.speechToText.Command;
import brotherhood.onboardcomputer.speechToText.commands.CloseAppCommand;
import brotherhood.onboardcomputer.speechToText.commands.HelloCommand;
import brotherhood.onboardcomputer.speechToText.commands.NavigationCommand;
import brotherhood.onboardcomputer.speechToText.commands.RepeatByMeCommand;
import brotherhood.onboardcomputer.speechToText.commands.SearchDataCommand;

public class RecognizeButton extends Button {
    public static final String LOCALE_LANGUAGE = "pl";
    public static int RECOGNIZE_RESPONSE = 141;

    private ArrayList<Command> commands = new ArrayList<>();
    private TextToSpeech speaker;

    public RecognizeButton(Context context) {
        super(context);
        init();
    }

    public RecognizeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setClickListener();
        createSpeaker();
        initCommands();
        setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    public RecognizeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void initCommands() {
        commands.add(new HelloCommand(speaker));
        commands.add(new SearchDataCommand(speaker));
        commands.add(new CloseAppCommand(speaker).setContext(getContext()));
        commands.add(new RepeatByMeCommand(speaker));
        commands.add(new NavigationCommand(speaker).setContext(getContext()));
    }

    private void setClickListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                speaker.stop();
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 500);
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 800);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, LOCALE_LANGUAGE);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, LOCALE_LANGUAGE);
                intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, LOCALE_LANGUAGE);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        "Say a command");
                ((Activity) getContext()).startActivityForResult(intent, RECOGNIZE_RESPONSE);
            }
        });
    }

    public void onRecognize(Intent bundle) {
        ArrayList<String> data = bundle.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        if (data.size() > 0) {
            System.out.println(data.get(0));
            for (Command command : commands)
                command.process(data.get(0));
        }
    }

    private void createSpeaker() {
        speaker = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = speaker.setLanguage(new Locale(LOCALE_LANGUAGE));
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });
    }

    public void destroy() {
        if (speaker != null) {
            speaker.stop();
            speaker.shutdown();
        }
    }
}
