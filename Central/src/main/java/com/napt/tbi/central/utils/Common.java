package com.napt.tbi.central.utils;

import com.napt.framework.ui.utils.Utils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class Common {

    private static Logger log = Logger.getLogger(Common.class);

    /**
     * Gets a resource file with a given name
     *
     * @param fName file name
     * @return resulting File
     */
    public static File getResourceFile(String fPath, String fName) {
        log.debug("file name : " + fName);
        String resourcePath = "/src/main/resources/";
        File fileSource;
        String path = System.getProperty("user.dir");
        // project file path default location
        fileSource = new File(path + resourcePath + fPath + "/" + fName);
        if (fileSource.exists() && !fileSource.isDirectory()) {
            return fileSource;
        }

        // root of resources project dir
        fileSource = new File(path + resourcePath + fName);
        if (fileSource.exists() && !fileSource.isDirectory()) {
            return fileSource;
        }

        // central file path
        File projectDirPath = new File(new File(path).getParent());
        path = projectDirPath.getParent();
        fileSource = new File(path + "/Central" + resourcePath + fPath + "/" + fName);
        if (fileSource.exists() && !fileSource.isDirectory()) {
            return fileSource;
        }

        // root of resources central dir
        fileSource = new File(path + "/Central" + resourcePath + fName);
        if (fileSource.exists() && !fileSource.isDirectory()) {
            return fileSource;
        }

        Assert.fail(fName + " file is not found at " + fileSource);
        return fileSource;
    }

    public static JSONObject getRandomAddress(String addressType) {
        JSONObject address = null;
        try {
            File addressFile = getResourceFile("release11apis", "addresses.json");
            String jsonTxt = Utils.readTextFile(addressFile);
            JSONObject json = new JSONObject(jsonTxt);
            JSONArray addresses = (JSONArray) json.get(addressType);
            Random rand = new Random();
            address = (JSONObject) addresses.get(rand.nextInt(addresses.length()));
            if (address == null) {
                throw new Exception("Unable to find address matching given options");
            }
        } catch (Exception e) {
            Assert.fail("Unable to parse JSON: " + e);
            log.error("Unable to get random address: " + e.getMessage());
        }
        return address;
    }
    public static JSONObject getRandomOrders(String ecomordersType) {
        JSONObject ecomorders = null;
        try {
            File ordersFile = getResourceFile("ecom","eccom_ordertypes.json");
            String jsonTxt = Utils.readTextFile(ordersFile);
            JSONObject json = new JSONObject(jsonTxt);
            JSONArray addresses = (JSONArray) json.get(ecomordersType);
            Random rand = new Random();
            ecomorders = (JSONObject) addresses.get(rand.nextInt(addresses.length()));
            if (ecomorders  == null) {
                throw new Exception("Unable to find orders matching given options");
            }
        } catch (Exception e) {
            Assert.fail("Unable to parse JSON: " + e);
            log.error("Unable to get random address: " + e.getMessage());
        }
        return ecomorders;
    }

    /**
     * @param days
     * @return
     */
    public static String getDate(int days) {
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.mmm");
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        return dateFormat.format(cal.getTime()).replace(" ", "T");
    }

}
