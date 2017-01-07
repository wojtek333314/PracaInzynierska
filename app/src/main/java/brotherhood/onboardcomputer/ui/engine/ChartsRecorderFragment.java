package brotherhood.onboardcomputer.ui.engine;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.engine.engineController.EngineController;
import brotherhood.onboardcomputer.ui.BaseFragment;

@EFragment(R.layout.charts_recorder_fragment)
public class ChartsRecorderFragment extends BaseFragment implements EngineController.EngineListener{

    @AfterViews
    void initViews() {

    }

    @Override
    public void onDataRefresh() {

    }
}
