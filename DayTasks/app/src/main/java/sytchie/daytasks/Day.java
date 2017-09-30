package sytchie.daytasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

class Day {
    String date = "", dayStartTime = "", dayEndTime = "";
    Map<String, Boolean> tasks = new LinkedHashMap<>();
    int posThingsNum = 3;
    Map<String, String> posThings = new LinkedHashMap<>();

    Day(ArrayList<String> taskList) {
        for (String task : taskList) {
            tasks.put(task, false);
        }
        for (int i = 1; i <= posThingsNum; i++) {
            posThings.put("pos_thing_" + i, "Positive thing " + i);
        }
    }
}
