package com.noelherrick.jell.mappers;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PublicFieldMapper implements Mapper
{
    private List<FieldMapper> mappers;

    // We are assuming that people will use public, writable fields that are exactly the same as the column names
    public <T> T createObjectFromResultSet (ResultSet resultSet, Class<T> clazz)
            throws SQLException, IllegalAccessException, InstantiationException
    {
        if (mappers == null)
        {
            int colCount = resultSet.getMetaData().getColumnCount();

            Map<String, Integer> columnNames = new HashMap<>();

            for (int i = 0; i < colCount; i++)
            {
                // ResultSet metadata is 1-indexed
                columnNames.put(resultSet.getMetaData().getColumnName(i+1).toLowerCase(), i);
            }

            mappers = new ArrayList<>();

            for (Field field : clazz.getFields())
            {
                String fieldName = field.getName().toLowerCase();

                if (columnNames.containsKey(fieldName))
                {
                    final int index = columnNames.get(fieldName);

                    if (field.getType().equals(java.util.Date.class))
                    {
                        mappers.add( (rs, obj) -> field.set(obj, new java.util.Date(rs.getTimestamp(index+1).getTime())));
                    }
                    else
                    {
                        mappers.add( (rs, obj) -> field.set(obj, rs.getObject(index+1)) );
                    }
                }
            }
        }

        T newObj = clazz.newInstance();

        for (FieldMapper mapper : mappers)
        {
            mapper.map(resultSet, newObj);
        }

        return  newObj;
    }


}
