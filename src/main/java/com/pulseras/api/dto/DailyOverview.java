package com.pulseras.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
public record DailyOverview (
        String label,          // "T2" … "T7" / "CN" / "1" … "31"
        long   orderCount,
        BigDecimal revenue
) {}
