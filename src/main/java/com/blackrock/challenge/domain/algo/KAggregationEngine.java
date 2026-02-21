package com.blackrock.challenge.domain.algo;

import com.blackrock.challenge.domain.DomainKPeriod;

import java.util.List;

public final class KAggregationEngine {

    private KAggregationEngine() {
    }

    public static double[] aggregate(long[] txTimes, double[] finalRem, List<DomainKPeriod> kPeriods) {
        int n = txTimes.length;
        double[] prefix = new double[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + finalRem[i];

        double[] sums = new double[kPeriods.size()];
        for (DomainKPeriod k : kPeriods) {
            int l = lowerBound(txTimes, k.startEpoch());
            int r = upperBound(txTimes, k.endEpoch()) - 1;
            double sum = (l <= r) ? (prefix[r + 1] - prefix[l]) : 0L;
            sums[k.inputIndex()] = sum;
        }
        return sums;
    }

    private static int lowerBound(long[] a, long x) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (a[mid] >= x) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }

    private static int upperBound(long[] a, long x) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (a[mid] > x) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }
}