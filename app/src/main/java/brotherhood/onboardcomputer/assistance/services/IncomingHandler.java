package brotherhood.onboardcomputer.assistance.services;

import android.os.Handler;
import android.os.Message;
import android.speech.SpeechRecognizer;

import java.lang.ref.WeakReference;

/**
 * Created by Wojtas on 2016-08-21.
 */
public class IncomingHandler extends Handler {
    private WeakReference<SpeechToTextService> target;

    IncomingHandler(SpeechToTextService target) {
        this.target = new WeakReference<>(target);
    }


    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SpeechToTextService.MSG_RECOGNIZER_START_LISTENING:
                startListening();
                break;

            case SpeechToTextService.MSG_RECOGNIZER_CANCEL:
                cancelListening();
                break;

            case SpeechToTextService.MSG_RECOGNIZER_RESET:
                resetListening();
                break;
            case SpeechToTextService.MSG_RECOGNIZER_HARD_RESET:
                hardResetListening();
                break;
        }
    }

    private void startListening() {
        final SpeechToTextService target = this.target.get();
        target.speechRecognizer.cancel();
        if (!target.isListening) {
            target.speechRecognizer.startListening(target.speechRecognizerIntent);
            target.isListening = true;
        }
    }

    private void resetListening() {
        System.out.println("IncomingHandler Reset");
        final SpeechToTextService target = this.target.get();
        target.speechRecognizer.cancel();
        target.isListening = false;

        Message message = Message.obtain(null, SpeechToTextService.MSG_RECOGNIZER_START_LISTENING);
        handleMessage(message);
    }

    private void cancelListening() {
        final SpeechToTextService target = this.target.get();
        target.speechRecognizer.cancel();
        target.isListening = false;
    }

    private void hardResetListening(){
        System.out.println("IncomingHandler HARD Reset");
        final SpeechToTextService target = this.target.get();
        target.isListening = false;
        target.speechRecognizer.destroy();
        target.speechRecognizer.cancel();
        target.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(target);
        target.speechRecognizer.setRecognitionListener(target.speechRecognitionListener);

        if (!target.isListening) {
            target.speechRecognizer.startListening(target.speechRecognizerIntent);
            target.isListening = true;
        }
    }


}
