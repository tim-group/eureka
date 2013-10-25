package com.timgroup.eureka;

import java.io.IOException;

import com.timgroup.eureka.server.EurekaServer;

public final class Eureka {

    public static void main(String[] args) throws IOException {
        int port = 9444;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        new EurekaServer(port).start();
    }

}
