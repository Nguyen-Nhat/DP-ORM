package org.group05.com.entityManager;

import java.sql.Connection;

public class PostgresSQLEntityManager extends EntityManager {
    public PostgresSQLEntityManager(Connection connection) {
        super(connection);
    }
}
