package com.timgroup.eureka.server.responders;

import java.io.IOException;
import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;

public final class StatusJsonResponder implements Container {

    public StatusJsonResponder() {
    }

    @Override
    public void handle(Request req, Response resp) {
        resp.setValue("Content-Type", "application/json; charset=utf-8");
        resp.setValue("Server", "Eureka/1.0 (Simple 5.1.6)");
        resp.setDate("Date", System.currentTimeMillis());
        resp.setStatus(Status.OK);
        
        try {
            PrintStream printStream = resp.getPrintStream();
            printStream.append("[" + 
                    "    {" + 
                    "        \"name\": \"FX\"," + 
                    "        \"error\": \"1\"" + 
                    "    }," + 
                    "    {" + 
                    "        \"name\": \"FR\"," + 
                    "        \"error\": \"1\"" + 
                    "    }," + 
                    "    {" + 
                    "        \"name\": \"EQ\"," + 
                    "        \"error\": \"1\"" + 
                    "    }," + 
                    "    {" + 
                    "        \"name\": \"TF\"," + 
                    "        \"error\": \"\"" + 
                    "    }," + 
                    "    {" + 
                    "        \"name\": \"PM\"," + 
                    "        \"error\": \"1\"" + 
                    "    }" + 
                    "]");
            printStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
