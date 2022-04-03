package com.demo.provisioner;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class SFProvisioning {

    public static void main(String[] args) throws SQLException {
        System.out.println("sf provisioning....");
        System.out.println("trigger actions....");
        simpleJDBCConnection1();
    }

    private static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.snowflake.client.jdbc.SnowflakeDriver");
            // Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
        } catch (ClassNotFoundException ex) {
            System.err.println("Driver not found");
        }
        // build connection properties
        Properties props = new Properties();
       /* props.put("user", "A1SF_USER_104_803_ETL");
        props.put("password", "AAA35W@eXw6UvaWmL@l");*/
        props.put("user", "SMUNSWAMY");
        props.put("password", "0G62mKHqTi965SUj0iQo");
        props.put("db", "DEMO");
        props.put("warehouse", "A1SF_WH_INTERNAL_ETL_XSMALL");
        props.put("account", "agilonedev.us-east-1");  // replace "" with your account name
        props.put("schema", "PUBLIC");
        // props.put("tracing", "on");

        // create a new connection
        String connectStr = System.getenv("SF_JDBC_CONNECT_STRING");
        // use the default connection string if it is not set in environment
        if (connectStr == null) {
            //  public static final String CONNECTION_STR = "jdbc:snowflake://{0}.snowflakecomputing.com";
            connectStr = "jdbc:snowflake://agilonedev.us-east-1.snowflakecomputing.com"; // replace accountName with your account name
        }
        try {
            return DriverManager.getConnection(connectStr, props);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private static void simpleJDBCConnection1() throws SQLException {
        // get connection
        System.out.println("Create JDBC connection");
        Connection connection = getConnection();
        System.out.println("Done creating JDBC connectionn");
        // create statement
        System.out.println("Create JDBC statement");
        Statement statement = connection.createStatement();
        System.out.println("Done creating JDBC statementn");
        // create a table
        System.out.println("Create demo table");
        statement.executeUpdate("create or replace table demo(C1 STRING)");
        statement.close();
        System.out.println("Done creating demo tablen");
        // insert a row
        System.out.println("Insert 'hello world'");
        statement.executeUpdate("insert into demo values ('hello world')");
        statement.close();
        System.out.println("Done inserting 'hello world'n");
        // query the data
        System.out.println("Query demo");
        ResultSet resultSet = statement.executeQuery("SELECT * FROM demo");
        System.out.println("Metadata:");
        System.out.println("================================");
        // fetch metadata
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        System.out.println("Number of columns=" +
                resultSetMetaData.getColumnCount());
        for (int colIdx = 0; colIdx < resultSetMetaData.getColumnCount();
             colIdx++) {
            System.out.println("Column " + colIdx + ": type=" +
                    resultSetMetaData.getColumnTypeName(colIdx + 1));
        }
        // fetch data
        System.out.println("nData:");
        System.out.println("================================");
        int rowIdx = 0;
        while (resultSet.next()) {
            System.out.println("row " + rowIdx + ", column 0: " +
                    resultSet.getString(1));
        }
        statement.close();
    }
}

