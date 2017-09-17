package sytchie.daytasks;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private final String[] taskList = {"Morning Sport", "Sport", "Stepper", "Walk", "Read"};

    private String date = null, dayStartTime = null, dayEndTime = null;
    private CheckBox[] checkBoxes = new CheckBox[5];
    private Map<String, Boolean> tasks = new HashMap<>();
    private EditText[] editTexts = new EditText[3];
    private Map<String, String> posThings = new HashMap<>();
    private boolean enableSubmit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDay();
        if (dayStartTime == null) {
            setContentView(R.layout.layout_day_start);
        } else {
            setContentView(R.layout.layout_tasks);
            loadCheckBoxes();
            String time = "Day started at " + dayStartTime;
            TextView textView = (TextView) findViewById(R.id.text_day_start);
            textView.setText(time);
            startTaskListeners();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDay();
    }

    private void loadDay() {
        date = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(new Date());
        SharedPreferences sharedPreferences = getSharedPreferences("day_" + date.replaceAll("\\.", "-"), MODE_PRIVATE);
        //sharedPreferences.edit().clear().apply(); //Debug
        dayStartTime = sharedPreferences.getString("day_start_time", null);
        dayEndTime = sharedPreferences.getString("day_end_time", null);
        for (String task : taskList) {
            tasks.put(task, Boolean.valueOf(sharedPreferences.getString(task, null)));
        }
        for (int i = 1; i <= editTexts.length; i++) {
            String posThing = "pos_thing_" + i;
            String posString = sharedPreferences.getString(posThing, null);
            if (posString == null) {
                posString = "Positive thing " + i;
            }
            posThings.put(posThing, posString);
        }
    }

    private void loadCheckBoxes() {
        checkBoxes[0] = (CheckBox) findViewById(R.id.checkBox);
        checkBoxes[1] = (CheckBox) findViewById(R.id.checkBox2);
        checkBoxes[2] = (CheckBox) findViewById(R.id.checkBox3);
        checkBoxes[3] = (CheckBox) findViewById(R.id.checkBox4);
        checkBoxes[4] = (CheckBox) findViewById(R.id.checkBox5);
        int i = 0;
        for (String task : tasks.keySet()) {
            boolean tmp = tasks.get(task);
            checkBoxes[i++].setChecked(tmp);
        }
    }

    private void loadEditTexts() {
        editTexts[0] = (EditText) findViewById(R.id.editText);
        editTexts[1] = (EditText) findViewById(R.id.editText2);
        editTexts[2] = (EditText) findViewById(R.id.editText3);
        for (String posThing : posThings.keySet()) {
            editTexts[Integer.parseInt(posThing.substring(posThing.length() - 1)) - 1].setText(posThings.get(posThing));
        }
        enableSubmit = !(posThings.containsValue(null) ||
                posThings.containsValue("") ||
                posThings.containsValue("Positive thing 1") ||
                posThings.containsValue("Positive thing 2") ||
                posThings.containsValue("Positive thing 3"));
    }

    private void saveDay() {
        SharedPreferences sharedPreferences = getSharedPreferences("day_" + date.replaceAll("\\.", "-"), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("day_start_time", dayStartTime);
        editor.putString("day_end_time", dayEndTime);
        for (String task : tasks.keySet()) {
            editor.putString(task, String.valueOf(tasks.get(task)));
        }
        for (int i = 1; i <= editTexts.length; i++) {
            String posThing = "pos_thing_" + i;
            editor.putString(posThing, posThings.get(posThing));
        }
        editor.apply();
    }

    private void startTaskListeners() {
        int i = 0;
        for (final String task : tasks.keySet()) {
            checkBoxes[i++].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    tasks.put(task, isChecked);
                }
            });
        }
    }

    private void startTextListeners() {
        int i = 0;
        for (final String posThing : posThings.keySet()) {
            final EditText editText = editTexts[i++];
            final Button button = (Button) findViewById(R.id.button_submit);
            button.setEnabled(enableSubmit);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    button.setEnabled(false);
                    posThings.put(posThing, editText.getText().toString());
                    if (!(posThings.containsValue(null) ||
                            posThings.containsValue("") ||
                            posThings.containsValue("Positive thing 1") ||
                            posThings.containsValue("Positive thing 2") ||
                            posThings.containsValue("Positive thing 3"))) {
                        button.setEnabled(true);
                    }
                }
            });
        }
    }

    private void copyToClipboard(String msg) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", msg);
        clipboard.setPrimaryClip(clip);
    }

    private void saveToDB() {
        //TODO: Connect to and query from DB
    }

    public void startDay(View view) {
        dayStartTime = new SimpleDateFormat("HH:mm", Locale.GERMANY).format(new Date());
        date = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(new Date());
        String time = "Day started at " + dayStartTime;
        setContentView(R.layout.layout_tasks);
        loadCheckBoxes();
        startTaskListeners();
        TextView textView = (TextView) findViewById(R.id.text_day_start);
        textView.setText(time);
    }

    public void endDay(View view) {
        dayEndTime = new SimpleDateFormat("HH:mm", Locale.GERMANY).format(new Date());
        String time = "Day ended at " + dayEndTime;
        setContentView(R.layout.layout_day_end);
        loadEditTexts();
        startTextListeners();
        TextView textView = (TextView) findViewById(R.id.text_day_end);
        textView.setText(time);
    }

    public void submit(View view) {
        String summary = "Summary for " + date + ":\n\n" +
                "Day start at " + dayStartTime + "\n" +
                "Day end at " + dayEndTime + "\n\n";
        for (String task : tasks.keySet()) {
            summary += task + ": " + tasks.get(task) + "\n";
        }
        summary += "\n";
        for (String posThing : posThings.keySet()) {
            summary += "Positive thing " + posThing.substring(posThing.length() - 1) + ": " + posThings.get(posThing) + "\n";
        }
        copyToClipboard(summary);
        saveDay();
        setContentView(R.layout.layout_summary);
        TextView textView = (TextView) findViewById(R.id.text_summary);
        textView.setText(summary);
    }

    public void exit(View view) {
        finish();
        System.exit(0);
    }
}