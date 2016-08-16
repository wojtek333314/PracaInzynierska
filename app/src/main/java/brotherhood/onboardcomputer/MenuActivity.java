package brotherhood.onboardcomputer;

import android.support.v7.app.AppCompatActivity;
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
import brotherhood.onboardcomputer.utils.MetricUtil;

@EActivity(R.layout.activity_menu)
public class MenuActivity extends AppCompatActivity {

    @ViewById(R.id.centerMenuButton)
    View centerActionButton;

    @ViewById
    BackgroundView backgroundView;

    @ViewById
    TextView title;

    private FloatingActionMenu actionMenu;
    private boolean animationThreadRun = true;

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
