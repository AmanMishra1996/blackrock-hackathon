package com.blackrock.challenge.api;

import com.blackrock.challenge.api.dto.*;
import com.blackrock.challenge.service.TransactionFilterService;
import com.blackrock.challenge.service.TransactionParseService;
import com.blackrock.challenge.service.TransactionValidationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class TransactionsController {

    private final TransactionParseService parseService;
    private final TransactionValidationService validationService;
    private final TransactionFilterService filterService;

    public TransactionsController(TransactionParseService parseService,
                                  TransactionValidationService validationService,
                                  TransactionFilterService filterService) {
        this.parseService = parseService;
        this.validationService = validationService;
        this.filterService = filterService;
    }

    @PostMapping("/transactions:parse")
    public List<TransactionDTO> parse(@Valid @RequestBody List<ExpenseDTO> expenses) {
        return parseService.parse(expenses);
    }

    @PostMapping("/transactions:validator")
    public ValidationResponseDTO<TransactionDTO> validate(@RequestBody ValidatorRequestDTO req) {
        return validationService.validate(req.wage(), req.transactions());
    }

    @PostMapping("/transactions:filter")
    public ValidationResponseDTO<TransactionDTO> filter(@Valid @RequestBody FilterRequestDTO req) {
        return filterService.filter(req);
    }
}