package sytchie.daytasks;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

class Day {
    String date = "", dayStartTime = "", dayEndTime = "";
    Map<String, Boolean> tasks = new LinkedHashMap<>();
    int posThingsNum;
    Map<String, String> posThings = new LinkedHashMap<>();

    Day(ArrayList<String> taskList, int posThingsNum) {
        for (String task : taskList) {
            tasks.put(task, false);
        }
        this.posThingsNum = posThingsNum;
        for (int i = 1; i <= posThingsNum; i++) {
            posThings.put("pos_thing_" + i, "Positive thing " + i);
        }
    }
}
