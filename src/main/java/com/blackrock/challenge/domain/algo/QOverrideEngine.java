package com.blackrock.challenge.domain.algo;

import com.blackrock.challenge.domain.DomainQPeriod;

import java.util.*;

public final class QOverrideEngine {

    private QOverrideEngine() {
    }

    public static long[] computeOverrides(long[] txTimes, List<DomainQPeriod> qPeriods) {
        int n = txTimes.length;
        long[] override = new long[n];
        Arrays.fill(override, Long.MIN_VALUE);

        if (qPeriods == null || qPeriods.isEmpty() || n == 0) return override;

      record QEvent(long time, boolean isStart, DomainQPeriod p) {
      }

        List<QEvent> events = new ArrayList<>(qPeriods.size() * 2);
        for (DomainQPeriod p : qPeriods) {
            events.add(new QEvent(p.startEpoch(), true, p));
            events.add(new QEvent(p.endEpoch(), false, p));
        }
        events.sort((a, b) -> {
            int c = Long.compare(a.time, b.time);
            if (c != 0) return c;
            if (a.isStart == b.isStart) return 0;
            return a.isStart ? -1 : 1;
        });

        Comparator<DomainQPeriod> cmp = (a, b) -> {
            int c = Long.compare(b.startEpoch(), a.startEpoch());
            if (c != 0) return c;
            return Integer.compare(a.inputIndex(), b.inputIndex());
        };

        TreeSet<DomainQPeriod> active = new TreeSet<>(cmp);

        int e = 0;
        for (int i = 0; i < n; i++) {
            long t = txTimes[i];

            while (e < events.size() && events.get(e).time <= t && events.get(e).isStart) {
                active.add(events.get(e).p);
                e++;
            }


            while (e < events.size() && events.get(e).time < t && !events.get(e).isStart) {
                active.remove(events.get(e).p);
                e++;
            }

            DomainQPeriod chosen = active.isEmpty() ? null : active.first();
            if (chosen != null && t <= chosen.endEpoch()) {
                override[i] = chosen.fixed();
            }

            while (e < events.size() && events.get(e).time == t && !events.get(e).isStart) {
                active.remove(events.get(e).p);
                e++;
            }

        }

        return override;
    }
}