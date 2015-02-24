package com.noelherrick.jell.mappers;

import com.noelherrick.jell.containers.Dyno;
import com.noelherrick.jell.containers.SimpleDyno;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DynoCreator
{
    private ResultSet resultSet;
    private String[] columnNames;

    public DynoCreator(ResultSet resultSet)
    {
        this.resultSet = resultSet;
    }

    public Dyno createDyno() throws SQLException {
        if (columnNames == null)
        {
            int colCount = resultSet.getMetaData().getColumnCount();

            columnNames = new String[colCount];

            for (int i = 0; i < colCount; i++)
            {
                // Column metadata is 1-indexed
                columnNames[i] = resultSet.getMetaData().getColumnName(i+1).toLowerCase();
            }
        }

        Map<String, Object> backingRow = new HashMap<>();

        for (int i = 0; i < columnNames.length; i++)
        {
            // ResultSet is 1-indexed, not 0-indexed
            backingRow.put(columnNames[i], resultSet.getObject(i + 1));
        }

        return new SimpleDyno(backingRow);
    }
}