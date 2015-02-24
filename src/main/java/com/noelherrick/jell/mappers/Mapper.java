package com.noelherrick.jell.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Mapper
{
    public <T> T createObjectFromResultSet (ResultSet resultSet, Class<T> clazz)
            throws SQLException, IllegalAccessException, InstantiationException;
}
