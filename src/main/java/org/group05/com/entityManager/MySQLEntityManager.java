package org.group05.com.entityManager;
import java.sql.Connection;

public class MySQLEntityManager extends EntityManager {
    public MySQLEntityManager(Connection connection) {
        super(connection);
    }

}


