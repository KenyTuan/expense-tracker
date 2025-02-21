package com.test.entity;

import java.io.Serializable;

public class Budget implements Serializable {
    private int month;
    private int amount;

    public Budget(int month, int amount) {
        this.month = month;
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public int getAmount() {
        return amount;
    }
}
