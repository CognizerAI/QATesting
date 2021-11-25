package com.napt.tbi.central.utils;

import com.napt.framework.api.runner.EnvVariables;
import com.napt.framework.api.utils.RequestObject;
import com.napt.framework.api.utils.RestCall;
import com.napt.framework.ui.utils.Utils;
import com.napt.tbi.central.steps.ActiveOmni_API;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;
import java.util.*;

import static com.napt.tbi.central.utils.Common.getResourceFile;

public class OrderHelper {

    private static Logger log = Logger.getLogger(ActiveOmni_API.class);
    public static String webUrl = EnvVariables.getEnvVariables().get("webURL");
    public static Map<String, String> generatedData = new HashMap<>();

    /*
     *  Generate the AuthURL from the WebUrl.
     */
    public static String generateAuthUrl() {
        String[] envName = webUrl.replace(".", " ").split(" ");
        String envPrefix = envName[0].replace("https://", "").replace("http://", "");
        return webUrl.replace(envPrefix, envPrefix + "-auth");
    }

    public static String getOrganizationId(String orderType) {
        String orgId = "";
        if (orderType.contains("MW") || orderType.equals("PT") || orderType.equals("R") || orderType.equals("TCX")) {
            orgId = "TMW";
        } else if (orderType.contains("JB") || orderType.equals("JR") || orderType.equals("JCX")) {
            orgId = "JAB";
        } else if (orderType.contains("JA")) {
            orgId = "JA";
        }
        return orgId;
    }

    /**
     * @param apiType   is used to get the endpoint url
     * @param orderType is used to get the organization type
     * @return it will return the request object
     * @throws Exception when it is not able to create a request object with the given values
     */
    public static RequestObject generateReqObj(String apiType, String orderType) throws Exception {
        String orgType = getOrganizationId(orderType);
        RequestObject req = new RequestObject("central_data_path", "tlrdAoApis.json", apiType);
        String user = orgType.equals("system") ? "ome_username" : "child_user";
        String pass = orgType.equals("system") ? "ome_password" : "child_pass";
        //admin@org.com
        req.setQueryParam("username", EnvVariables.getEnvVariables().get(user).replace("org", orgType));
        req.setQueryParam("password", EnvVariables.getEnvVariables().get(pass));
        req.setAuth("user", EnvVariables.getEnvVariables().get("auth_user"));
        req.setAuth("pass", EnvVariables.getEnvVariables().get("auth_pass"));
        log.info("Request params are passed successfully");
        return req;
    }


    /**
     * @param addType
     * @return
     */
    public static JSONObject addAddress(String addType) {
        JSONObject add = Common.getRandomAddress(addType);
        JSONObject address = new JSONObject();
        address.put("FirstName", generatedData.get("FirstName"));
        address.put("State", add.get("address_state"));
        String phoneNumber = add.get("phone_area_code") + "-" + CustomerData
                .getRandomNumber(0, 1000) + "-" + CustomerData.getRandomNumber(0, 10000);
        generatedData.put("phoneNumber", phoneNumber);
        address.put("Phone", generatedData.get("phoneNumber"));
        address.put("Address2", add.get("address_line_2"));
        address.put("PostalCode", add.get("address_zip_code"));
        address.put("Country", add.get("country_code"));
        address.put("LastName", generatedData.get("LastName"));
        address.put("Address1", add.get("address_line_1"));
        address.put("City", add.get("address_city"));
        return address;
    }

