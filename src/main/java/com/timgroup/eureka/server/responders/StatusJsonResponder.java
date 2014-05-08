package com.timgroup.eureka.server.responders;

import java.io.IOException;
import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.timgroup.eureka.data.CpuStatusChecker;

public final class StatusJsonResponder implements Container {
    
    private final CpuStatusChecker cpuStatusChecker;

    public StatusJsonResponder() {
        cpuStatusChecker = new CpuStatusChecker();
    }

    @Override
    public void handle(Request req, Response resp) {
        resp.setValue("Content-Type", "application/json; charset=utf-8");
        resp.setValue("Server", "Eureka/1.0 (Simple 5.1.6)");
        resp.setDate("Date", System.currentTimeMillis());
        resp.setStatus(Status.OK);
        
        ImmutableList<String> apps = ImmutableList.of("FX", "FR", "EQ", "AD", "M$", "BS");
        JsonArray status = new JsonArray();
        for (String app : apps) {
            JsonObject appStatus = new JsonObject();
            appStatus.addProperty("name", app);
            appStatus.addProperty("error", cpuStatusChecker.cpuStatusOf(app));
            status.add(appStatus);
        }

        try {
            PrintStream printStream = resp.getPrintStream();
            new Gson().toJson(status, Streams.writerForAppendable(printStream));
            printStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
