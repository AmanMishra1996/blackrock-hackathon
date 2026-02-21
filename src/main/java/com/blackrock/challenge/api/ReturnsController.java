package com.blackrock.challenge.api;

import com.blackrock.challenge.api.dto.ReturnsRequestDTO;
import com.blackrock.challenge.api.dto.ReturnsResponseDTO;
import com.blackrock.challenge.service.ReturnCalculationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class ReturnsController {

    private final ReturnCalculationService service;

    public ReturnsController(ReturnCalculationService service) {
        this.service = service;
    }

    @PostMapping("/returns:nps")
    public ReturnsResponseDTO nps(@Valid @RequestBody ReturnsRequestDTO req) {
        return service.computeNps(req);
    }

    @PostMapping("/returns:index")
    public ReturnsResponseDTO index(@Valid @RequestBody ReturnsRequestDTO req) {
        return service.computeIndex(req);
    }
}