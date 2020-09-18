package com.darena.automation.util;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import java.io.File;
import java.net.URL;

/**
 *
 * @author diego arena <diego88arena@gmail.com>
 *
 */
public class AppiumServer {

    private static AppiumDriverLocalService server;

    public static void startServer() {
        AppiumServiceBuilder serviceBuilder = new AppiumServiceBuilder();
        serviceBuilder.usingAnyFreePort();
        serviceBuilder.usingDriverExecutable(new File("/usr/local/bin/node"));
        serviceBuilder.withAppiumJS(new File("/usr/local/bin/appium"));
        serviceBuilder.withArgument(GeneralServerFlag.LOG_LEVEL,"error");
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
