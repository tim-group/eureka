package com.timgroup.eureka.data;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.transform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CpuUsageFetcher {

    private static final ImmutableMap<String, String> TARGETS = ImmutableMap.of(
            "FX", "production-ideasfxapp-???_mgmt_pg_net_local",
            "EQ", "pg-timapp-???_pgldn_youdevise_com",
            "PM", "pg-dpmapp-???_mgmt_pg_net_local",
            "FR", "pg-frapp-???_mgmt_pg_net_local");

    public static BigDecimal fetch(String app) {
        return Ordering.<BigDecimal>natural().max(fetchFor(TARGETS.get(app)));
    }

    private static List<BigDecimal> fetchFor(String serverName)  {
        String cpuIdleMetricName = "collectd." + serverName + ".cpu-*.cpu-idle";

        String url = "https://metrics.timgroup.com/render?target=" + cpuIdleMetricName + "&format=json&from=-60s";
        String data = call(url);

        JsonParser parser = new JsonParser();
        JsonArray targets = parser.parse(data).getAsJsonArray();

        List<BigDecimal> cpuUsages = Lists.newArrayList();
        for (JsonElement target : targets) {
            JsonObject targetObject = target.getAsJsonObject();
            JsonArray datapoints = targetObject.get("datapoints").getAsJsonArray();

            try {
                BigDecimal cpuIdle = getLast(transform(filter(transform(datapoints, toCpuUsageElement()), not(jsonNull())), toBigDecimal()));
                BigDecimal cpuUsage = BigDecimal.ONE.scaleByPowerOfTen(2).subtract(cpuIdle);
                cpuUsages.add(cpuUsage);
            } catch (NoSuchElementException e) {
                // System.out.println("No value available for target " + target.toString());
            }
        }
        return cpuUsages;
    }

    private static Function<JsonElement, BigDecimal> toBigDecimal() {
        return new Function<JsonElement, BigDecimal>() {
            @Override public BigDecimal apply(JsonElement input) {
                return input.getAsBigDecimal();
            }
        };
    }

    private static Predicate<JsonElement> jsonNull() {
        return new Predicate<JsonElement>() {
            @Override public boolean apply(JsonElement input) {
                return input.isJsonNull();
            }
        };
    }

    private static Function<JsonElement, JsonElement> toCpuUsageElement() {
        return new Function<JsonElement, JsonElement>() {
            @Override public JsonElement apply(JsonElement input) {
                return input.getAsJsonArray().get(0);
            }
        };
    }

    public static String call(String urlString) {
        try {
            final URL url = new URL(urlString);
            final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setInstanceFollowRedirects(false);
            final int responseCode = conn.getResponseCode();
            final BufferedReader in = new BufferedReader(new InputStreamReader(responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream()));

            final StringBuilder responseText = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseText.append(inputLine);
            }
            in.close();
            return responseText.toString();
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}

//"collectd.production-ideasfxapp-001_mgmt_pg_net_local.cpu-0.cpu-system"
//"collectd.production-ideasfxapp-001_mgmt_pg_net_local.cpu-0.cpu-user"
