package com.blackrock.challenge.service;

import com.blackrock.challenge.api.dto.*;
import com.blackrock.challenge.domain.DomainTransaction;
import com.blackrock.challenge.util.TimeParser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionFilterService {

    private final TemporalRuleService temporalRuleService;

    public TransactionFilterService(TemporalRuleService temporalRuleService) {
        this.temporalRuleService = temporalRuleService;
    }

    public ValidationResponseDTO<TransactionDTO> filter(FilterRequestDTO req) {
        validatePeriodsOrThrow(req.q(), req.p(), req.k());

        ValidationAccumulator<TransactionDTO> acc = new ValidationAccumulator<>();
        List<TransactionDTO> validTx = new ArrayList<>(req.transactions().size());

        for (TransactionDTO tx : req.transactions()) {
            StringBuilder err = new StringBuilder();

            tryParseEpoch(tx, err);
            if (tx.amount() < 0) appendErr(err, "amount must be >= 0.");
            if (tx.ceiling() < 0) appendErr(err, "ceiling must be >= 0.");
            if (tx.ceiling() % 100 != 0) appendErr(err, "ceiling must be a multiple of 100.");
            if (tx.ceiling() < tx.amount()) appendErr(err, "ceiling must be >= amount.");
            if (tx.remanent() < 0) appendErr(err, "remanent must be >= 0.");

            if (err.length() == 0) {
                validTx.add(tx);
            } else {
                acc.addInvalid(tx, err.toString());
            }
        }

        List<DomainTransaction> finalTx = temporalRuleService.applyQP(validTx, req.q(), req.p());

        for (DomainTransaction t : finalTx) {
            acc.addValid(new TransactionDTO(t.date(), t.amount(), t.ceiling(), t.remanent()));
        }

        return acc.build();
    }

    private void validatePeriodsOrThrow(List<QPeriodDTO> q, List<PPeriodDTO> p, List<KPeriodDTO> k) {
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < q.size(); i++) {
            QPeriodDTO qp = q.get(i);
            validatePeriod("q[" + i + "]", qp.start(), qp.end(), errors);
            if (qp.fixed() < 0) errors.add("q[" + i + "].fixed must be >= 0.");
        }

        for (int i = 0; i < p.size(); i++) {
            PPeriodDTO pp = p.get(i);
            validatePeriod("p[" + i + "]", pp.start(), pp.end(), errors);
            if (pp.extra() < 0) errors.add("p[" + i + "].extra must be >= 0.");
        }

        for (int i = 0; i < k.size(); i++) {
            KPeriodDTO kp = k.get(i);
            validatePeriod("k[" + i + "]", kp.start(), kp.end(), errors);
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(" ", errors));
        }
    }

    private void validatePeriod(String label, String start, String end, List<String> errors) {
        long s, e;
        try {
            s = TimeParser.toEpochSecond(start);
        } catch (Exception ex) {
            errors.add(label + ".start invalid format (expected 'YYYY-MM-DD HH:mm:ss').");
            return;
        }
        try {
            e = TimeParser.toEpochSecond(end);
        } catch (Exception ex) {
            errors.add(label + ".end invalid format (expected 'YYYY-MM-DD HH:mm:ss').");
            return;
        }
        if (s > e) errors.add(label + " has start > end.");
    }

    private void tryParseEpoch(TransactionDTO tx, StringBuilder err) {
        try {
            TimeParser.toEpochSecond(tx.date());
        } catch (Exception e) {
            appendErr(err, "Invalid date format. Expected 'YYYY-MM-DD HH:mm:ss'.");
        }
    }

    private void appendErr(StringBuilder sb, String msg) {
        if (sb.length() > 0) sb.append(" ");
        sb.append(msg);
    }
}