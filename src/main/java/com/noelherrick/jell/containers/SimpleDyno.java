package com.noelherrick.jell.containers;


import java.util.Map;

public class SimpleDyno implements Dyno
{
    private Map<String, Object> backingRow;

    public SimpleDyno(Map<String, Object> backingRow)
    {
        this.backingRow = backingRow;
    }

    @Override
    public Object obj(String column) {
        return backingRow.get(column.toLowerCase());
    }

    @Override
    public String str(String column) {
        return backingRow.get(column.toLowerCase()).toString();
    }

}
