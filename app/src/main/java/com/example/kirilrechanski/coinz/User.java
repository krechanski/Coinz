package com.example.kirilrechanski.coinz;

class User {
    public String email;
    public int coinsCollected;
    public int coinsLeft;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, int coinsCollected, int coinsLeft) {
        this.email = email;
        this.coinsCollected = coinsCollected;
        this.coinsLeft = coinsLeft;
    }
}
