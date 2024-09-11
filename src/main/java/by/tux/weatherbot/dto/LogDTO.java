package by.tux.weatherbot.dto;

import lombok.Data;

@Data
public class LogDTO {

    private long timestmp;
    private String formatted_message;
    private String logger_name;
    private String level_string;
    private String thread_name;
    private String caller_class;
    private String caller_method;
    private String caller_line;

}
