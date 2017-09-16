package sytchie.daytasks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private String date = null, dayStartTime = null, dayEndTime = null;
    private boolean[] done = new boolean[5];
    private String[] posThings = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (dayStartTime == null) {
            setContentView(R.layout.layout_day_start);
        } else {
            setContentView(R.layout.layout_tasks);
            startTaskListeners();
        }
        Arrays.fill(done, false);
    }

    private void startTaskListener(CheckBox checkBox, final int i) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                done[i] = isChecked;
            }
        });
    }

    private void startTaskListeners() {
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        startTaskListener(checkBox, 0);
        checkBox = (CheckBox) findViewById(R.id.checkBox2);
        startTaskListener(checkBox, 1);
        checkBox = (CheckBox) findViewById(R.id.checkBox3);
        startTaskListener(checkBox, 2);
        checkBox = (CheckBox) findViewById(R.id.checkBox4);
        startTaskListener(checkBox, 3);
        checkBox = (CheckBox) findViewById(R.id.checkBox5);
        startTaskListener(checkBox, 4);
    }

    private void startTextListener(final EditText editText, final int i) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Button button = (Button) findViewById(R.id.button_submit);
                button.setEnabled(false);
                posThings[i] = editText.getText().toString();
                if (!(Arrays.asList(posThings).contains(null)
                        || Arrays.asList(posThings).contains("")
                        || posThings[0].matches("First positive thing")
                        || posThings[1].matches("Second positive thing")
                        || posThings[2].matches("Third positive thing"))) {
                    button.setEnabled(true);
                }
            }
        });
    }

    private void startTextListeners() {
        EditText editText = (EditText) findViewById(R.id.editText);
        startTextListener(editText, 0);
        editText = (EditText) findViewById(R.id.editText2);
        startTextListener(editText, 1);
        editText = (EditText) findViewById(R.id.editText3);
        startTextListener(editText, 2);
    }

    public void startDay(View view) {
        dayStartTime = new SimpleDateFormat("HH:mm", Locale.GERMANY).format(new Date());
        date = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(new Date());
        String time = "Day started at " + dayStartTime;
        setContentView(R.layout.layout_tasks);
        startTaskListeners();
        TextView textView = (TextView) findViewById(R.id.text_day_start);
        textView.setText(time);
    }

    public void endDay(View view) {
        dayEndTime = new SimpleDateFormat("HH:mm", Locale.GERMANY).format(new Date());
        String time = "Day ended at " + dayEndTime;
        setContentView(R.layout.layout_day_end);
        startTextListeners();
        TextView textView = (TextView) findViewById(R.id.text_day_end);
        textView.setText(time);
    }

    public void submit(View view) {
        String summary = "Summary for " + date + ":\n\n" +
                "Day start at " + dayStartTime + "\n" +
                "Day end at " + dayEndTime + "\n\n" +
                "Morning sport: " + done[0] + "\n" +
                "Sport: " + done[1] + "\n" +
                "Stepper: " + done[2] + "\n" +
                "Walk: " + done[3] + "\n" +
                "Read: " + done[4] + "\n\n" +
                "Positive thing 1: " + posThings[0] + "\n" +
                "Positive thing 2: " + posThings[1] + "\n" +
                "Positive thing 3: " + posThings[2];
        //TODO: Copy to clipboard
        //TODO: Save in DB
        setContentView(R.layout.layout_summary);
        TextView textView = (TextView) findViewById(R.id.text_summary);
        textView.setText(summary);
    }

    public void exit(View view) {
        finish();
        System.exit(0);
    }
}