package eureka;

import static com.google.common.collect.Iterables.getLast;
import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CpuUsageFetcher {

    public static void main(String[] args) throws Exception {
        String serverName = "production-ideasfxapp-???_mgmt_pg_net_local";
        String cpuIdleMetricName = "collectd." + serverName + ".cpu-?.cpu-idle";
        
        String url = "https://metrics.timgroup.com/render?target=" + cpuIdleMetricName + "&format=json&from=-60s";
        String data = call(url);
        
        JsonParser parser = new JsonParser();
        JsonArray targets = parser.parse(data).getAsJsonArray();
        for (JsonElement target : targets) {
            JsonObject targetObject = target.getAsJsonObject();
            String targetName = targetObject.get("target").getAsString();
            JsonArray datapoints = targetObject.get("datapoints").getAsJsonArray();
            BigDecimal cpuIdle = Iterables.get(getLast(datapoints).getAsJsonArray(), 0).getAsBigDecimal();
            System.out.println(format("%s, %s", targetName, BigDecimal.ONE.scaleByPowerOfTen(2).subtract(cpuIdle)));
        }
    }

    public static String call(String urlString) throws IOException {
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
