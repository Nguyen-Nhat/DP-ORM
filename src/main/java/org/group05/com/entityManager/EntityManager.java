package org.group05.com.entityManager;

import org.group05.com.repository.IRepository;
import org.group05.com.repository.RepositoryImpl;
import org.group05.com.repository.RepositoryProxyHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class EntityManager{
    protected final Connection connection;

    public EntityManager(Connection connection) {
        this.connection = connection;
    }
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error while closing the connection", e);
        }
    }
    public IRepository<?> createRepo(Class<?> clazz) {
        return new RepositoryImpl<>(connection, clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> T createRepository(Class<T> repositoryInterface, IRepository<?> repository) {
        return (T) Proxy.newProxyInstance(
                repositoryInterface.getClassLoader(),
                new Class<?>[]{repositoryInterface},
                new RepositoryProxyHandler<>(repository)
        );
    }
}