    /**
     * @param lines
     * @param vasIns
     * @return
     */
    public static ArrayList<JSONObject> addLineItems(String[] lines, String vasIns) throws IOException {

        ArrayList<JSONObject> a = new ArrayList<>();
        JSONObject lineItem;
        for (int i = 0; i < lines.length; i++) {
            lineItem = new JSONObject();
            JSONObject json = new JSONObject(Utils.readTextFile(getResourceFile("release11apis","items.json")));
            if ((json.has(lines[i]))) {
                lineItem = (JSONObject) json.get(lines[i]);
                lineItem.put("Extended", addVAS(vasIns));
                lineItem.put("OrderId", generatedData.get("OrderId"));
                lineItem.put("ItemId", lines[i]);
                lineItem.put("FulfillmentLineId", String.valueOf(i + 1));
                lineItem.put("OrderLineId", generatedData.get("OrderLineId") + (i + 1));
            }
            lineItem.put("ItemUnitPrice", "699.99");
            lineItem.put("WeightUOM", "LB");
            lineItem.put("IsHazmat", "false");
            lineItem.put("Extended", addVAS(vasIns));
            lineItem.put("OrderId", generatedData.get("OrderId"));
            lineItem.put("ItemDescription", "CK 2B SV FF TUX");
            lineItem.put("ItemUnitWeight", "1");
            lineItem.put("SupplyTypeId", "OnHand");
            lineItem.put("FulfillmentLineStatusId", "1000.000");
            lineItem.put("ItemColor", "10");
            lineItem.put("ItemId", lines[i]);
            lineItem.put("FulfillmentLineId", String.valueOf(i + 1));
            lineItem.put("OrderLineId", generatedData.get("OrderLineId") + (i + 1));
            lineItem.put("ItemStyle", "3041");
            lineItem.put("PipelineId", "FULFILLMENT_EXECUTION");
            lineItem.put("ItemSize", "432");
            lineItem.put("OrderedQty", "1");
            lineItem.put("QuantityUom", "U");
            a.add(lineItem);
            generatedData.put("ItemId" + i, lines[i]);
        }
        return a;
    }

    /**
     * @param vasIns
     * @return
     */
    public static JSONObject addVAS(String vasIns) {
        JSONObject vas = new JSONObject();
        String vasInstruct = vasIns.equals("Creaset") ? "Creaset" : (vasIns
                .equals("Hem_CF") ? "Plain Inseam 27" : (vasIns
                .equals("NTS") ? "Color: BLACK Font: BLOCK Location: LEFT CUFF ABOVE WATCH Initials: BNP" : "Cuff " +
                "Inseam 26"));
        generatedData.put("vasIns",vasInstruct);
        String vasType = vasIns.equals("Creaset") ? "SC" : (vasIns.equals("Hem_CF") ? "HM" : (vasIns
                .equals("NTS") ? "MG" : "CF"));
        if (!vasIns.equals("No VAS")) {
            vas.put("VasInstruction1", vasInstruct);
            vas.put("VasTypeId1", vasType);
        }
        vas.put("Comments", JSONObject.NULL);
        return vas;
    }

    public static JSONObject addVadInstructionText() {
        JSONObject vasInsText = new JSONObject();
        vasInsText.put("InstructionText", "Creaset");
        return vasInsText;
    }

    /**
     * @param giftMsg
     * @return
     */
    public static String addGiftMsg(String giftMsg) {
        String gift_msg;
        if (giftMsg.equals("false")) {
            gift_msg = "null";
        } else {
            gift_msg = "The gift message will be printed on the packing slip Hurray!!!! :)";
        }
        return gift_msg;
    }

    /**
     * @param lines
     * @param addType
     * @return
     */
    public static JSONObject addFulfillmentPackages(int lines, String addType, String shipType) {
        JSONObject addPackage = new JSONObject();
        addPackage.put("ShipToAddress", addAddress(addType));
        addPackage.put("TrackingNumber", JSONObject.NULL);
        addPackage.put("ShippedDate", JSONObject.NULL);
        JSONObject addPackageDetail = new JSONObject();
        addPackage.put("PackageDetail", addPackageDetail);
        addPackageDetail.put("FulfillmentLineId", String.valueOf(lines + 1));
        addPackageDetail.put("ItemId", generatedData.get("ItemId" + lines));
        addPackageDetail.put("PackageDetailId", JSONObject.NULL);
        addPackageDetail.put("FulfillmentId", generatedData.get("FulfillmentId"));
        JSONObject addQuantity = new JSONObject();
        addPackageDetail.put("Quantity", addQuantity);
        JSONObject addUOM = new JSONObject();
        addQuantity.put("UOM", addUOM);
        addUOM.put("Code", "U");
        addQuantity.put("Qty", "1");
        addPackage.put("ShipFromLocationId", generatedData.get("ShipFrom"));
        if (!shipType.equals("STA")) {
            addPackage.put("ShipToLocationId", generatedData.get("ShipTo"));
        }
        addPackage.put("ServiceLevelCode", generatedData.get("shipMethod"));
        addPackage.put("PackageStatus", "1000.000");
        addPackage.put("PackageTypeId", JSONObject.NULL);
        addPackage.put("FulfillmentId", JSONObject.NULL);
        addPackage.put("CurrentLocationId", generatedData.get("ShipFrom"));
        addPackage.put("ReceiptType", "ReceiveByPackage");
        addPackage.put("PackageId", JSONObject.NULL);
        addPackage.put("DeliveryType", JSONObject.NULL);
        return addPackage;
    }

