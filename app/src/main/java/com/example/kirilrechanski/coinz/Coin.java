package com.example.kirilrechanski.coinz;

import com.mapbox.mapboxsdk.annotations.Icon;

public class Coin {

    private int icon;
    private String currency;
    private double value;

    public int getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
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
