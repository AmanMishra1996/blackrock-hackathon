package com.blackrock.challenge.domain.returns;

public final class TaxSlabCalculator {
    private TaxSlabCalculator() {}

    public static double tax(double income) {
        if (income <= 700_000) return 0.0;

        double tax = 0.0;

        tax += slab(income, 700_000, 1_000_000, 0.10);


        tax += slab(income, 1_000_000, 1_200_000, 0.15);

        tax += slab(income, 1_200_000, 1_500_000, 0.20);

        if (income > 1_500_000) {
            tax += (income - 1_500_000) * 0.30;
        }

        return tax;
    }

    private static double slab(double income, double start, double end, double rate) {
        if (income <= start) return 0.0;
        double upper = Math.min(income, end);
        return (upper - start) * rate;
    }
}