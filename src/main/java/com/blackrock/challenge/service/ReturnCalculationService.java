package com.blackrock.challenge.service;

import com.blackrock.challenge.api.dto.KPeriodDTO;
import com.blackrock.challenge.api.dto.KReturnDTO;
import com.blackrock.challenge.api.dto.ReturnsRequestDTO;
import com.blackrock.challenge.api.dto.ReturnsResponseDTO;
import com.blackrock.challenge.domain.DomainTransaction;
import com.blackrock.challenge.domain.returns.IndexCalculator;
import com.blackrock.challenge.domain.returns.NpsCalculator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReturnCalculationService {

    private final TemporalRuleService temporalRuleService;
    private final KWindowAggregationService kWindowAggregationService;

    public ReturnCalculationService(TemporalRuleService temporalRuleService,
                                    KWindowAggregationService kWindowAggregationService) {
        this.temporalRuleService = temporalRuleService;
        this.kWindowAggregationService = kWindowAggregationService;
    }

    public ReturnsResponseDTO computeNps(ReturnsRequestDTO req) {
        List<DomainTransaction> tx = temporalRuleService.applyQP(req.transactions(), req.q(), req.p());
        double[] sums = kWindowAggregationService.sumByK(tx, req.k());

        double totalAmount = tx.stream().mapToDouble(DomainTransaction::amount).sum();
        double totalCeil = tx.stream().mapToDouble(DomainTransaction::ceiling).sum();

        List<KReturnDTO> out = new ArrayList<>(req.k().size());
        for (int i = 0; i < req.k().size(); i++) {
            KPeriodDTO k = req.k().get(i);
            double principal = sums[i];
            NpsCalculator.Result r = NpsCalculator.compute(principal, req.age(), req.wage(), req.inflation());
            out.add(new KReturnDTO(k.start(), k.end(), r.amount(), r.profits(), r.taxBenefit()));
        }
        return new ReturnsResponseDTO(totalAmount, totalCeil, out);
    }

    public ReturnsResponseDTO computeIndex(ReturnsRequestDTO req) {
        List<DomainTransaction> tx = temporalRuleService.applyQP(req.transactions(), req.q(), req.p());
        double[] sums = kWindowAggregationService.sumByK(tx, req.k());

        double totalAmount = tx.stream().mapToDouble(DomainTransaction::amount).sum();
        double totalCeil = tx.stream().mapToDouble(DomainTransaction::ceiling).sum();

        List<KReturnDTO> out = new ArrayList<>(req.k().size());
        for (int i = 0; i < req.k().size(); i++) {
            KPeriodDTO k = req.k().get(i);
            double principal = sums[i];
            IndexCalculator.Result r = IndexCalculator.compute(principal, req.age(), req.inflation());
            out.add(new KReturnDTO(k.start(), k.end(), r.amount(), r.profits(), 0.0));
        }
        return new ReturnsResponseDTO(totalAmount, totalCeil, out);
    }
}