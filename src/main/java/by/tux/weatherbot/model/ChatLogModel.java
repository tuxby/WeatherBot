package by.tux.weatherbot.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "chat_log")
@Data
public class ChatLogModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "chat_id")
    Long chatId;

    @Column(name = "user_id")
    Long userId;

    @Column(name = "userName")
    String userName;

    @Column(name = "request")
    String request;

    @Column(name = "result")
    String result;


}