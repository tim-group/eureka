package com.timgroup.eureka.data;

import java.math.BigDecimal;
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
        if (new BigDecimal("50").compareTo(cpuUsage.getUnchecked(app)) < 0) {
            return "AGHH";
        }
        return "";
    }

}
