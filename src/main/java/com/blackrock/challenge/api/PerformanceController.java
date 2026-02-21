package com.blackrock.challenge.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class PerformanceController {

    @GetMapping("/performance")
    public PerformanceDTO performance() {
        Runtime rt = Runtime.getRuntime();
        long used = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        int threads = Thread.getAllStackTraces().size();
        return new PerformanceDTO(0L, used, threads);
    }

    public record PerformanceDTO(long timeMs, long memoryMB, int threads) {
    }
}