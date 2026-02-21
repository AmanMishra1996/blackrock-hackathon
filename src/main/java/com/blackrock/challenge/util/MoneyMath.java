package com.blackrock.challenge.util;

public final class MoneyMath {
    private MoneyMath() {
    }

    public static double ceilToNext100(double amount) {
        double rem = amount % 100;
        return rem == 0 ? amount : (amount + (100 - rem));
    }

    public static double baseRemanent(double amount) {
        return ceilToNext100(amount) - amount;
    }
}