package com.example.kirilrechanski.coinz;

public class Coin {

    private int icon;
    private String currency;
    private double value;

    public int getIcon() {
        return icon;
    }

    public String getCurrency() {
        return currency;
    }


    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }


    Coin(String currency, double value) {
        this.currency = currency;
        this.value = value;

        switch (currency) {
            case "DOLR":
                this.icon = R.drawable.green_marker;
                break;

            case "SHIL":
                this.icon = R.drawable.blue_marker;
                break;

            case "PENY":
                this.icon = R.drawable.red_marker;
                break;

            case "QUID":
                this.icon = R.drawable.yellow_marker;
                break;

        }
    }
}
