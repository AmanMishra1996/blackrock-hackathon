package com.blackrock.challenge.domain.returns;

public final class NpsCalculator {
    private NpsCalculator() {
    }

    public static Result compute(double principal, int age, double wage, double inflation) {
        double r = 0.0711;
        int t = (age < 60) ? (60 - age) : 5;

        double A = principal * Math.pow(1.0 + r, t);
        double AReal = A / Math.pow(1.0 + inflation, t);
        double profitsReal = AReal - principal;

        double deduction = Math.min(principal, Math.min((long) (0.1 * wage), 200_000L));
        double taxBenefit = TaxSlabCalculator.tax(wage) - TaxSlabCalculator.tax(wage - deduction);

        return new Result(principal, profitsReal, taxBenefit);
    }

    public record Result(double amount, double profits, double taxBenefit) {
    }
}