package com.abby.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * server端向client端發送的物件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutMessage {

    private String from;

    private String content;

    private Date time = new Date();


    public OutMessage(String content) {
        this.content = content;
    }
}

