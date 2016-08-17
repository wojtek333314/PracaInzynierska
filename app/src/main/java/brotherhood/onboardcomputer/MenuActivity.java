package brotherhood.onboardcomputer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
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

import brotherhood.onboardcomputer.background.BackgroundView;
import brotherhood.onboardcomputer.speechToText.services.SpeechToTextService;
import brotherhood.onboardcomputer.utils.MetricUtil;

@EActivity(R.layout.activity_menu)
public class MenuActivity extends FragmentActivity {

    @ViewById(R.id.centerMenuButton)
    View centerActionButton;

    @ViewById
    BackgroundView backgroundView;

    @ViewById
    TextView title;

    private FloatingActionMenu actionMenu;
    private boolean animationThreadRun = true;
    private int mBindFlag;
    private Messenger mServiceMessenger;

    private final ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            System.out.println("service connected!");
            mServiceMessenger = new Messenger(service);
            Message msg = new Message();
            msg.what = SpeechToTextService.MSG_RECOGNIZER_START_LISTENING;

            try
            {
                mServiceMessenger.send(msg);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            System.out.println("service DISconnected!");
             mServiceMessenger = null;
        }

    }; // mServiceConnection



    @AfterViews
    void afterView() {
        createCircleMenu();
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

        Intent service = new Intent(getBaseContext(), SpeechToTextService.class);
        startService(service);
        mBindFlag = Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH ? 0 : Context.BIND_ABOVE_CLIENT;

        bindService(new Intent(this, SpeechToTextService.class), mServiceConnection, mBindFlag);
    }




    @Override
    protected void onStop()
    {
        super.onStop();

        if (mServiceMessenger != null)
        {
            unbindService(mServiceConnection);
            mServiceMessenger = null;
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

    private void createCircleMenu() {

        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(getResources().getDrawable(R.drawable.menu_icon));

        SubActionButton.Builder botItem = new SubActionButton.Builder(this);
        ImageView botIcon = new ImageView(this);
        botIcon.setImageDrawable(getResources().getDrawable(R.drawable.bot_icon));
        SubActionButton botButton = botItem.setContentView(botIcon).build();

        ViewGroup.LayoutParams layoutParams = botButton.getLayoutParams();
        layoutParams.width = (int) MetricUtil.convertDpToPixel(this, 60);
        layoutParams.height = (int) MetricUtil.convertDpToPixel(this, 60);

        SubActionButton.Builder engineItem = new SubActionButton.Builder(this);
        ImageView engineIcon = new ImageView(this);
        engineIcon.setImageDrawable(getResources().getDrawable(R.drawable.engine_icon));
        SubActionButton engineButton = engineItem.setContentView(engineIcon).build();

        SubActionButton.Builder infoItem = new SubActionButton.Builder(this);
        ImageView infoIcon = new ImageView(this);
        infoIcon.setImageDrawable(getResources().getDrawable(R.drawable.info));
        SubActionButton infoButton = infoItem.setContentView(infoIcon).build();

        engineButton.setLayoutParams(layoutParams);
        botButton.setLayoutParams(layoutParams);
        infoButton.setLayoutParams(layoutParams);

        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(engineButton)
                .addSubActionView(botButton)
                .addSubActionView(infoButton)
                .attachTo(centerActionButton)
                .build();
    }
}
