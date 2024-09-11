package by.tux.weatherbot.utils;

import by.tux.weatherbot.constants.LogDB;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.sql.*;

public class LogDBAppender extends AppenderBase<ILoggingEvent> {
    public static Connection logDbConnection = null;
    public static PreparedStatement statement = null;

    @Override
    protected void append(ILoggingEvent eventObject) {
        try {
            logDbGetConnection();
            String sql = "INSERT INTO " + LogDB.TABLE_NAME + " (timestmp, formatted_message, logger_name, level_string, thread_name, caller_class, caller_method, caller_line) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            statement = logDbConnection.prepareStatement(sql);
            statement.setLong(1, eventObject.getTimeStamp());
            statement.setString(2, eventObject.getFormattedMessage());
            statement.setString(3, eventObject.getLoggerName());
            statement.setString(4, eventObject.getLevel().toString());
            statement.setString(5, eventObject.getThreadName());
            StackTraceElement callerData = eventObject.getCallerData()[0];
            statement.setString(6, callerData.getClassName());
            statement.setString(7, callerData.getMethodName());
            statement.setString(8, String.valueOf(callerData.getLineNumber()));

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (logDbConnection != null) {
                try {
                    logDbConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private String tableDDL =
            "CREATE TABLE IF NOT EXISTS " + LogDB.TABLE_NAME + " ("+
                    "timestmp long, "+
                    "caller_class varchar(255), "+
                    "caller_line varchar(255), "+
                    "caller_method varchar(255), "+
                    "formatted_message varchar(255), "+
                    "level_string varchar(255), "+
                    "logger_name varchar(255), "+
                    "thread_name varchar(255), "+
                    "primary key (timestmp)"+
                    ");";

    public void logDbGetConnection() throws SQLException {
            if (logDbConnection == null || logDbConnection.isClosed()) {
                try {
                    logDbConnection = DriverManager.getConnection("jdbc:sqlite:" + LogDB.DB_NAME, "", "");
                    Statement statement = logDbConnection.createStatement();
                    statement.execute(tableDDL);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
    }
}