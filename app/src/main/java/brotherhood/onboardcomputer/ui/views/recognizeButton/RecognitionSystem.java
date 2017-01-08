package brotherhood.onboardcomputer.ui.views.recognizeButton;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

import brotherhood.onboardcomputer.assistance.VoiceAssistanceCommand;
import brotherhood.onboardcomputer.assistance.commands.CallVoiceAssistanceCommand;
import brotherhood.onboardcomputer.assistance.commands.NavigationVoiceAssistanceCommand;
import brotherhood.onboardcomputer.assistance.commands.PositionInformVoiceAssistanceCommand;
import brotherhood.onboardcomputer.assistance.commands.ReplySmsVoiceAssistanceCommand;
import brotherhood.onboardcomputer.assistance.commands.SmsVoiceAssistanceCommand;

public class RecognitionSystem {
    public static final String LOCALE_LANGUAGE = "pl";
    public static final String STRING_ACTION = "stringAction";

    private ArrayList<VoiceAssistanceCommand> voiceAssistanceCommands = new ArrayList<>();
    private TextToSpeech speaker;
    private Context context;

    public RecognitionSystem(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        createSpeaker();
        initCommands();
    }

    private void initCommands() {
        voiceAssistanceCommands.add(new PositionInformVoiceAssistanceCommand(speaker).setContext(getContext()));
        voiceAssistanceCommands.add(new NavigationVoiceAssistanceCommand(speaker).setContext(getContext()));
        voiceAssistanceCommands.add(new SmsVoiceAssistanceCommand(speaker).setContext(getContext()));
        voiceAssistanceCommands.add(new CallVoiceAssistanceCommand(speaker).setContext(getContext()));
        voiceAssistanceCommands.add(new ReplySmsVoiceAssistanceCommand(speaker).setContext(getContext()));
    }

    public Context getContext() {
        return context;
    }

    public void onRecognize(ArrayList<String> data) {
        if (data.size() > 0) {
            System.out.println(data.get(0));
            for (VoiceAssistanceCommand voiceAssistanceCommand : voiceAssistanceCommands) {
                voiceAssistanceCommand.process(data.get(0));
            }
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

    public TextToSpeech getSpeaker() {
        return speaker;
    }

    public void destroy() {
        if (speaker != null) {
            speaker.stop();
            speaker.shutdown();
        }
    }
}
