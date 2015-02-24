package com.noelherrick.jell.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DynoMapper implements Mapper
{
    private DynoCreator dynoCreator;

    @Override
    public <T> T createObjectFromResultSet(ResultSet resultSet, Class<T> clazz)
            throws SQLException, IllegalAccessException, InstantiationException
    {
        if (dynoCreator == null)
        {
            dynoCreator = new DynoCreator(resultSet);
        }

        return (T) dynoCreator.createDyno();
    }
}
