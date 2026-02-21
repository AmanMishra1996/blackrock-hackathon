package com.blackrock.challenge.service;

import com.blackrock.challenge.api.dto.TransactionDTO;
import com.blackrock.challenge.api.dto.ValidationResponseDTO;
import com.blackrock.challenge.util.MoneyMath;
import com.blackrock.challenge.util.TimeParser;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TransactionValidationService {

    public ValidationResponseDTO<TransactionDTO> validate(double wage, List<TransactionDTO> transactions) {
        ValidationAccumulator<TransactionDTO> acc = new ValidationAccumulator<>();
        Set<Long> seenEpoch = new HashSet<>(Math.max(16, transactions.size() * 2));

        for (TransactionDTO tx : transactions) {
            StringBuilder err = new StringBuilder();

            Long epoch = tryParseEpoch(tx, err);
            validateAmounts(tx, err);
            validateCeilingAndRemanent(tx, err);

            if (epoch != null) {
                if (!seenEpoch.add(epoch)) {
                    appendErr(err, "Duplicate transaction timestamp (same date/time).");
                }
            }

            if (err.length() == 0) acc.addValid(tx);
            else acc.addInvalid(tx, err.toString());
        }

        return acc.build();
    }

    private Long tryParseEpoch(TransactionDTO tx, StringBuilder err) {
        try {
            return TimeParser.toEpochSecond(tx.date());
        } catch (Exception e) {
            appendErr(err, "Invalid date format. Expected 'YYYY-MM-DD HH:mm:ss'.");
            return null;
        }
    }

    private void validateAmounts(TransactionDTO tx, StringBuilder err) {
        if (tx.amount() < 0) appendErr(err, "amount must be >= 0.");
        if (tx.ceiling() < 0) appendErr(err, "ceiling must be >= 0.");
        if (tx.remanent() < 0) appendErr(err, "remanent must be >= 0.");
    }

    private void validateCeilingAndRemanent(TransactionDTO tx, StringBuilder err) {
        double amount = tx.amount();
        double ceiling = tx.ceiling();
        double rem = tx.remanent();

        if (ceiling < amount) appendErr(err, "ceiling must be >= amount.");
        if (ceiling % 100 != 0) appendErr(err, "ceiling must be a multiple of 100.");

        double expectedCeiling = MoneyMath.ceilToNext100(amount);
        if (ceiling != expectedCeiling) {
            appendErr(err, "ceiling mismatch. Expected " + expectedCeiling + " for amount " + amount + ".");
        }

        double expectedRem = ceiling - amount;
        if (rem != expectedRem) {
            appendErr(err, "remanent mismatch. Expected (ceiling - amount) = " + expectedRem + ".");
        }
    }

    private void appendErr(StringBuilder sb, String msg) {
        if (sb.length() > 0) sb.append(" ");
        sb.append(msg);
    }
}