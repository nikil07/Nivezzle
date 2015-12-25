package com.nikhil.volley.util;

import com.nikhil.volley.dbutils.DBAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LeaderBoardChecklist {
    static DBAdapter adapter;

    static Set<String> checkList = new HashSet<>();

    public static Set<String> getCheckList() {
        return checkList;
    }

    public static void setCheckList(String text) {

        checkList.add(text);
    }
}
