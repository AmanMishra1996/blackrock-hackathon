package com.blackrock.challenge.service;

import com.blackrock.challenge.api.dto.PPeriodDTO;
import com.blackrock.challenge.api.dto.QPeriodDTO;
import com.blackrock.challenge.api.dto.TransactionDTO;
import com.blackrock.challenge.domain.DomainPPeriod;
import com.blackrock.challenge.domain.DomainQPeriod;
import com.blackrock.challenge.domain.DomainTransaction;
import com.blackrock.challenge.domain.algo.PAdditionEngine;
import com.blackrock.challenge.domain.algo.QOverrideEngine;
import com.blackrock.challenge.util.TimeParser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class TemporalRuleService {

    public List<DomainTransaction> applyQP(List<TransactionDTO> txDtos,
                                           List<QPeriodDTO> qDtos,
                                           List<PPeriodDTO> pDtos) {

        int n = txDtos.size();
        DomainTransaction[] tx = new DomainTransaction[n];
        for (int i = 0; i < n; i++) {
            TransactionDTO t = txDtos.get(i);
            long epoch = TimeParser.toEpochSecond(t.date());
            tx[i] = new DomainTransaction(epoch, t.date(), t.amount(), t.ceiling(), t.remanent());
        }
        Arrays.sort(tx, Comparator.comparingLong(DomainTransaction::epochSec));

        long[] times = new long[n];
        double[] baseRem = new double[n];
        for (int i = 0; i < n; i++) {
            times[i] = tx[i].epochSec();
            baseRem[i] = tx[i].remanent();
        }

        List<DomainQPeriod> q = new ArrayList<>(qDtos.size());
        for (int i = 0; i < qDtos.size(); i++) {
            QPeriodDTO qp = qDtos.get(i);
            long s = TimeParser.toEpochSecond(qp.start());
            long e = TimeParser.toEpochSecond(qp.end());
            q.add(new DomainQPeriod(s, e, qp.fixed(), i));
        }

        List<DomainPPeriod> p = new ArrayList<>(pDtos.size());
        for (PPeriodDTO pp : pDtos) {
            long s = TimeParser.toEpochSecond(pp.start());
            long e = TimeParser.toEpochSecond(pp.end());
            p.add(new DomainPPeriod(s, e, pp.extra()));
        }

        long[] qOverride = QOverrideEngine.computeOverrides(times, q);
        long[] pAdd = PAdditionEngine.computeAdditions(times, p);

        List<DomainTransaction> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            double rem = (qOverride[i] != Long.MIN_VALUE ? qOverride[i] : baseRem[i]) + pAdd[i];
            DomainTransaction t = tx[i];
            out.add(new DomainTransaction(t.epochSec(), t.date(), t.amount(), t.ceiling(), rem));
        }
        return out;
    }
}