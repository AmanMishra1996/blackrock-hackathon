package com.blackrock.challenge.domain.returns;

public final class IndexCalculator {
    private IndexCalculator() {
    }

    public static Result compute(double principal, int age, double inflation) {
        double r = 0.1449;
        int t = (age < 60) ? (60 - age) : 5;

        double A = principal * Math.pow(1.0 + r, t);
        double AReal = A / Math.pow(1.0 + inflation, t);
        double profitsReal = AReal - principal;

        return new Result(principal, profitsReal, 0.0);
    }

    public record Result(double amount, double profits, double taxBenefit) {
    }
}