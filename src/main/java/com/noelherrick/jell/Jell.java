package com.noelherrick.jell;

import com.noelherrick.jell.containers.Dyno;
import com.noelherrick.jell.containers.LazyResultSetCollection;
import com.noelherrick.jell.mappers.*;
import com.noelherrick.jell.sql.ParameterConsumer;
import com.noelherrick.jell.sql.ParameterizedSql;
import com.noelherrick.jell.sql.Parameterizer;

import java.sql.*;
import java.util.*;

public class Jell
{
    private Connection connection;
    private MapperGenerator mapperGenerator;
    private ParameterConsumer parameterConsumer;
    private Parameterizer parameterizer;

    public Jell(Connection connection)
    {
        this.connection = connection;
        this.mapperGenerator = PublicFieldMapper::new;
        this.parameterConsumer = new ParameterConsumer();
        this.parameterizer = new Parameterizer();
    }

    public Collection<Dyno> query (String sql, boolean buffered)
            throws SQLException
    {
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery(sql);

        if (buffered)
        {
            List<Dyno> dynos = new ArrayList<>();

            DynoCreator creator = new DynoCreator(rs);

            while (rs.next())
            {
                dynos.add(creator.createDyno());
            }

            return dynos;
        }
        else
        {
            return new LazyResultSetCollection<>(rs, new DynoMapper(), Dyno.class);
        }
    }

    public Collection<Dyno> query (String sql)
            throws SQLException
    {
        return query(sql, true);
    }

    public <T> Collection<T> query (String sql, Class<T> clazz, boolean buffered)
            throws SQLException, InstantiationException, IllegalAccessException
    {
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery(sql);

        Mapper mapper = mapperGenerator.getInstance();

        if (buffered)
        {
            List<T> results = new ArrayList<>();

            while (rs.next())
            {
                results.add(mapper.createObjectFromResultSet(rs, clazz));
            }

            return results;
        }
        else
        {
            return new LazyResultSetCollection<>(rs, mapper, clazz);
        }
    }

    public <T> Collection<T> query (String sql, Class<T> clazz)
            throws SQLException, InstantiationException, IllegalAccessException
    {
        return query(sql, clazz, true);
    }

    public <T> Collection<T> query (String sql, Class<T> clazz, Object param)
            throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        return query(sql, clazz, param, true);
    }

    public <T> Collection<T> query (String sql, Class<T> clazz, Object param, boolean buffered)
            throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException
    {

        Mapper mapper = mapperGenerator.getInstance();

        ParameterizedSql parameterizedSql = parameterizer.parameterizeSql(sql);

        PreparedStatement ps = connection.prepareStatement(parameterizedSql.sql);

        parameterConsumer.consumeParameter(ps, parameterizedSql.parameterNames, param);

        ResultSet rs = ps.executeQuery();

        if (buffered)
        {
            List<T> results = new ArrayList<>();

            while (rs.next())
            {
                results.add(mapper.createObjectFromResultSet(rs, clazz));
            }

            return results;
        }
        else
        {
            return new LazyResultSetCollection<>(rs, mapper, clazz);
        }
    }

    public int execute (String sql) throws SQLException {
        Statement statement = connection.createStatement();

        return statement.executeUpdate(sql);
    }

    public int executeBatch (String sql, Collection params)
            throws SQLException, NoSuchFieldException, IllegalAccessException
    {
        ParameterizedSql parameterizedSql = parameterizer.parameterizeSql(sql);

        PreparedStatement ps = connection.prepareStatement(parameterizedSql.sql);

        for (Object param : params)
        {
            parameterConsumer.consumeParameter(ps, parameterizedSql.parameterNames, param);
            ps.addBatch();
        }

        int[] counts = ps.executeBatch();

        int sum = 0;

        for (int count : counts)
        {
            sum += count;
        }

        return sum;
    }

    public int execute (String sql, Object param)
            throws SQLException, NoSuchFieldException, IllegalAccessException
    {
        ParameterizedSql parameterizedSql = parameterizer.parameterizeSql(sql);

        PreparedStatement ps = connection.prepareStatement(parameterizedSql.sql);

        parameterConsumer.consumeParameter(ps, parameterizedSql.parameterNames, param);

        return ps.executeUpdate();
    }
}
