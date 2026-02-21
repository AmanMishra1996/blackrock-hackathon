package com.blackrock.challenge.service;

import com.blackrock.challenge.api.dto.ExpenseDTO;
import com.blackrock.challenge.api.dto.TransactionDTO;
import com.blackrock.challenge.util.MoneyMath;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionParseService {

    public List<TransactionDTO> parse(List<ExpenseDTO> expenses) {
        List<TransactionDTO> out = new ArrayList<>(expenses.size());
        for (ExpenseDTO e : expenses) {
            double ceiling = MoneyMath.ceilToNext100(e.amount());
            double rem = ceiling - e.amount();
            out.add(new TransactionDTO(e.date(), e.amount(), ceiling, rem));
        }
        return out;
    }
}