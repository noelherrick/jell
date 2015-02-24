package com.noelherrick.jell.containers;

import com.noelherrick.jell.mappers.Mapper;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class LazyResultSetCollection<E> implements Collection<E>
{
    private ResultSet rs;
    private Mapper mapper;
    private Class<E> clazz;

    public LazyResultSetCollection(ResultSet resultSet, Mapper mapper, Class<E> clazz)
    {
        rs = resultSet;
        this.mapper = mapper;
        this.clazz = clazz;
    }

    @Override
    public int size() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isEmpty() {
        throw new NotImplementedException();
    }

    @Override
    public boolean contains(Object o) {
        throw new NotImplementedException();
    }

    @Override
    public Iterator<E> iterator() {
        return new ResultSetIterator<>(rs, clazz);
    }

    @Override
    public Object[] toArray() {
        List<E> results = new ArrayList<>();

        this.stream().forEach(results::add);

        return results.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        List<E> results = new ArrayList<>();

        this.stream().forEach(results::add);

        return results.toArray(a);
    }

    @Override
    public boolean add(E e) {
        throw new NotImplementedException();
    }

    @Override
    public boolean remove(Object o) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new NotImplementedException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new NotImplementedException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new NotImplementedException();
    }

    @Override
    public void clear()
    {
    }

    private class ResultSetIterator<C> implements Iterator<C>
    {
        private ResultSet rs;
        private C next;
        private Class<C> clazz;

        public ResultSetIterator (ResultSet resultSet, Class<C> clazz)
        {
            rs = resultSet;
            this.clazz = clazz;
        }

        @Override
        public boolean hasNext() {
            if (next == null)
            {
                boolean hasNext = false;
                try
                {
                    if (hasNext = rs.next())
                    {
                        next = mapper.createObjectFromResultSet(rs, clazz);
                    }
                    else
                    {
                        return false;
                    }

                }
                catch (Exception e)
                {
                }

                return hasNext;
            }
            else
            {

                return true;
            }
        }

        @Override
        public C next() {
            if (next == null)
            {
                try
                {
                    rs.next();
                    return mapper.createObjectFromResultSet(rs, clazz);
                }
                catch (Exception e)
                {
                    return null;
                }
            }
            else
            {
                C returnItem = next;
                next = null;
                return returnItem;
            }
        }
    }
}
