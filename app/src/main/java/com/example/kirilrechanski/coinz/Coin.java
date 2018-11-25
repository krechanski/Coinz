package com.example.kirilrechanski.coinz;

public class Coin {
    public String currency;
    public double value;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Coin() {

    }

    public Coin(String currency, double value) {
        this.currency = currency;
        this.value = value;
    }
}
