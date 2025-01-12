package org.group05.com.utils;

import org.group05.com.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class Utils {
    public static String getTableName(Class<?> entityClass) {
        String tableName = entityClass.getAnnotation(Entity.class).tableName();
        if (tableName.isEmpty()) {
            tableName = entityClass.getSimpleName().toLowerCase();
        }
        return tableName;
    }

    public static String getColumnName(Class<?> entityClass, String fieldName) {
        try {
            Field field = entityClass.getDeclaredField(fieldName);

            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                if (!column.name().isEmpty()) {
                    return column.name();
                }
            }

            return field.getName();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field '" + fieldName + "' not found in class " + entityClass.getSimpleName(), e);
        }
    }

    public static String getPrimaryKeyName(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return getColumnName(entityClass, field.getName());
            }
        }
        throw new RuntimeException("Primary key not found in class " + entityClass.getSimpleName());
    }

    public static Object getPrimaryKeyValue(Object entity) {
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return getFieldValue(field, entity);
            }
        }
        throw new RuntimeException("Primary key not found in class " + entity.getClass().getSimpleName());
    }

    public static Object getFieldValue(Field field, Object entity) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error getting field value", e);
        }
    }

    public static String getForeignKeyName(Class<?> childClass, Class<?> parentClass) {
        for (Field field : childClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(JoinColumn.class)) {
                JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                if (!joinColumn.name().isEmpty() && field.getType().equals(parentClass)) {
                    return joinColumn.name();
                }
                return joinColumn.name();
            }
        }
        throw new RuntimeException("Foreign key not found in " + childClass.getSimpleName());
    }

    public static Class<?> getFieldType(Field field) {
        if (field.getType().equals(List.class)) {
            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            return (Class<?>) genericType.getActualTypeArguments()[0];
        }
        return field.getType();
    }

    public static Field getForeignKeyField(Class<?> childClass, Class<?> parentClass) {
        for (Field field : childClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ManyToOne.class)) {
                if (field.getType().equals(parentClass)) {
                    return field;
                }
            }
        }
        throw new RuntimeException("Foreign key not found in " + childClass.getSimpleName());
    }


}
