package com.blackrock.challenge.service;

import com.blackrock.challenge.api.dto.KPeriodDTO;
import com.blackrock.challenge.domain.DomainKPeriod;
import com.blackrock.challenge.domain.DomainTransaction;
import com.blackrock.challenge.domain.algo.KAggregationEngine;
import com.blackrock.challenge.util.TimeParser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KWindowAggregationService {

    public double[] sumByK(List<DomainTransaction> sortedTx, List<KPeriodDTO> kDtos) {
        int n = sortedTx.size();
        long[] times = new long[n];
        double[] rem = new double[n];

        for (int i = 0; i < n; i++) {
            times[i] = sortedTx.get(i).epochSec();
            rem[i] = sortedTx.get(i).remanent();
        }

        List<DomainKPeriod> kPeriods = new ArrayList<>(kDtos.size());
        for (int i = 0; i < kDtos.size(); i++) {
            KPeriodDTO kd = kDtos.get(i);
            long s = TimeParser.toEpochSecond(kd.start());
            long e = TimeParser.toEpochSecond(kd.end());
            kPeriods.add(new DomainKPeriod(s, e, i, kd.start(), kd.end()));
        }

        return KAggregationEngine.aggregate(times, rem, kPeriods);
    }
}