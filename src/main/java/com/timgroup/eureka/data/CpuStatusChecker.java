package com.timgroup.eureka.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public final class CpuStatusChecker {
    
    final LoadingCache<String, BigDecimal> cpuUsage = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.SECONDS)
        .build(CacheLoader.from(new Function<String, BigDecimal>() {
            @Override
            public BigDecimal apply(String app) {
                return CpuUsageFetcher.fetch(app);
            }
        }));

    public String cpuStatusOf(String app) {
        try {
            BigDecimal usage = cpuUsage.getUnchecked(app);
            String limit = "50";

            if (new BigDecimal(limit).compareTo(usage) < 0) {
                return String.format("CPU usage (%s%%) greater than %s%%", usage.setScale(0, RoundingMode.HALF_UP), limit);
            }
        } catch (Exception ignored) {
            return "N/A";
        }
        return "";
    }

}
