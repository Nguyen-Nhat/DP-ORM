package org.group05.com.logging.proxy;

import org.group05.com.entityManager.EntityManager;
import org.group05.com.logging.strategy.LoggingStrategy;
import org.group05.com.repository.IRepository;

import java.lang.reflect.Method;
import java.util.List;


public class RepositoryLoggingProxy<U> implements IRepository<U> {
    private final IRepository<U> repository;
    private final LoggingStrategy loggingStrategy;

    public RepositoryLoggingProxy(IRepository<U> repository, LoggingStrategy loggingStrategy) {
        this.repository = repository;
        this.loggingStrategy = loggingStrategy;
    }

    @Override
    public U find(Object id) {
        loggingStrategy.log("Finding entity with id: " + id);
        return repository.find(id);
    }

    @Override
    public List<U> find(String column, String value) {
        loggingStrategy.log("Finding entity with " + column + " = " + value);
        return repository.find(column, value);
    }

    @Override
    public U insert(U entity) {
        loggingStrategy.log("Saving entity: " + entity.toString());
        return repository.find(entity);
    }

    @Override
    public U update(U entity) {
        loggingStrategy.log("Updating entity: " + entity.toString());
        return repository.update(entity);
    }

    @Override
    public int delete(U entity) {
        loggingStrategy.log("Deleting entity: " + entity.toString());
        return repository.delete(entity);
    }

    @Override
    public List<?> executeQuery(Method method, String query, Object... params) {
        loggingStrategy.log("Executing query: " + query);
        return repository.executeQuery(method, query, params);
    }
}
