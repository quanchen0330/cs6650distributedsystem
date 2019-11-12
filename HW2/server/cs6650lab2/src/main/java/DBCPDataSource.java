import java.sql.*;
import org.apache.commons.dbcp.*;


public class DBCPDataSource {
    //private  BasicDataSource dataSource;
    private static BasicDataSource dataSource = new BasicDataSource();

    // NEVER store sensitive information below in plain text!
    private static final String HOST_NAME = System.getenv("MySQL_IP_ADDRESS");
    private static final String PORT = System.getenv("MySQL_PORT");
    private static final String DATABASE = System.getenv("MySQL_DBNAME");
    private static final String USERNAME = System.getenv("DB_USERNAME");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

//    private static final String NAME="admin";
//    private static final String HOST_NAME = "localhost";
//   // private static final String HOST_NAME = "database-3.cvfxeylhncek.us-east-1.rds.amazonaws.com";
//      private static final String PORT = "9000";
//    //private static final String PORT = "3306";
//    private static final String DATABASE = "LiftRides";
//    private static final String USERNAME = "admin";
//    private static final String PASSWORD = "admin123";

    static {
        // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html
        String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
        dataSource.setUrl(url);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setMinIdle(0);
        dataSource.setMaxIdle(-1);
        dataSource.setMaxActive(-1);
        dataSource.setMaxOpenPreparedStatements(1000000);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }



}