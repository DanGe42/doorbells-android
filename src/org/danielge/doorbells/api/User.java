package org.danielge.doorbells.api;

public class User {
    private String name;

    // I know this looks like a useless class, but it's useful for Gson
    private User() {}

    public String getName() {
        return name;
    }
}
