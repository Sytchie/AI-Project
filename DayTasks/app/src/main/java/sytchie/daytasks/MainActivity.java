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
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> taskList = new ArrayList<>(Arrays.asList(new String[]{"Morning Sport", "Sport", "Stepper", "Walk", "Read"}));
    private Day day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDay();
        if (Objects.equals(day.dayStartTime, "")) {
            setContentView(R.layout.layout_day_start);
        } else {
            setContentView(R.layout.layout_tasks);
            String time = "Day started at " + day.dayStartTime;
            TextView textView = (TextView) findViewById(R.id.text_day_start);
            textView.setText(time);
            loadCheckBoxes();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDay();
    }

    private void loadDay() {
        SharedPreferences sharedPreferences = getSharedPreferences("day_tasks", MODE_PRIVATE);
        Gson gson = new Gson();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY).format(new Date(new Date().getTime() - 5 * 3600 * 1000));
        String json = sharedPreferences.getString("day_" + currentDate, "");
        if (json.matches("")) {
            day = new Day(taskList);
        } else {
            day = gson.fromJson(json, Day.class);
        }
    }

    private void loadCheckBoxes() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_tasks_inner);
        for (final String task : day.tasks.keySet()) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(task);
            checkBox.setChecked(day.tasks.get(task));
            linearLayout.addView(checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    day.tasks.put(task, isChecked);
                    enableEndDay();
                }
            });
        }
        enableEndDay();
    }

    private void loadEditTexts() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_day_end_inner);
        for (int i = 1; i <= day.posThingsNum; i++) {
            final EditText editText = new EditText(this);
            final String posKey = "pos_thing_" + i;
            editText.setText(day.posThings.get(posKey));
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
                    day.posThings.put(posKey, editText.getText().toString());
                    enableSubmit();
                }
            });
        }
        enableSubmit();
    }

    private void saveDay() {
        SharedPreferences sharedPreferences = getSharedPreferences("day_tasks", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(day);
        editor.putString("day_" + day.date.replaceAll("\\.", "-"), json);
        editor.apply();
    }

    public void startDay(View view) {
        setContentView(R.layout.layout_tasks);
        day.dayStartTime = new SimpleDateFormat("HH:mm", Locale.GERMANY).format(new Date());
        day.date = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(new Date(new Date().getTime() - 5 * 3600 * 1000));
        String timeString = "Day started at " + day.dayStartTime;
        TextView textView = (TextView) findViewById(R.id.text_day_start);
        textView.setText(timeString);
        loadCheckBoxes();
    }

    private void enableEndDay() {
        Button button = (Button) findViewById(R.id.button_end_day);
        int time = Integer.parseInt(new SimpleDateFormat("HH", Locale.GERMANY).format(new Date()));
        button.setEnabled(time < 5 || time > 21);
    }

    public void endDay(View view) {
        setContentView(R.layout.layout_day_end);
        TextView textView = (TextView) findViewById(R.id.text_day_end);
        day.dayEndTime = new SimpleDateFormat("HH:mm", Locale.GERMANY).format(new Date());
        String timeString = "Day ended at " + day.dayEndTime;
        textView.setText(timeString);
        textView = (TextView) findViewById(R.id.text_pos_things);
        textView.setText("\nWrite down " + day.posThingsNum + " positive things that happened today:");
        loadEditTexts();
    }

    private void copyToClipboard(String string) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", string);
        clipboard.setPrimaryClip(clip);
    }

    private void enableSubmit() {
        Button button = (Button) findViewById(R.id.button_submit);
        for (int i = 1; i <= day.posThingsNum; i++) {
            String posKey = day.posThings.get("pos_thing_" + i);
            if (posKey.matches("Positive thing " + i) || posKey.matches("")) {
                button.setEnabled(false);
                return;
            }
        }
        button.setEnabled(true);
    }

    public void submit(View view) {
        saveDay();
        setContentView(R.layout.layout_summary);
        String summary = "Summary for " + day.date + ":\n\n" +
                "Day start at " + day.dayStartTime + "\n" +
                "Day end at " + day.dayEndTime + "\n\n";
        for (String task : day.tasks.keySet()) {
            String string = "No";
            if (day.tasks.get(task)) {
                string = "Yes";
            }
            summary += task + ": " + string + "\n";
        }
        summary += "\n";
        for (int i = 1; i <= day.posThingsNum; i++) {
            String posKey = "pos_thing_" + i;
            summary += "Positive thing " + i + ": " + day.posThings.get(posKey) + "\n";
        }
        summary = summary.substring(0, summary.length() - 1);
        copyToClipboard(summary);
        TextView textView = (TextView) findViewById(R.id.text_summary);
        textView.setText(summary);
    }
}