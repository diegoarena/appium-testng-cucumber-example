package com.darena.automation.util;

import org.json.simple.parser.JSONParser;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileReader;
import java.util.Iterator;
import java.util.Set;

public class CapabilityReader {

    public static DesiredCapabilities readCapabilities(String capabilityFile) throws Exception{
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(capabilityFile));
        Iterator it = jsonObject.keySet().iterator();
        DesiredCapabilities capabilities = new DesiredCapabilities();
        while(it.hasNext()){
            String capabilityName = it.next().toString();
            capabilities.setCapability(capabilityName, jsonObject.get(capabilityName));
        }
        return capabilities;
    }
}
