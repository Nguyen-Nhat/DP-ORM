package org.group05.com.repository;

import org.group05.com.annotations.Id;
import org.group05.com.annotations.ManyToOne;
import org.group05.com.annotations.OneToMany;
import org.group05.com.utils.Utils;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

public class RepositoryImpl<U> implements IRepository<U> {
    private final Connection connection;
    private final Class<U> entityClass;
    private final Map<String, Object> map = new HashMap<>();

    public RepositoryImpl(Connection connection, Class<U> entityClass) {
        this.connection = connection;
        this.entityClass = entityClass;
    }
    @Override
    public List<?> executeQuery(Method method, String query, Object... params) {
        Class<?> typeClass = null;
        Type genericReturnType = method.getGenericReturnType();
        if (genericReturnType instanceof ParameterizedType parameterizedType) {
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            for (Type typeArgument : typeArguments) {
                if (typeArgument instanceof Class) {
                    typeClass = (Class<?>) typeArgument;
                }
            }
        }


        List<Object> results = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }
            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                if (entityClass.equals(typeClass)){
                    U entity = entityClass.getDeclaredConstructor().newInstance();
                    mapResultSetToEntity(resultSet, entity);
                    results.add(entity);
                } else {
                    int columnCount = resultSet.getMetaData().getColumnCount();
                    Object[] row = new Object[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        row[i] = resultSet.getObject(i + 1);
                    }
                    results.add(row);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error executing query: " + query, e);
        }
        return results;
    }

    @Override
    public U find(Object value) {
        return findEntity(entityClass,value);
    }

    @Override
    public List<U> find(String column, String value) {
        return findEntity(entityClass,column,value);
    }

    @Override
    public U insert(U entity) {
        return insertEntity(entity);
    }

    @Override
    public U update(U entity) {
        return updateEntity(entity);
    }

    @Override
    public int delete(U entity) {
        return deleteEntity(entity);
    }


    private <T> T insertEntity(T entity) {
        try {
            String tableName = Utils.getTableName(entity.getClass());
            StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
            StringBuilder values = new StringBuilder(" VALUES (");
            List<Object> params = new ArrayList<>();

            // Insert after the parent entity
            Map<Field, List<Object>> childEntities = new HashMap<>();

            for (Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object val = field.get(entity);
                if (val == null) {
                    continue;
                }

                if (field.isAnnotationPresent(OneToMany.class)){
                    // If the cascade type is ALL, insert the entities in the list after inserting the parent entity
                    if (field.getAnnotation(OneToMany.class).cascade().equals("ALL")) {
                        List<?> entities = (List<?>) val;
                        for (Object obj : entities) {
                            if (Utils.getPrimaryKeyValue(obj) == null ||
                                    findEntity(obj.getClass(), Utils.getPrimaryKeyValue(obj)) == null) {
                                Field foreignField = Utils.getForeignKeyField(obj.getClass(), entity.getClass());
                                List<Object> list = childEntities.getOrDefault(foreignField, new ArrayList<>());
                                list.add(obj);
                                childEntities.put(foreignField, list);
                            }
                        }
                    }
                } else if (field.isAnnotationPresent(ManyToOne.class)) {
                    // If the cascade type is ALL, insert the parent entity (not exists in the database)
                    if (field.getAnnotation(ManyToOne.class).cascade().equals("ALL")) {
                        Object foreignKey = Utils.getPrimaryKeyValue(val);
                        if (findEntity(val.getClass(), foreignKey) == null)
                            insertEntity(val);
                    }

                    String foreignKeyName = Utils.getForeignKeyName(entity.getClass(), val.getClass());
                    query.append(foreignKeyName).append(", ");
                    values.append("?, ");
                    params.add(Utils.getPrimaryKeyValue(val));
                } else {
                    query.append(Utils.getColumnName(entity.getClass(), field.getName())).append(", ");
                    values.append("?, ");
                    params.add(val);
                }
            }

            query.delete(query.length() - 2, query.length());
            values.delete(values.length() - 2, values.length());
            query.append(")").append(values).append(")");

            PreparedStatement preparedStatement = connection.prepareStatement
                    (query.toString(), PreparedStatement.RETURN_GENERATED_KEYS);

            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
            System.out.println(preparedStatement.toString());
            int newRow = preparedStatement.executeUpdate();

            if (newRow > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    Object key = generatedKeys.getObject(1);
                    setKeyValue(entity, key);
                }

                for (Field field : childEntities.keySet()) {
                    List<Object> entities = childEntities.get(field);
                    for (Object obj : entities) {
                        field.setAccessible(true);
                        field.set(obj, entity);
                        insertEntity(obj);
                    }
                }
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error while inserting entity", e);
        }
    }

    private <T> T updateEntity(T entity) {
        try {
            String tableName = Utils.getTableName(entity.getClass());
            StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");
            List<Object> params = new ArrayList<>();
            Object key = Utils.getPrimaryKeyValue(entity);

            for (Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object val = field.get(entity);
                if (val == null) {
                    continue;
                }

                if (field.isAnnotationPresent(OneToMany.class)){
                } else if (field.isAnnotationPresent(ManyToOne.class)) {
                    String foreignKeyName = Utils.getForeignKeyName(entity.getClass(), val.getClass());
                    query.append(foreignKeyName).append(" = ?, ");
                    params.add(Utils.getPrimaryKeyValue(val));
                } else {
                    query.append(Utils.getColumnName(entity.getClass(), field.getName())).append(" = ?, ");
                    params.add(val);
                }
            }

            query.delete(query.length() - 2, query.length());
            query.append(" WHERE ").append(Utils.getPrimaryKeyName(entity.getClass())).append(" = ?");
            params.add(key);

            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error while updating entity", e);
        }
    }

    private <T> int deleteEntity(T entity) {
        try {
            if (entity == null) {
                return 0;
            }
            int nRows = 0;
            for (Field field : entity.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(OneToMany.class)
                        && field.getAnnotation(OneToMany.class).cascade().equals("ALL")){
                    field.setAccessible(true);
                    List<?> entities = (List<?>) field.get(entity);
                    for (Object obj : entities) {
                        nRows += deleteEntity(obj);
                    }
                }
            }
            String tableName = Utils.getTableName(entity.getClass());
            String primaryKeyName = Utils.getPrimaryKeyName(entity.getClass());

            String query = "DELETE FROM " + tableName + " WHERE " + primaryKeyName + " = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            Object key = Utils.getPrimaryKeyValue(entity);
            preparedStatement.setObject(1, key);
            nRows += preparedStatement.executeUpdate();

            return nRows;
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting entity", e);
        }
    }

    private <T> T findEntity(Class<T> entityClass, Object value) {
        if (value == null)
            return null;
        String tableName = Utils.getTableName(entityClass);
        String primaryKeyName = Utils.getPrimaryKeyName(entityClass);
        String key = tableName + "_" + value;
        if (map.containsKey(key)) {
            return (T) map.get(key);
        }

        String query = "SELECT * FROM " + tableName + " WHERE " + primaryKeyName + " = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, value);
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            T entity = null;
            if (resultSet.next()) {
                entity = entityClass.getDeclaredConstructor().newInstance();
                mapResultSetToEntity(resultSet, entity);
            }
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while fetching data from the database.");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> findEntity(Class<T> entityClass, String column, Object value) {
        String tableName = Utils.getTableName(entityClass);
        String query = "SELECT * FROM " + tableName + " WHERE " + column + " = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, value);
            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<T> entities = new ArrayList<>();
            while (resultSet.next()) {
                T entity = entityClass.getDeclaredConstructor().newInstance();
                mapResultSetToEntity(resultSet, entity);
                entities.add(entity);
            }
            return entities;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while fetching data from the database.");
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // Helper methods
    private <T> void mapResultSetToEntity(ResultSet resultSet, T entity) {
        try {
            Class<?> entityClass = entity.getClass();
            Object val;

            List<Field> delayFields = new ArrayList<>();
            for (Field field : entityClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(ManyToOne.class)) {
                    delayFields.addFirst(field);continue;
                } else if (field.isAnnotationPresent(OneToMany.class)) {
                    delayFields.addLast(field);continue;
                } else {
                    String columnName = Utils.getColumnName(entityClass, field.getName());
                    val = resultSet.getObject(columnName);
                }
                field.setAccessible(true);
                field.set(entity, val);
            }
            String key = Utils.getTableName(entityClass) + "_" + Utils.getPrimaryKeyValue(entity);
            map.put(key, entity);
            for (Field field : delayFields) {
                if (field.isAnnotationPresent(ManyToOne.class)) {
                    Class<?> parentClass = Utils.getFieldType(field);
                    String foreignKeyName = Utils.getForeignKeyName(entityClass, parentClass);
                    val = findEntity(parentClass, resultSet.getObject(foreignKeyName));
                }
                else {
                    Class<?> parentClass = Utils.getFieldType(field);
                    String foreignKeyName = Utils.getForeignKeyName(entityClass, parentClass);
                    val = findEntity(parentClass, foreignKeyName, Utils.getPrimaryKeyValue(entity));
                }
                field.setAccessible(true);
                field.set(entity, val);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while mapping result set to entity", e);
        }
    }

    private void setKeyValue(Object entity, Object value) {
        try {
            Class<?> entityClass = entity.getClass();
            for (Field field : entityClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    if (field.getType().equals(Integer.class)) {
                        field.set(entity, ((Number) value).intValue());
                    } else {
                        field.set(entity, value);
                    }
                    return;
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error setting field value", e);
        }
    }

}
