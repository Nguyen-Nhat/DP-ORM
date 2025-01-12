package org.group05.com.repository;

import java.lang.reflect.Method;
import java.util.List;

public interface IRepository<U> {
    U find(Object id);
    List<U> find(String column, String value);
    U insert(U entity);
    U update(U entity);
    int delete(U entity);
    List<?>executeQuery(Method method, String query, Object... params);
}