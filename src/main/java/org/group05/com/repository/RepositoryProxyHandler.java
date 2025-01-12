package org.group05.com.repository;

import org.group05.com.annotations.Query;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RepositoryProxyHandler<U> implements InvocationHandler {
    private final IRepository<U> repository;

    public RepositoryProxyHandler(IRepository<U> repository) {
        this.repository = repository;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isAnnotationPresent(Query.class)) {
            Query queryAnnotation = method.getAnnotation(Query.class);
            return repository.executeQuery(method, queryAnnotation.value(), args);
        }

        try {
            Method implMethod = RepositoryImpl.class.getMethod(method.getName(), method.getParameterTypes());
            return implMethod.invoke(repository, args);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("Method not supported: " + method.getName());
        }
    }
}