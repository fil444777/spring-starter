package spring.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataUtil {

    private final ConnectionProperties connectionProperties;

    public DataUtil(ConnectionProperties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(connectionProperties.getUrl(),
                    connectionProperties.getUserName(), connectionProperties.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
