package com.darena.automation.util;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

/**
 *
 */
public class AppiumServer {

    private static AppiumDriverLocalService server;

    public static void startServer() {
        AppiumServiceBuilder serviceBuilder = new AppiumServiceBuilder();
        serviceBuilder.usingAnyFreePort();
        serviceBuilder.usingDriverExecutable(new File("/usr/local/bin/node"));
        serviceBuilder.withAppiumJS(new File("/usr/local/bin/appium"));
        server = AppiumDriverLocalService.buildService(serviceBuilder);
        server.start();
    }

    public static void stopServer(){
        server.stop();
    }

    public static URL getServerUrl(){
        return server.getUrl();
    }
}
