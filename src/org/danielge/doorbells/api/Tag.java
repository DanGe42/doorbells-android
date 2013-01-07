package org.danielge.doorbells.api;

public class Tag {
    private String id;
    private String location;
    private User user;

    // Useful for Gson
    private Tag() {
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public User getUser() {
        return user;
    }
}
