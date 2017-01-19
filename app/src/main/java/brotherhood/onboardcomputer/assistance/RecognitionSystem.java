package brotherhood.onboardcomputer.assistance;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

import brotherhood.onboardcomputer.assistance.commands.CallVoiceCommand;
import brotherhood.onboardcomputer.assistance.commands.NavigationVoiceCommand;
import brotherhood.onboardcomputer.assistance.commands.PositionInformVoiceCommand;
import brotherhood.onboardcomputer.assistance.commands.ReplySmsVoiceCommand;
import brotherhood.onboardcomputer.assistance.commands.SmsVoiceCommand;

public class RecognitionSystem {
    public static final String LOCALE_LANGUAGE = "pl";

    private ArrayList<VoiceAssistanceCommand> voiceAssistanceCommands = new ArrayList<>();
    private TextToSpeech speaker;
    private Context context;

    public RecognitionSystem(Context context) {
        this.context = context;
        createSpeaker();
        initCommands();
    }

    private void initCommands() {
        voiceAssistanceCommands.add(new PositionInformVoiceCommand(speaker).setContext(getContext()));
        voiceAssistanceCommands.add(new NavigationVoiceCommand(speaker).setContext(getContext()));
        voiceAssistanceCommands.add(new SmsVoiceCommand(speaker).setContext(getContext()));
        voiceAssistanceCommands.add(new CallVoiceCommand(speaker).setContext(getContext()));
        voiceAssistanceCommands.add(new ReplySmsVoiceCommand(speaker).setContext(getContext()));
    }

    public Context getContext() {
        return context;
    }

    public void onRecognize(ArrayList<String> data) {
        if (data.size() > 0) {
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
