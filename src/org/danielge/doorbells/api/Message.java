package org.danielge.doorbells.api;

import java.util.Date;

public class Message {
    private int id;
    private User sender;
    private String contents;
    private int timestamp;

    // Trust me, this is useful for Gson
    private Message() {}

    public int getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public String getContents() {
        return contents;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public Date getDateReceived() {
        return new Date(getTimestamp() * 1000L);
    }
}
