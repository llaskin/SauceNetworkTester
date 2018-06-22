package com.saucelabs;

import org.apache.commons.cli.*;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {

/**
* Created by llaskin on 6/15/18.
*
* Tester for Sauce Labs customers to use to verify connectivity prior to POC
*
*/
    public static void main(String[] args) throws IOException, InterruptedException {

        Options options = new Options();

        Option username = new Option("u", "username", true, "Sauce Labs " +
                "Desktop username");
        username.setRequired(false);
        options.addOption(username);
        Option accesskey = new Option("a", "accesskey", true, "Sauce Labs " +
                "Desktop access key");
        accesskey.setRequired(false);
        options.addOption(accesskey);
        Option apikey = new Option("t", "apikey", true, "Sauce Labs RDC API Key.  " +
                "Must provide web based API key");
        apikey.setRequired(false);
        options.addOption(apikey);
//        Option proxy = new Option("p", "proxy", true, "Pass proxy info for curl request." +
//                "  In format http://user:password@proxy.example.com or https://user:password@proxy.example.com.(optional)y");
//        proxy.setRequired(false);
//        options.addOption(proxy);
        Option dataCenter = new Option("d", "datacenter", true, "Sauce Labs RDC Data " +
                "Center location");
        dataCenter.setRequired(false);
        options.addOption(dataCenter);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Sauce Network Tester", options);

            System.exit(1);
            return;
        }

        String sauceUsername = cmd.getOptionValue("username");
        String sauceAccessKey = cmd.getOptionValue("accesskey");
        String sauceAPIKey = cmd.getOptionValue("apikey");
        String sauceDataCenter = cmd.getOptionValue("datacenter");


//        test_endpoint("ondemand.saucelabs.com");
        test_endpoint("eu1.appium.testobject.com");
        test_endpoint("us1.appium.testobject.com");

        //return traceroute info for endpoint

        //return endpoint curl
        curlDesktop("http");
        curlDesktop("https");
        curlRDC("http", "EU");
        curlRDC("https", "EU");
        curlRDC("http", "US");
        curlRDC("https", "US");

        //test api connectivity
        testDesktopAPI();
        testRDCApi();

        //connect to Sauce and run super basic test
        if((sauceUsername != null) && (sauceAccessKey != null))
            testDesktopConnection(sauceUsername, sauceAccessKey);
        else
            System.out.println("Skipping Desktop sample test.  No username or accesskey specified");

        String dc = sauceDataCenter;
        if(sauceAPIKey != null) {
            if ((sauceDataCenter.equals("eu")) || (sauceDataCenter.equals("us"))) {
                testRDCConnection(sauceAPIKey, sauceDataCenter);
            } else {
                System.out.println("Skiping RDC, no valid RDC Data Center defined");
            }
        }
        else {
            System.out.println("Skiping RDC, no valid RDC api key defined");
        }
        //test ranges to make sure they are accessible
//        test_ranges();

    }
    private static void test_ranges() throws IOException, InterruptedException {
        boolean fail = false;

        ExecutorService es = Executors.newFixedThreadPool(16);

        int a,b,c,d;
        InetAddress inet;

        a = 162;
        b = 222;
        for(c = 72; c<=79; c++){
            for(d = 1; d < 255; d++){
                Thread.sleep(50);
                inet = InetAddress.getByName(Integer.toString(a) + "." + Integer.toString(b) + "." + Integer.toString(c) + "." + Integer.toString(d));
                if(!inet.isReachable(5000)){
                    System.out.println("Was unable to reach IP" + a + "." + b + "." + c + "." + d);
                    fail = true;
                }
            }
        }

        //66.85.48.0/21
        a = 66;
        b = 85;
        for(c = 48; c<=55; c++){
            for(d = 1; d < 255; d++){
                Thread.sleep(50);
                inet = InetAddress.getByName(Integer.toString(a) + "." + Integer.toString(b) + "." + Integer.toString(c) + "." + Integer.toString(d));
                if(!inet.isReachable(5000)){
                    System.out.println("Was unable to reach IP" + a + "." + b + "." + c + "." + d);
                    fail = true;
                }
            }
        }

        //185.94.24.0/22
        a = 185;
        b = 94;
        for(c = 24; c<=27; c++){
            for(d = 1; d < 255; d++) {
                Thread.sleep(50);
                inet = InetAddress.getByName(Integer.toString(a) + "." + Integer.toString(b) + "." + Integer.toString(c) + "." + Integer.toString(d));
                if (!inet.isReachable(5000)) {
                    System.out.println("Was unable to reach IP" + a + "." + b + "." + c + "." + d);
                    fail = true;
                }
            }
        }

        if(fail){
            System.out.println("Unable to reach all ranges.  Check firewall and proxy settings");
        }




    }
    private static void test_endpoint(String endpoint) throws IOException {
        InetAddress inet = InetAddress.getByName(endpoint);
        if (inet.isReachable(5000)) {
            System.out.println("OK, Able to ping " + endpoint);
        } else {
            System.out.println("Unable to reach " + endpoint);
        }

    }
    private static void curlDesktop(String prefix) {
        try {

            String url = prefix + "://ondemand.saucelabs.com";

            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));

            String alive = reader.readLine().toString();
            if (alive.contains("OK,ondemand alive")) {
                System.out.println("OK, Desktop cloud connectable");
            } else {
                System.out.println("Unable to reach Desktop ondemand, is there a proxy in the way?");
            }
            conn.disconnect();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void curlRDC(String prefix, String dataCenter) {
        try {


            String url = "";
            if(dataCenter.equals("EU")){
                url = prefix + "://eu1.appium.testobject.com/wd/hub/status";
            }
            else if(dataCenter.equals("US")){
                url = prefix + "://us1.appium.testobject.com/wd/hub/status";
            }


            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);

            int resp = conn.getResponseCode();
            if (resp == 200) {
                System.out.println("OK, " + dataCenter + " via " + prefix + " is reachable");
            } else {
                System.out.println("Unable to reach " + dataCenter + " via " + prefix + " , is there a proxy in the way?");
            }
            conn.disconnect();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void testRDCApi(){
        try
        {

            String url = "https://app.testobject.com/api/rest/releaseVersion";
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);

            int resp = conn.getResponseCode();
            if (resp == 200) {
                System.out.println("OK, Sauce Labs RDC API endpoint connectable");
            } else {
                System.out.println("Unable to reach Sauce Labs RDC API endpoint");
            }
            conn.disconnect();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void testDesktopAPI(){
        try
        {

            String url = "https://saucelabs.com/rest/v1/info/status";
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));

            String resp = reader.readLine().toString();
            if (resp.contains("Basic service status checks passed.")) {
                System.out.println("OK, Sauce Labs Desktop API endpoint connectable");
            } else {
                System.out.println("Unable to reach Sauce Labs Desktop API endpoint");
            }
            conn.disconnect();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void testDesktopConnection(String username, String accesskey){
        MutableCapabilities options = new ChromeOptions();
        options.setCapability("version", "latest");
        options.setCapability("platform", "Windows 10");

        MutableCapabilities sauceCaps = new MutableCapabilities();
        sauceCaps.merge(options);
        sauceCaps.setCapability("seleniumVersion", "3.11.0");

        String methodName = "Desktop connection tester";
        sauceCaps.setCapability("name", methodName);

        WebDriver driver = null;
        try {
            String seleniumURI = "@ondemand.saucelabs.com:443";
            driver = new RemoteWebDriver(
                    new URL("https://" + username+ ":" + accesskey + seleniumURI +"/wd/hub"), options);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (driver != null) {
            driver.get("http://www.google.com");
            System.out.println("Ran successful test on Desktop with SessionID" + (((RemoteWebDriver) driver).getSessionId()).toString());
            driver.quit();
        }
        else {
            System.out.println("Desktop test was not successful");
        }
    }
    private static void testRDCConnection(String apiKey, String dataCenter){
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("testobject_api_key", apiKey);
        String methodName = "Desktop Connection Tester";
        caps.setCapability("name", methodName);

        WebDriver driver = null;
        try {
            driver = new RemoteWebDriver(
                    new URL("http://" + dataCenter + "1.appium.testobject.com/wd/hub"),
                    caps);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (driver != null) {
            driver.get("http://www.google.com");
            System.out.println("Ran successful test on RDC with SessionID" + (((RemoteWebDriver) driver).getSessionId()).toString());

            driver.quit();
        }
    }

}
