package com.pulseras.api.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record AggregatedOverview (
        List<DailyOverview> weekly,   // 7 phần tử
        List<DailyOverview>   monthly,  // 28‑31 phần tử
        List<MonthlyOverview> yearly    // 12 phần tử
) {}