package com.example.kirilrechanski.coinz;

class User {
    public String email;
    public String username;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email) {
        this.email = email;
    }



}
