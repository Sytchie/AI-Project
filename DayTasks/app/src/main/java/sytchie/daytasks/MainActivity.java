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
    private String[] taskList = {"Morning Sport", "Sport", "Stepper", "Walk", "Read"};
    private int posThingNum = 3;
    private String date = null, dayStartTime = null, dayEndTime = null;
    private Map<String, Boolean> tasks = new LinkedHashMap<>();
    private Map<String, String> posThings = new LinkedHashMap<>();

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
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDay();
    }

    private void loadDay() {
        date = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(new Date(new Date().getTime() - 5 * 3600 * 1000));
        SharedPreferences sharedPreferences = getSharedPreferences("day_" + date.replaceAll("\\.", "-"), MODE_PRIVATE);
        //sharedPreferences.edit().clear().apply(); //Clear day
        dayStartTime = sharedPreferences.getString("day_start_time", null);
        dayEndTime = sharedPreferences.getString("day_end_time", null);
        for (String task : taskList) {
            tasks.put(task, Boolean.valueOf(sharedPreferences.getString(task, null)));
        }
        for (int i = 1; i <= posThingNum; i++) {
            String posThing = "pos_thing_" + i;
            String posString = sharedPreferences.getString(posThing, null);
            if (posString == null) {
                posString = "Positive thing " + i;
            }
            posThings.put(posThing, posString);
        }
    }

    private void loadCheckBoxes() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_tasks_inner);
        for (final String task : tasks.keySet()) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(task);
            checkBox.setChecked(tasks.get(task));
            linearLayout.addView(checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    tasks.put(task, isChecked);
                    enableEndDay();
                }
            });
        }
        enableEndDay();
    }

    private void loadEditTexts() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_day_end_inner);
        for (int i = 1; i <= posThingNum; i++) {
            final EditText editText = new EditText(this);
            final int finalI = i;
            String string = posThings.get("pos_thing_" + i);
            editText.setText(string);
            editText.setSelectAllOnFocus(true);
            editText.setSingleLine(true);
            linearLayout.addView(editText);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    posThings.put("pos_thing_" + finalI, editText.getText().toString());
                    enableSubmit();
                }
            });
        }
        enableSubmit();
    }

    private void saveDay() {
        SharedPreferences sharedPreferences = getSharedPreferences("day_" + date.replaceAll("\\.", "-"), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("day_start_time", dayStartTime);
        editor.putString("day_end_time", dayEndTime);
        for (String task : tasks.keySet()) {
            editor.putString(task, String.valueOf(tasks.get(task)));
        }
        for (int i = 1; i <= posThingNum; i++) {
            String posThing = "pos_thing_" + i;
            editor.putString(posThing, posThings.get(posThing));
        }
        editor.apply();
    }

    private void copyToClipboard(String msg) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", msg);
        clipboard.setPrimaryClip(clip);
    }

    public void startDay(View view) {
        dayStartTime = new SimpleDateFormat("HH:mm", Locale.GERMANY).format(new Date());
        date = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(new Date());
        String time = "Day started at " + dayStartTime;
        setContentView(R.layout.layout_tasks);
        loadCheckBoxes();
        TextView textView = (TextView) findViewById(R.id.text_day_start);
        textView.setText(time);
    }

    private void enableEndDay() {
        Button button = (Button) findViewById(R.id.button_end_day);
        button.setEnabled(!(Integer.parseInt(new SimpleDateFormat("HH", Locale.GERMANY).format(new Date())) < 22));
    }

    public void endDay(View view) {
        dayEndTime = new SimpleDateFormat("HH:mm", Locale.GERMANY).format(new Date());
        String time = "Day ended at " + dayEndTime;
        setContentView(R.layout.layout_day_end);
        loadEditTexts();
        TextView textView = (TextView) findViewById(R.id.text_day_end);
        textView.setText(time);
    }

    private void enableSubmit() {
        Button button = (Button) findViewById(R.id.button_submit);
        boolean b = true;
        for (int i = 1; i <= posThingNum; i++) {
            String string = posThings.get("pos_thing_" + i);
            if (string == null || string.matches("Positive thing " + i) || string.matches("")) {
                b = false;
            }
        }
        button.setEnabled(b);
    }

    public void submit(View view) {
        String summary = "Summary for " + date + ":\n\n" +
                "Day start at " + dayStartTime + "\n" +
                "Day end at " + dayEndTime + "\n\n";
        for (String task : tasks.keySet()) {
            summary += task + ": " + tasks.get(task) + "\n";
        }
        summary += "\n";
        for (int i = 1; i <= posThings.keySet().size(); i++) {
            String posThing = "pos_thing_" + i;
            summary += "Positive thing " + i + ": " + posThings.get(posThing) + "\n";
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