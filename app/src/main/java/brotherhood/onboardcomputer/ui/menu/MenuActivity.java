package brotherhood.onboardcomputer.ui.menu;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.engine.engineController.EngineController;
import brotherhood.onboardcomputer.ui.BaseActivity;
import brotherhood.onboardcomputer.ui.devicesList.DevicesListActivity_;
import brotherhood.onboardcomputer.ui.engine.PidsListActivity_;
import brotherhood.onboardcomputer.ui.recording.RecordActivity_;
import brotherhood.onboardcomputer.ui.views.dotsBackground.BackgroundView;
import brotherhood.onboardcomputer.ui.views.recognizeButton.RecognitionSystem;
import brotherhood.onboardcomputer.utils.Helper;

@EActivity(R.layout.activity_menu)
public class MenuActivity extends BaseActivity {
    @ViewById(R.id.centerMenuButton)
    View centerActionButton;

    @ViewById
    TextView commandText;

    @ViewById
    TextView title;

    @ViewById
    BackgroundView backgroundView;

    private RecognitionSystem recognitionSystem;
    private FloatingActionMenu actionMenu;
    private SubActionButton demoButton;
    private BubblesManager bubblesManager;
    private boolean animationThreadRun = true;
    private RecognitionListener speechRecognitionListener;
    private Intent speechRecognizerIntent;
    private SpeechRecognizer speechRecognizer;
    private android.support.design.widget.FloatingActionButton floatingActionButton;

    @AfterViews
    void afterView() {
        initRecognizeListener();
        initRecognizeIntent();
        initSpeechRecognizer();
        initRecognitionSystem();
        createCircleMenu();
        initMenuAnimationThreads();
        createAppFab();
    }

    private void initSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(speechRecognitionListener);
    }

    private void initRecognitionSystem() {
        recognitionSystem = new RecognitionSystem(getApplicationContext());
    }

    private void initRecognizeIntent() {
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, RecognitionSystem.LOCALE_LANGUAGE);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, RecognitionSystem.LOCALE_LANGUAGE);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, RecognitionSystem.LOCALE_LANGUAGE);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "brotherhood.onboardcomputer.assistance.services");
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
    }


    private void createAppFab() {
        bubblesManager = new BubblesManager.Builder(this).setInitializationCallback(new OnInitializedCallback() {
            @Override
            public void onInitialized() {
                BubbleLayout bubbleView = (BubbleLayout) LayoutInflater.from(MenuActivity.this).inflate(R.layout.app_fab, null);
                bubbleView.setShouldStickToWall(true);
                floatingActionButton = (android.support.design.widget.FloatingActionButton) bubbleView.findViewById(R.id.fab);
                bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {
                    @Override
                    public void onBubbleClick(BubbleLayout bubble) {
                        speechRecognizer.startListening(speechRecognizerIntent);
                    }
                });
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int height = size.y;
                bubblesManager.addBubble(bubbleView, 0, (int) (height * 0.8f));
            }
        }).build();
        bubblesManager.initialize();

    }

    private void initMenuAnimationThreads() {
        YoYo.with(Techniques.Wobble)
                .duration(1700)
                .playOn(title);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (animationThreadRun) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!actionMenu.isOpen())
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                YoYo.with(Techniques.RubberBand)
                                        .duration(800)
                                        .playOn(centerActionButton);
                            }
                        });
                }
            }
        }).start();
    }

    private void createCircleMenu() {
        demoButton = createMenuButton(R.drawable.demo_off, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDemoButtonClick();
            }
        });

        actionMenu = new FloatingActionMenu.Builder(this)
                .setRadius(90)
                .setStartAngle(180)
                .setEndAngle(360)
                .addSubActionView(createMenuButton(R.drawable.engine_icon, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onEngineClick();
                    }
                }))
                .addSubActionView(createMenuButton(R.drawable.ic_videocam_black_24dp, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCameraButtonClick();
                    }
                }))
                .addSubActionView(demoButton)
                .setRadius(Helper.dpToPx(this, 80))
                .attachTo(centerActionButton)
                .build();
    }

    private void onDemoButtonClick() {
        if (EngineController.DEMO) {
            EngineController.DEMO = false;
            ((ImageView) demoButton.getTag()).setImageDrawable(getResources().getDrawable(R.drawable.demo_off));
        } else {
            EngineController.DEMO = true;
            ((ImageView) demoButton.getTag()).setImageDrawable(getResources().getDrawable(R.drawable.demo_on));
        }
    }

    private SubActionButton createMenuButton(int iconResource, View.OnClickListener onClickListener) {
        SubActionButton.Builder item = new SubActionButton.Builder(this);
        ImageView icon = new ImageView(this);
        icon.setImageDrawable(getResources().getDrawable(iconResource));
        SubActionButton button = item.setContentView(icon).build();
        button.setOnClickListener(onClickListener);
        button.setTag(icon);

        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
        layoutParams.width = (int) Helper.convertDpToPixel(this, Helper.dpToPx(this, 30));
        layoutParams.height = (int) Helper.convertDpToPixel(this, Helper.dpToPx(this, 30));
        button.setLayoutParams(layoutParams);

        return button;
    }

    private void onEngineClick() {
        if (EngineController.DEMO) {
            startActivity(new Intent(getApplicationContext(), PidsListActivity_.class));
            return;
        }
        if (checkBluetooth()) {
            startActivity(new Intent(getApplicationContext(), DevicesListActivity_.class));
        }
    }

    private void onCameraButtonClick() {
        startActivity(new Intent(this, RecordActivity_.class));
    }

    private boolean checkBluetooth() {
        if (!Helper.checkIsBluetoothEnabled()) {
            Helper.showInfoMsg(this, getString(R.string.enable_bluetooth_msg));
            return false;
        } else
            return true;
    }

    @Override
    protected void onPause() {
        animationThreadRun = false;
        backgroundView.destroy();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundView.resume();
    }

    @Override
    protected void onDestroy() {
        bubblesManager.recycle();
        recognitionSystem.destroy();
        super.onDestroy();
    }

    private void initRecognizeListener() {
        speechRecognitionListener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(getApplicationContext(), getString(R.string.all_listening), Toast.LENGTH_SHORT).show();
                floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccentRed)));
            }

            @Override
            public void onBeginningOfSpeech() {
                floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccentRed)));
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            }

            @Override
            public void onError(int error) {
                switch (error) {
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        Toast.makeText(getApplicationContext(), getString(R.string.all_recognition_no_match), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        recognitionSystem.getSpeaker().speak("Błąd połączenia", TextToSpeech.QUEUE_FLUSH, null);
                        Toast.makeText(getApplicationContext(), getString(R.string.all_connection_error), Toast.LENGTH_SHORT).show();
                        break;
                }
                floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

            }

            @Override
            public void onResults(Bundle results) {
                recognitionSystem.onRecognize(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
                floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        };
    }
}
