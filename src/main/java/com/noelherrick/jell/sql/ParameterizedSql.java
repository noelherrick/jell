package com.noelherrick.jell.sql;

import java.util.Collection;


public class ParameterizedSql
{
    public final String sql;
    public final Collection<String> parameterNames;

    public ParameterizedSql(String sql, Collection<String> parameterNames)
    {
        this.sql = sql;
        this.parameterNames = parameterNames;
    }
}
