package sytchie.daytasks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Date wakeUpTime = null;
    private boolean[] done = {false, false, false, false, false};
    private String[] posThings = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (wakeUpTime == null) {
            setContentView(R.layout.layout_day_start);
        } else {
            setContentView(R.layout.layout_tasks);
            startTaskListeners();
        }
    }

    private void startTaskListeners() {
        ((CheckBox) findViewById(R.id.checkBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    done[0] = true;
                }
            }
        });
        ((CheckBox) findViewById(R.id.checkBox2)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    done[1] = true;
                }
            }
        });
        ((CheckBox) findViewById(R.id.checkBox3)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    done[2] = true;
                }
            }
        });
        ((CheckBox) findViewById(R.id.checkBox4)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    done[3] = true;
                }
            }
        });
        ((CheckBox) findViewById(R.id.checkBox5)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    done[4] = true;
                }
            }
        });
    }

    public void startDay(View v) {
        String time = "Day started at " + new SimpleDateFormat("HH:mm", Locale.GERMANY).format(new Date());
        setContentView(R.layout.layout_tasks);
        startTaskListeners();
        TextView textView = (TextView) findViewById(R.id.text_day_start);
        textView.setText(time);
    }

    public void endDay(View v) {
        String time = "Day ended at " + new SimpleDateFormat("HH:mm", Locale.GERMANY).format(new Date());
        setContentView(R.layout.layout_day_end);
        TextView textView = (TextView) findViewById(R.id.text_day_end);
        textView.setText(time);
    }
}