package com.blackrock.challenge.domain.algo;

import com.blackrock.challenge.domain.DomainPPeriod;

import java.util.List;

public final class PAdditionEngine {

    private PAdditionEngine() {
    }

    public static long[] computeAdditions(long[] txTimes, List<DomainPPeriod> pPeriods) {
        int n = txTimes.length;
        long[] add = new long[n];
        if (pPeriods == null || pPeriods.isEmpty() || n == 0) return add;

        long[] diff = new long[n + 1];

        for (DomainPPeriod p : pPeriods) {
            int l = lowerBound(txTimes, p.startEpoch());
            int r = upperBound(txTimes, p.endEpoch()) - 1;
            if (l <= r) {
                diff[l] += p.extra();
                diff[r + 1] -= p.extra();
            }
        }

        long run = 0;
        for (int i = 0; i < n; i++) {
            run += diff[i];
            add[i] = run;
        }
        return add;
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