package eureka;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class CpuUsageFetcher {

    public static void main(String[] args) throws Exception {
        String serverName = "production-ideasfxapp-001_mgmt_pg_net_local";
        String cpuIdle = "collectd." + serverName + ".cpu-0.cpu-idle";
        
        String url = "https://metrics.timgroup.com/render?target=" + cpuIdle + "&format=json&from=-60s";
        
        
        
        System.out.println(call(url));
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
