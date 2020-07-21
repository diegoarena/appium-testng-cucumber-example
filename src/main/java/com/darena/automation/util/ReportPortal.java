package com.darena.automation.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Logger;

public class ReportPortal {

    private static Logger logger = Logger.getLogger(ReportPortal.class.getName());
    private String endPointUrl;
    private String apiToken;
    private String launchName;
    private String projectName;
    private String launchDescription;
    private final String REPORT_PORTAL_API_VERSION = "v1";


    public ReportPortal(String reportPortalConfigFile) throws ConfigurationException {
        Configuration config = new Configurations().properties(reportPortalConfigFile);
        endPointUrl = config.getString("rp.endpoint");
        apiToken = config.getString("rp.uuid");
        launchName = config.getString("rp.launch");
        projectName = config.getString("rp.project");
        launchDescription = config.getString("rp.launch.description");
    }

    /**
     * Create a launch
     *
     * @return
     * @throws Exception
     */
    public JSONObject startLaunch(String launchStartDate, Map<String, String> attributesMap) throws Exception {
        //body
        JSONObject body = new JSONObject();
        body.put("name", launchName);
        body.put("description", launchDescription);
        body.put("startTime", launchStartDate);
        body.put("mode", "DEFAULT");
        // test attributes
        JSONArray attributes = new JSONArray();
        if (attributesMap != null)
            for (Map.Entry<String, String> entry : attributesMap.entrySet()) {
                JSONObject testAttribute = new JSONObject();
                testAttribute.put("key", entry.getKey());
                testAttribute.put("value", entry.getValue());
                attributes.add(testAttribute);
            }
        if (!attributes.isEmpty()) {
            body.put("attributes", attributes);
        }
        //execute request
        return callReportPortalApiService(endPointUrl + "/api/v1/" + projectName + "/launch", "post", new StringEntity(body.toJSONString()), 201);
    }

    /**
     * Finish launch
     *
     * @param launchUuid
     * @throws Exception
     */
    public void finishLaunch(String launchUuid, String launchEndTime) throws Exception {
        //body
        JSONObject body = new JSONObject();
        body.put("endTime", launchEndTime);
        //execute request
        callReportPortalApiService(endPointUrl + "/api/v1/" + projectName + "/launch/" + launchUuid + "/finish", "put", new StringEntity(body.toJSONString()), 200);
    }

    /**
     * Create an item
     *
     * @return
     * @throws Exception
     */
    public JSONObject startItem(String launchUuid, String itemParentId, String itemName, String itemType, String itemStartDate, Map<String, String> extraFields, Map<String, String> attributes) throws Exception {
        //body
        JSONObject body = new JSONObject();
        body.put("name", itemName);
        body.put("startTime", itemStartDate);
        body.put("type", itemType);
        body.put("launchUuid", launchUuid);
        //add extra fields
        if (extraFields != null) {
            body.putAll(extraFields);
        }
        // test attributes
        JSONArray itemAttributes = new JSONArray();
        if (attributes != null) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                JSONObject testAttribute = new JSONObject();
                testAttribute.put("key", entry.getKey());
                testAttribute.put("value", entry.getValue());
                itemAttributes.add(testAttribute);
            }
            if (!attributes.isEmpty()) {
                body.put("attributes", itemAttributes);
            }
        }
        //execute request
        String serviceUrl = endPointUrl + "/api/v1/" + projectName + "/item";
        if (itemParentId != null) {
            serviceUrl += "/" + itemParentId;
        }
        return callReportPortalApiService(serviceUrl, "post", new StringEntity(body.toJSONString()), 201);
    }

    /**
     * Finish an item
     *
     * @param suiteId
     * @throws Exception
     */
    public void finishItem(String launchUuid, String suiteId, String suiteEndTime, String status) throws Exception {
        //body
        JSONObject body = new JSONObject();
        body.put("endTime", suiteEndTime);
        body.put("launchUuid", launchUuid);
        if (status != null)
            body.put("status", status);
        //execute report portal api
        //System.out.println("-------- "+body.toJSONString());
        JSONObject apiResponse = callReportPortalApiService(endPointUrl + "/api/v1/" + projectName + "/item/" + suiteId, "put", new StringEntity(body.toJSONString()), 200);
    }

    /**
     * Add a log item
     *
     * @throws Exception
     */
    public void addLog(String launchUuid, String itemUuid, String logTime, File logAttachment, String logMessage) throws Exception {
        //body to create test
        MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        JSONArray requestBodyAttachments = new JSONArray();
        JSONArray logsJson = new JSONArray();
        // json
        JSONObject logFileJsonItem = new JSONObject();
        logFileJsonItem.put("itemUuid", itemUuid);
        logFileJsonItem.put("launchUuid", launchUuid);
        logFileJsonItem.put("time", logTime);
        logFileJsonItem.put("level", 40000);
        logFileJsonItem.put("message", logMessage);
        requestBodyAttachments.add(logFileJsonItem);
        //add log attachment file
        if (logAttachment != null && logAttachment.exists()) {
            JSONObject logFile = new JSONObject();
            logFile.put("name", logAttachment.getName());
            logFileJsonItem.put("file", logFile);
            logsJson.add(logFile);
            //add multipart file
            builder.addBinaryBody("file", logAttachment, ContentType.create(URLConnection.guessContentTypeFromName(logAttachment.getName())), logAttachment.getName());
        }
        File jsonBodyFile = new File("body.json");
        FileUtils.writeStringToFile(jsonBodyFile, requestBodyAttachments.toJSONString());
        builder.addBinaryBody("json_request_part", jsonBodyFile, ContentType.APPLICATION_JSON, "body.json");
        //execute request to create test result
        JSONObject apiResponse = callReportPortalApiService(endPointUrl + "/api/v1/" + projectName + "/log", "post", builder.build(), 201);
        jsonBodyFile.delete();
    }

    /**
     * Generic method to call report portal service
     *
     * @param serviceUrl
     * @param method
     * @param body
     * @param expectedCode
     * @return
     * @throws Exception
     */
    private JSONObject callReportPortalApiService(String serviceUrl, String method, HttpEntity body, Integer expectedCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpEntityEnclosingRequestBase request = method.equalsIgnoreCase("post") ? new HttpPost(serviceUrl) : new HttpPut(serviceUrl);
        //header
        request.addHeader("Authorization", "Bearer " + apiToken);
        if (!body.getContentType().getValue().contains("multipart/form-data")) {
            request.setHeader("Content-Type", "application/json");
        }
        //body
        request.setEntity(body);
        //response
        CloseableHttpResponse response = client.execute(request);
        if (response.getStatusLine().getStatusCode() != expectedCode) {
            throw new Exception("There was an error executing service " + serviceUrl + ": Error " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
        }
        JSONObject jsonResponse = (JSONObject) new JSONParser().parse(EntityUtils.toString(response.getEntity()));
        return jsonResponse;
    }

    /**
     * Builds launch url
     *
     * @param reportNumber
     * @return
     */
    public String getLaunchUrl(String reportNumber) {
        return endPointUrl + "ui/#" + projectName + "/launches/all/" + reportNumber;
    }

}
