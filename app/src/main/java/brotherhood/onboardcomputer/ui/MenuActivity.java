package brotherhood.onboardcomputer.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.engineController.EngineController;
import brotherhood.onboardcomputer.utils.Helper;
import brotherhood.onboardcomputer.views.dotsBackground.BackgroundView;
import brotherhood.onboardcomputer.views.recognizeButton.RecognizeButton;

@EActivity(R.layout.activity_menu)
public class MenuActivity extends Activity {
    @ViewById(R.id.centerMenuButton)
    View centerActionButton;

    @ViewById
    TextView commandText;

    @ViewById
    TextView title;

    @ViewById
    BackgroundView backgroundView;

    @ViewById
    RecognizeButton recognizeButton;

    private FloatingActionMenu actionMenu;
    private SubActionButton demoButton;
    private boolean animationThreadRun = true;

    @AfterViews
    void afterView() {
        createCircleMenu();
        initMenuAnimationThreads();
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
                .addSubActionView(createMenuButton(R.drawable.bot_icon, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBotButtonClick();
                    }
                }))
                .addSubActionView(demoButton)
                .addSubActionView(createMenuButton(R.drawable.info, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("info");
                    }
                }))
                .setRadius(Helper.dpToPx(this, 80))
                .attachTo(centerActionButton)
                .build();
    }

    private void onDemoButtonClick() {
        if(EngineController.DEMO){
            EngineController.DEMO = false;
            ((ImageView) demoButton.getTag()).setImageDrawable(getResources().getDrawable(R.drawable.demo_off));
        }else{
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
        if(EngineController.DEMO){
            startActivity(new Intent(getApplicationContext(), PidsListActivity_.class));
            return;
        }
        if (checkBluetooth()) {
            startActivity(new Intent(getApplicationContext(), DevicesListActivity_.class));
        }
    }

    private void onBotButtonClick() {

    }

    private boolean checkBluetooth() {
        if (!Helper.checkIsBluetoothEnabled()) {
            Helper.showInfoMsg(this, getString(R.string.enable_bluetooth_msg));
            return false;
        } else
            return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RecognizeButton.RECOGNIZE_RESPONSE && resultCode == RESULT_OK) {
            recognizeButton.onRecognize(data);
        }
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
        recognizeButton.destroy();
        super.onDestroy();
    }
}
