package com.example.support;

import java.util.List;

public class ConfigDiff {

    private List<ConfigChangedItem> list;

    public boolean hasDiff() {
        return false;
    }

    public List<ConfigChangedItem> getList() {
        return list;
    }
}