    /**
     * @return
     */
    public static Map<String, String> fulfillmentStatusCodes() {
        Map<String, String> statusCodes = new HashMap<>();
        //Units have been created and not yet attempted for allocation.
        statusCodes.put("1000.000", "OPEN");
        //Units has been attempted for allocation but failed to get allocation due to inventory unavailability.
        statusCodes.put("1500.000", "Back Ordered");
        //Units have been allocated and inventory has been reserved for it.
        statusCodes.put("2000.000", "ACCEPTED");
        //The units have been picked.
        statusCodes.put("3000.000", "PICKED");
        //The units have been sorted.
        statusCodes.put("3300.000", "SORTED");
        //The fulfillment system has indicated to OM that the units are in-process. Hard-allocated in WM, Accepted by
        // store fulfillment, etc.
        statusCodes.put("3500.000", "IN PACKING");
        //The units have been packed.
        statusCodes.put("4000.000", "PACKED");
        //The units have been partially shipped.
        statusCodes.put("4500.000", "PARTIALLY SHIPPED");
        //The units have been shipped.
        statusCodes.put("5000.000", "SHIPPED");
        //The units have been picked up.
        statusCodes.put("6000.000", "PICKED UP");
        //Units have been fulfilled; this state includes both Shipped and Picked Up units.
        statusCodes.put("7000.000", "Fulfilled");
        //Return The customer has initiated a return, but items have not been received by the retailer.
        statusCodes.put("8000.000", "PENDING");
        //Units have been returned to the retailer
        statusCodes.put("8500.000", "RETURNED");
        //Order line has been canceled
        statusCodes.put("9000.000", "CANCELLED");
        log.info("Returned the Status Name based on Status ID");
        return statusCodes;
    }

    /**
     * @param response
     * @param fulfillmentStatus
     * @return
     */
    public static String verifyResponse(Response response, String fulfillmentStatus) {
        String res = response.getBody().asString();
        int statusCode = response.getStatusCode();
        Assert.assertEquals("Unable to create order due to the status code: " + statusCode, 200, statusCode);
        JSONObject resBody = new JSONObject(res);
        Assert.assertEquals("success message is false", true, resBody.get("success"));
        Assert.assertEquals("statusCode is not OK", "OK", String.valueOf(resBody.get("statusCode")));
        String actualFulfilStatus = (String) resBody.getJSONObject("data").getJSONArray("FulfillmentLine").getJSONObject(0).get("FulfillmentLineStatusId");
        Assert.assertEquals("Expected FulfillmentLineStatusId is not received", fulfillmentStatus, actualFulfilStatus);
        log.info("Verified response successfully");
        return actualFulfilStatus;
    }

    /**
     * @throws Exception
     */
    public static void executeShipmentJob() throws Exception {
        RequestObject req = new RequestObject("central_data_path", "tlrdAoApis.json", "execute_shipment_job");
        req.setHeader("Authorization", "Bearer " + generatedData.get("AccessToken"));
        req.setHeader("Content-Type", "application/json");
        req.setBody(getBatchJobId());
        Response response = RestCall.invoke(webUrl, "JobScheduleId", req);
        int statusCode = response.getStatusCode();
        Assert.assertEquals("Unable to execute shipment Job due to the status code: " + statusCode, 200, statusCode);
        log.info("Executed shipment job for updating the fulfillmentLine Status");
    }

    private static String getBatchJobId() {
        JSONObject batchJobId = new JSONObject();
        batchJobId.put("JobScheduleId", "trackAndShipPackagesJob1");
        return batchJobId.toString();
    }


}
