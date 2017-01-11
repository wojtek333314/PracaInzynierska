package brotherhood.onboardcomputer.ui.engine;

import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import brotherhood.onboardcomputer.R;
import brotherhood.onboardcomputer.engine.ecuCommands.EngineCommand;
import brotherhood.onboardcomputer.engine.engineController.EngineController;
import brotherhood.onboardcomputer.utils.cardsBuilder.models.RecordPidModel;
import brotherhood.onboardcomputer.utils.cardsBuilder.models.RecordPidValue;
import brotherhood.onboardcomputer.utils.cardsBuilder.models.SelectablePidCardModel;
import brotherhood.onboardcomputer.ui.BaseFragment;
import brotherhood.onboardcomputer.utils.cardsBuilder.adapters.CardsRecyclerViewAdapter;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.CardModel;
import brotherhood.onboardcomputer.utils.cardsBuilder.views.SelectablePidCard;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

@EFragment(R.layout.charts_recorder_fragment)
public class ChartsRecorderFragment extends BaseFragment implements EngineController.CommandListener {

    @ViewById
    RecyclerView recyclerView;

    @ViewById
    Button recordingButton;

    @ViewById
    TextView info;


    private ArrayList<CardModel> pids;
    private CardsRecyclerViewAdapter adapter;
    private EngineController engineController;
    private boolean recording = false;
    private HashMap<SelectablePidCard, RecordPidModel> recordingPidsModels;
    private String fileLocalization = "testFile.xls";

    @AfterViews
    void initViews() {
        hideInfo();
        initRecyclerView();
        initButton();
    }

    private void initRecyclerView() {
        if (pids != null) {
            recreateRecyclerView();
            return;
        }
        pids = new ArrayList<>();
        for (EngineCommand availableCommand : engineController.getEngineCommandsController().getOnlyAvailableEngineCommands()) {
            pids.add(new SelectablePidCard(getActivity(), new SelectablePidCardModel(availableCommand, false)));
        }
        recreateRecyclerView();
    }

    private void recreateRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CardsRecyclerViewAdapter(getActivity(), pids);
        recyclerView.setAdapter(adapter);
        refreshList();
    }

    @UiThread
    void refreshList() {
        adapter.notifyDataSetChanged();
    }

    private void initButton() {
        recordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording) {
                    onStopRecording();
                    recording = false;
                    recordingButton.setText(getString(R.string.all_start_recording));
                    hideInfo();
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    onStartRecording();
                    recordingButton.setText(getString(R.string.all_stop_recording));
                }
            }
        });
    }

    private void onStartRecording() {
        recordingPidsModels = new HashMap<>();
        for (CardModel cardModel : pids) {
            SelectablePidCard card = ((SelectablePidCard) cardModel);
            if (card.getData().isChecked()) {
                recordingPidsModels.put(card, new RecordPidModel());
            }
        }
        recyclerView.setVisibility(View.GONE);
        fileLocalization = DateFormat.format("yyyy-MM-dd_kk-mm-ss", new Date().getTime()).toString() + ".xls";
        showInfo();
        recording = true;
    }

    private void showInfo() {
        info.setText(String.format("%s %s", getString(R.string.data_is_recording), fileLocalization));
        info.setVisibility(View.VISIBLE);
    }

    private void hideInfo() {
        info.setVisibility(View.GONE);
    }

    @Override
    public void onDataRefresh() {
        if (recording) {
            storeData();
        }
    }

    private void onStopRecording() {
        System.out.println("stop recording");
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveFile();
            }
        }).start();

        showDialog(String.format(getString(R.string.recording_stop_message), fileLocalization), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


    }

    private void saveFile() {
        final String fileName = fileLocalization;

        //Saving file in external storage
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/CarInterface/ChartsRecorder");

        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }

        //file path
        File file = new File(directory, fileName);

        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook;

        try {
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("MyShoppingList", 0);

            try {
                sheet.addCell(new Label(0, 0, "Time"));
                sheet.addCell(new Label(1, 0, "PID Name"));
                sheet.addCell(new Label(2, 0, "Value"));

                int row = 1;

                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

                for (SelectablePidCard card : recordingPidsModels.keySet()) {
                    sheet.addCell(new Label(1, row, String.valueOf(card.getData().getEngineCommand().getDescription())));
                    System.out.println(card.getData().getEngineCommand().getDescription() + " recorded data:");
                    for (RecordPidValue value : recordingPidsModels.get(card).getValues()) {
                        sheet.addCell(new Label(0, row, dateFormat.format(new Date(value.getTime()))));
                        sheet.addCell(new Label(2, row, String.valueOf(value.getValue())));
                        row++;
                    }
                }

            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("end");
    }


    private void storeData() {
        //get all new values from all checked pids and after stop save them to file!
        long time = System.currentTimeMillis();
        for (SelectablePidCard card : recordingPidsModels.keySet()) {
            recordingPidsModels.get(card).addData(new RecordPidValue(card.getData().getEngineCommand().getLastValue(), time));
        }
    }

    @Override
    public void onNoData(EngineCommand engineCommand) {

    }

    public ChartsRecorderFragment setEngineController(EngineController engineController) {
        this.engineController = engineController;
        return this;
    }

    public boolean isRecording() {
        return recording;
    }
}
