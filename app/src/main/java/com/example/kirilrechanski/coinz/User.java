package com.example.kirilrechanski.coinz;

class User {
    public String email;
    public int coinsLeft;
    public double goldAvailable;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, int goldAvailable, int coinsLeft) {
        this.email = email;
        this.coinsLeft = coinsLeft;
        this.goldAvailable = goldAvailable;
    }
}
