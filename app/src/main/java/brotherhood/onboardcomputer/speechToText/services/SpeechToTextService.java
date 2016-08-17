package brotherhood.onboardcomputer.speechToText.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Wojtas on 2016-08-17.
 */
public class SpeechToTextService extends Service {
    protected AudioManager audioManager;
    protected SpeechRecognizer speechRecognizer;
    protected Intent speechRecognizerIntent;
    protected final Messenger serverMessenger = new Messenger(new IncomingHandler(this));

    protected boolean isListening;
    protected volatile boolean isCountDownOn;

    public static final int MSG_RECOGNIZER_START_LISTENING = 1;
    public static final int MSG_RECOGNIZER_CANCEL = 2;
    public static final int MSG_RECOGNIZER_RESET = 3;

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 500);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 500);
    }

    protected static class IncomingHandler extends Handler {
        private WeakReference<SpeechToTextService> target;

        IncomingHandler(SpeechToTextService target) {
            this.target = new WeakReference<>(target);
        }


        @Override
        public void handleMessage(Message msg) {
            final SpeechToTextService target = this.target.get();
            switch (msg.what) {
                case MSG_RECOGNIZER_START_LISTENING:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        target.audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM
                                ,AudioManager.ADJUST_LOWER
                                ,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE); //turn off beep sound

                    if (!target.isListening) {
                        target.speechRecognizer.startListening(target.speechRecognizerIntent);
                        target.isListening = true;
                    }
                    break;

                case MSG_RECOGNIZER_CANCEL:
                    target.speechRecognizer.cancel();
                    target.isListening = false;
                    break;

                case MSG_RECOGNIZER_RESET:
                    target.speechRecognizer.cancel();
                    target.isListening = false;

                    Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                    handleMessage(message);
                    break;
            }
        }
    }

    protected CountDownTimer mNoSpeechCountDown = new CountDownTimer(5000, 5000) {

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            isCountDownOn = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_CANCEL);
            try {
                serverMessenger.send(message);
                message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                serverMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isCountDownOn) {
            mNoSpeechCountDown.cancel();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    protected class SpeechRecognitionListener implements RecognitionListener {

        private static final String TAG = "SpeechRecognitionLstner";

        @Override
        public void onBeginningOfSpeech() {
            // speech input will be processed, so there is no need for count down anymore
            if (isCountDownOn) {
                isCountDownOn = false;
                mNoSpeechCountDown.cancel();
            }
            Log.d(TAG, "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int error) {
            if (isCountDownOn) {
                isCountDownOn = false;
                mNoSpeechCountDown.cancel();
            }
            isListening = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
            try {
                serverMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }

        @Override
        public void onPartialResults(Bundle results) {
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (data != null)
                for (int i = 0; i < data.size(); i++) {
                    System.out.println(data.get(i));
                    if(data.get(i).toString().toLowerCase().contains("abecadło"))
                    {
                        System.out.println("RESET!");
                        isListening = false;
                        Message message = Message.obtain(null, MSG_RECOGNIZER_RESET);
                        try {
                            serverMessenger.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                isCountDownOn = true;
                mNoSpeechCountDown.start();
                audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            }
            Log.d(TAG, "onReadyForSpeech");
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (data != null)
                for (int i = 0; i < data.size(); i++) {
                    System.out.println(data.get(i));
                }

            isListening = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
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

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("binded!");
        return serverMessenger.getBinder();
    }
}