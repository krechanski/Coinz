package com.example.kirilrechanski.coinz;

class User {
    public String email;
    public int coinsCollected;
    public double sumCoins;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, int coinsCollected, double sumCoins) {
        this.email = email;
        this.coinsCollected = coinsCollected;
        this.sumCoins = sumCoins;
    }
}
