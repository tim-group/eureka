package com.timgroup.eureka;

import java.io.IOException;

import com.timgroup.eureka.server.EurekaServer;

public final class Eureka {

    public static void main(String[] args) throws IOException {
        new EurekaServer(9444).start();
    }

}
