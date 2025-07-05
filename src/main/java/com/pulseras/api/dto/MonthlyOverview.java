package com.pulseras.api.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MonthlyOverview (
        String label,          // "T1" … "T12"
        long   orderCount,
        BigDecimal revenue
) {}
