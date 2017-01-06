package brotherhood.onboardcomputer.assistance.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

import brotherhood.onboardcomputer.ui.menu.MenuWithVoiceRecognitionActivity;
import brotherhood.onboardcomputer.assistance.Command;
import brotherhood.onboardcomputer.assistance.enums.BroadcastMessageType;
import brotherhood.onboardcomputer.assistance.enums.SpeechToTextServiceStatus;

public class SpeechToTextService extends Service {
    public static final String LOCALE_LANGUAGE = "en";
    public static final int MSG_RECOGNIZER_START_LISTENING = 1;
    public static final int MSG_RECOGNIZER_CANCEL = 2;
    public static final int MSG_RECOGNIZER_RESET = 3;
    public static final int MSG_RECOGNIZER_HARD_RESET = 4;

    private static final int PAUSE_AFTER_LAST_WORD = 800;
    private final static String FIRST_HOT_KEYWORD = "okay";
    private final static String SECOND_HOT_KEYWORD = "interface";

    protected AudioManager audioManager;
    protected SpeechRecognizer speechRecognizer;
    protected Intent speechRecognizerIntent;
    protected Messenger serverMessenger = new Messenger(new IncomingHandler(this));
    protected SpeechRecognitionListener speechRecognitionListener = new SpeechRecognitionListener();
    private TextToSpeech speaker;
    private String recognizedText = null;
    private ArrayList<Command> commands = new ArrayList<>();
    protected volatile boolean muteAfterEndOfSpeaking = false;
    protected volatile boolean isListening;
    protected volatile boolean isSpeaking = false;
    protected volatile boolean pauseAfterLastWordThread = true;
    protected volatile boolean wordRecognized = false;
    protected int streamOriginalVolume;
    protected float stuckTimeAccumulator; // sometimes service stuck onReadyOfSpeach
    private boolean isServiceRunning = true;
    private boolean isTtsConnected = false;
    private boolean isReadyForSpeechStuck;

    @Override
    public void onCreate() {
        super.onCreate();
        createSpeaker();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        streamOriginalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // getting system volume into var for later un-muting
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(speechRecognitionListener);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, LOCALE_LANGUAGE);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, LOCALE_LANGUAGE);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, LOCALE_LANGUAGE);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "brotherhood.onboardcomputer.assistance.services");
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        new Thread(pauseAfterLastWordCounter).start();
        new Thread(isSpeakingListener).start();

        initCommands();
    }

    public TextToSpeech getSpeaker() {
        return speaker;
    }

    private void createSpeaker() {
        speaker = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    int result = speaker.setLanguage(new Locale("pl"));

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    } else
                        isTtsConnected = true;
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });
        speaker.setSpeechRate(.8f);
    }


    private void initCommands() {
    //    commands.add(new HelloCommand(this));
    }

    public void speak(String textToSpeak) {
        speaker.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void recognizeCommand(String sentence) {
        for (Command command : commands) {
            command.process(sentence);
        }
    }


    protected class SpeechRecognitionListener implements RecognitionListener {

        private static final String TAG = "SpeechRecognitionLstner";

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginingOfSpeech");
            isReadyForSpeechStuck = true;
            sendBroadcastMessage(BroadcastMessageType.STATUS, SpeechToTextServiceStatus.START_RECOGNIZING.name());
        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech");
            sendBroadcastMessage(BroadcastMessageType.STATUS, SpeechToTextServiceStatus.STOP_RECOGNIZING.name());
        }

        @Override
        public void onError(int error) {
            isListening = false;
            System.out.println("ERROR:" + error);
            Message message = Message.obtain(null, MSG_RECOGNIZER_HARD_RESET);
            try {
                serverMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            sendBroadcastMessage(BroadcastMessageType.STATUS, SpeechToTextServiceStatus.ERROR.name() + ":" + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }

        @Override
        public void onPartialResults(Bundle results) {
            unmute();
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String partiallyResult = (data != null && data.size() > 0) ? data.get(0) : null;

            if (partiallyResult != null && data.size() > 0) {
                if (!isSpeaking)
                    System.out.println(data.get(0));
                String[] array = partiallyResult.split("\\s+");
                String wordsAfterHotword = "";
                boolean hotwordsUsed = false;
                for (int i = 0; i < array.length - 1; i++) {
                    if (array[i].equals(FIRST_HOT_KEYWORD) && array[i + 1].equals(SECOND_HOT_KEYWORD)) {
                        if (i + 2 < array.length) {
                            for (int j = i + 2; j < array.length; j++) {
                                wordsAfterHotword += array[j] + " ";
                            }
                        }

                        if (!wordsAfterHotword.equals(""))
                            SpeechToTextService.this.recognizedText = wordsAfterHotword;

                        hotwordsUsed = true;
                    }
                }
                if (!hotwordsUsed && array.length > 40 && !array[array.length - 1].equals(FIRST_HOT_KEYWORD)) {
                    isListening = false;
                    Message message = Message.obtain(null, MSG_RECOGNIZER_RESET);
                    try {
                        serverMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            wordRecognized = true;
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
            isReadyForSpeechStuck = false;
            mute();
        }

        @Override
        public void onResults(Bundle results) {
            unmute();
            Log.d(TAG, "onResults");
            isListening = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_RESET);
            try {
                serverMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

    }


    private void sendBroadcastMessage(BroadcastMessageType type, String msg) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MenuWithVoiceRecognitionActivity.STRING_ACTION);
        broadcastIntent.putExtra(type.name(), msg);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {
        if (speaker != null) {
            speaker.stop();
            speaker.shutdown();
        }

        super.onDestroy();

        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("binded!");
        return serverMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        pauseAfterLastWordThread = false;
        isServiceRunning = false;
        return super.onUnbind(intent);
    }

    protected volatile Runnable pauseAfterLastWordCounter = new Runnable() {
        @Override
        public void run() {
            while (pauseAfterLastWordThread) {
                try {
                    Thread.sleep(PAUSE_AFTER_LAST_WORD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!wordRecognized && recognizedText != null) {
                    recognizeCommand(recognizedText);
                    sendBroadcastMessage(BroadcastMessageType.RECOGNIZED_TEXT, recognizedText);
                    isListening = false;
                    Message message = Message.obtain(null, MSG_RECOGNIZER_RESET);
                    try {
                        serverMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    wordRecognized = false;
                    recognizedText = null;
                } else if (!wordRecognized && recognizedText == null && isReadyForSpeechStuck) {
                    stuckTimeAccumulator += PAUSE_AFTER_LAST_WORD;
                    if (stuckTimeAccumulator > 5000) {
                        System.out.println("System was stucked! Reset!");
                        stuckTimeAccumulator = 0;
                        isListening = false;
                        Message message = Message.obtain(null, MSG_RECOGNIZER_HARD_RESET);
                        try {
                            serverMessenger.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else
                    wordRecognized = false;//reset
            }
        }
    };

    protected volatile Runnable isSpeakingListener = new Runnable() {
        @Override
        public void run() {
            while (isServiceRunning) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!isTtsConnected) {
                    muteAfterEndOfSpeaking = false;
                    continue;
                }

                // if(isSpeaking && !speaker.isSpeaking() && muteAfterEndOfSpeaking)
                //     audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);//todo
                isSpeaking = speaker.isSpeaking();
            }
        }
    };

    protected void mute() {
        if (isSpeaking)
            muteAfterEndOfSpeaking = true;
        else
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    }

    protected void unmute() {
        if (isSpeaking)
            muteAfterEndOfSpeaking = true;
        else
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamOriginalVolume, 0);
    }
}