package com.example.kirilrechanski.coinz;

import java.util.List;

class User {
    public String email;
    public int coinsLeft;
    public double goldAvailable;
    public int steps;
    public float totalDistanceWalked;
    public List<String> wallet;
    public List<String> notifications;
    public boolean hasNotification;
    public boolean valueBoosterEnabled;
    public boolean distanceBoosterEnabled;
    public int coinsLeftInBoosterMode;

    public User(String email, int goldAvailable, int coinsLeft, int steps, float totalDistanceWalked,
                List<String> wallet, List<String> notifications, boolean hasNotification, boolean valueBoosterEnabled,
                int coinsLeftInBoosterMode, boolean distanceBoosterEnabled) {
        this.email = email;
        this.coinsLeft = coinsLeft;
        this.goldAvailable = goldAvailable;
        this.steps = steps;
        this.totalDistanceWalked = totalDistanceWalked;
        this.wallet = wallet;
        this.notifications = notifications;
        this.hasNotification = hasNotification;
        this.valueBoosterEnabled = valueBoosterEnabled;
        this.coinsLeftInBoosterMode = coinsLeftInBoosterMode;
        this.distanceBoosterEnabled = distanceBoosterEnabled;
    }
}
