package by.tux.weatherbot.controller;

import by.tux.weatherbot.constants.LogDB;
import by.tux.weatherbot.dto.LogDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class LogApiController {
    public static Connection logDbConnection = null;
    public static Statement statement = null;

    @RequestMapping(value = "/log")
    public List<LogDTO> GetLogging(@RequestParam(defaultValue = "0") String eventTime) {
        if (eventTime.equals("0")) {
            List<LogDTO> logDTO = GetSomeRecords(400);
            logDTO.sort(Comparator.comparing(LogDTO::getTimestmp));
            return logDTO;
        }
        else{
            List<LogDTO> logDTO = GetLastRecords(Long.parseLong(eventTime));
            return logDTO;
        }
    }

    public void logDbGetConnection() throws SQLException {
        if ( logDbConnection == null  || logDbConnection.isClosed() ){
            logDbConnection = DriverManager.getConnection("jdbc:sqlite:"+ LogDB.DB_NAME, "", "");
            statement = logDbConnection.createStatement();
        }
    }

    public List<LogDTO> GetSomeRecords(long eventCount){
        List<LogDTO> logEvents = new ArrayList<>();
        try {
            logDbGetConnection();
            String tableSql = "SELECT * FROM " + LogDB.TABLE_NAME + " ORDER BY timestmp DESC LIMIT "+ eventCount;
            ResultSet resultSet = statement.executeQuery(tableSql);
            while (resultSet.next()) {
                LogDTO logDTO = new LogDTO();
                logDTO.setTimestmp(resultSet.getLong("timestmp"));
                logDTO.setCaller_class(resultSet.getString("caller_class"));
                logDTO.setCaller_line(resultSet.getString("caller_line"));
                logDTO.setCaller_method(resultSet.getString("caller_method"));
                logDTO.setFormatted_message(resultSet.getString("formatted_message"));
                logDTO.setLevel_string(resultSet.getString("level_string"));
                logDTO.setLogger_name(resultSet.getString("logger_name"));
                logDTO.setThread_name(resultSet.getString("thread_name"));

                logEvents.add(logDTO);
            }
            return logEvents;
        } catch (
                SQLException e) {
            e.printStackTrace();
        } finally {
            if (logDbConnection != null) {
                try {
                    logDbConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return logEvents;
    }

    public List<LogDTO> GetLastRecords(long lastEventTime){
        List<LogDTO> logEvents = new ArrayList<>();
        try {
            logDbGetConnection();
            String tableSql = "SELECT * FROM " + LogDB.TABLE_NAME + " WHERE timestmp > "+lastEventTime + ";";
            ResultSet resultSet = statement.executeQuery(tableSql);
            while (resultSet.next()) {
                LogDTO logDTO = new LogDTO();
                logDTO.setTimestmp(resultSet.getLong("timestmp"));
                logDTO.setCaller_class(resultSet.getString("caller_class"));
                logDTO.setCaller_line(resultSet.getString("caller_line"));
                logDTO.setCaller_method(resultSet.getString("caller_method"));
                logDTO.setFormatted_message(resultSet.getString("formatted_message"));
                logDTO.setLevel_string(resultSet.getString("level_string"));
                logDTO.setLogger_name(resultSet.getString("logger_name"));
                logDTO.setThread_name(resultSet.getString("thread_name"));
                logEvents.add(logDTO);
            }
            return logEvents;
        } catch (
                SQLException e) {
            e.printStackTrace();
        } finally {
            if (logDbConnection != null) {
                try {
                    logDbConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return logEvents;
    }

}
