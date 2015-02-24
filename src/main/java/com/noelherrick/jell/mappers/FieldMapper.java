package com.noelherrick.jell.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface FieldMapper
{
    public void map (ResultSet resultSet, Object object) throws SQLException, IllegalAccessException;
}
