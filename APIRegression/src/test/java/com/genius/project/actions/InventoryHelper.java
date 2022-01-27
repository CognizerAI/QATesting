package com.genius.project.actions;

import com.napt.framework.api.utils.FileUtils;
import com.napt.framework.api.utils.RequestObject;
import com.napt.framework.api.utils.RestCall;
import com.napt.tbi.central.utils.Common;
import com.napt.tbi.central.utils.CustomerData;
import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.napt.tbi.central.utils.Common.getDate;
import static com.napt.tbi.central.utils.Common.getResourceFile;
import static com.napt.tbi.central.utils.CustomerData.*;
import static com.napt.tbi.central.utils.OrderHelper.*;

public class InventoryHelper {

    //adde on 27th Jan
    private static Logger log = Logger.getLogger(InventoryHelper.class);
    private static Response response;
    public static Map<String, String> generatedInfo = new HashMap<>();

    /**
     * @param itemId
     * @param locationId
     * @param viewName
     * @return
     * @throws Exception
     */
    public JSONArray checkInventory(String itemId, String locationId, String viewName) throws Exception {
        RequestObject req = new RequestObject("central_path_r12_apis", "ApisR12.json", "check_inventory");
        req.setBody(createCheckInventoryJson(itemId, locationId, viewName));
        req.setHeader("Authorization", "Bearer " + generatedData.get("AccessToken"));
        req.setHeader("Content-Type", "application/json");
        response = RestCall.invoke(webUrl, "GetSupply", req);
        String resBody = response.getBody().asString();
        log.info("checkInventory response: " + resBody);
        int statusCode = response.getStatusCode();
        Assert.assertEquals("Unable to do Auth Api call due to the status code: " + statusCode, 200, statusCode);
        log.info("Supply Inventory info is received for Item/s: " + itemId);
        return new JSONObject(resBody).getJSONObject("data").getJSONArray("supplyResponseDetailDTOList");
    }

    private static String createCheckInventoryJson(String itemId, String locationId, String viewName) throws IOException {
        File filePath = getResourceFile("release12apis", "Check_Inventory.json");
        JSONObject checkInventoryBody = new JSONObject(FileUtils.readFile(filePath));
        if (locationId.isEmpty() && viewName.isEmpty()) {
            checkInventoryBody.remove("Locations");
            checkInventoryBody.remove("ViewId");
        } else if (viewName.isEmpty()) {
            checkInventoryBody.getJSONArray("Locations").put(0, locationId);
            checkInventoryBody.remove("ViewId");
        } else {
            checkInventoryBody.getJSONArray("Locations").put(0, locationId);
            checkInventoryBody.put("ViewId", viewName);
        }
        checkInventoryBody.getJSONArray("Items").put(0, itemId);
        log.info("created the Json body with the given info of item/s: " + itemId + ",Location/s: " + locationId + "and ViewName: " + viewName);
        return checkInventoryBody.toString();
    }

    public void addOrRemoveInventory(String itemId, String locationId, Double quantity, String qtyAdType) throws Exception {
        RequestObject req = new RequestObject("central_path_r12_apis", "ApisR12.json", "add_remove_inventory");
        File filePath = getResourceFile("release12apis", "Add_Remove_Inventory.json");
        JSONObject addRemoveBody = new JSONObject(FileUtils.readFile(filePath));
        addRemoveBody.getJSONArray("SupplyEvent").getJSONObject(0).getJSONObject("SupplyDefinition").put("ItemId", itemId);
        addRemoveBody.getJSONArray("SupplyEvent").getJSONObject(0).getJSONObject("SupplyDefinition").put("LocationId", locationId);
        addRemoveBody.getJSONArray("SupplyEvent").getJSONObject(0).getJSONObject("SupplyDefinition").getJSONObject("SupplyData").put("Quantity", quantity);
        addRemoveBody.getJSONArray("SupplyEvent").getJSONObject(0).getJSONObject("SupplyDefinition").getJSONObject("SupplyData").put("QuantityAdjustmentType", qtyAdType.equals("add") ? "A" : "S");
        req.setBody(addRemoveBody.toString());
        req.setHeader("Authorization", "Bearer " + generatedData.get("AccessToken"));
        req.setHeader("Content-Type", "application/json");
        response = RestCall.invoke(webUrl, "SupplyEvent", req);
        int statusCode = response.getStatusCode();
        Assert.assertEquals("Unable to do Auth Api call due to the status code: " + statusCode, 200, statusCode);
        log.info(qtyAdType + "ed inventory for Item/s: " + itemId + ",Location/s: " + locationId);
    }

    public void createOrderR12Api(String apiType, DataTable table) throws Exception {
        RequestObject req = new RequestObject("central_path_r12_apis", "ApisR12.json", apiType);
        req.setBody(updateOrderJson(table));
        req.setHeader("Authorization", "Bearer " + generatedData.get("AccessToken"));
        req.setHeader("Content-Type", "application/json");
        response = RestCall.invoke(webUrl, "placeOrder", req);
        verifyOrderResponse(response, "1000");
        log.info("Created " + table.row(1).get(0) + "order with updated values using create API");
    }

    private static String updateOrderJson(DataTable table) throws IOException {
        // Store the detail from the datatable
        String orderType = table.row(1).get(0);
        String shipType = table.row(1).get(1);
        String shipMType = table.row(1).get(2);
        String addType = table.row(1).get(3);
        String[] lines = table.row(1).get(4).split(",");
        String[] location = table.row(1).get(5).split("-");
        generatedInfo.put("ShipFrom", location[0]);
        generatedInfo.put("ShipTo", location[1].replace("Add", "null"));

        //Generating Random order, fulfillment, name and email
        generatedInfo.put("Email", getRandomEmail());
        String orderNumber = getOrganizationId(orderType) + "_" + getDate(0).replace("-", "_").replace("T", "_").replace(":", "_").replace(".", "_");
        generatedInfo.put("OrderNumber", orderNumber);
        generatedInfo.put("FirstName", getRandomFirstName());
        generatedInfo.put("LastName", getRandomLastName());
        log.info("Generated orderId, names and email for updating Create Order json");

        File filePath = getResourceFile("release12apis", "Create_Order_R12.json");

        JSONObject createOrderJson = new JSONObject(FileUtils.readFile(filePath));
        createOrderJson.put("CapturedDate", getDate(0));
        createOrderJson.put("CustomerEmail", generatedInfo.get("Email"));
        createOrderJson.put("CustomerFirstName", generatedInfo.get("FirstName"));
        createOrderJson.put("CustomerLastName", generatedInfo.get("LastName"));
        createOrderJson.put("DoNotReleaseBefore", getDate(0));
        createOrderJson.put("OrgId", getOrganizationId(orderType));
        createOrderJson.put("OrderId", generatedInfo.get("OrderNumber"));
        createOrderJson.getJSONObject("OrderType").put("OrderTypeId", orderType);
        createOrderJson.getJSONArray("Payment").getJSONObject(0).getJSONArray("PaymentMethod").getJSONObject(0).getJSONObject("BillingAddress").put("Address", addBillAddress(addType));
        createOrderJson.put("CustomerPhone", generatedInfo.get("phoneNumber"));
        createOrderJson.put("OrderLine", addOrderLines(lines, shipType, addType, shipMType));

        log.info("Place order json is created successfully: " + createOrderJson.toString());
        return createOrderJson.toString();
    }

    /**
     * @param addType
     * @return
     */
    public static JSONObject addBillAddress(String addType) {
        JSONObject add = Common.getRandomAddress(addType);
        JSONObject address = new JSONObject();
        address.put("FirstName", generatedInfo.get("FirstName"));
        address.put("LastName", generatedInfo.get("LastName"));
        address.put("Address1", add.get("address_line_1"));
        address.put("Address2", add.get("address_line_2").equals("") ? JSONObject.NULL : add.get("address_line_2"));
        address.put("Address3", add.get("address_line_3").equals("") ? JSONObject.NULL : add.get("address_line_3"));
        address.put("City", add.get("address_city"));
        address.put("State", add.get("address_state"));
        address.put("PostalCode", add.get("address_zip_code"));
        address.put("Country", add.get("country_code"));
        String phoneNumber = add.get("phone_area_code") + String.valueOf(CustomerData.getRandomNumber(0, 10000000));
        generatedInfo.put("phoneNumber", phoneNumber);
        address.put("Phone", generatedInfo.get("phoneNumber"));
        address.put("Email", generatedInfo.get("Email"));
        return address;
    }

    public static ArrayList<JSONObject> addOrderLines(String[] lines, String shipType, String addType, String shipMType) {

        ArrayList<JSONObject> orderLines = new ArrayList<>();
        JSONObject lineItem;
        for (int i = 0; i < lines.length; i++) {
            lineItem = new JSONObject();

            lineItem.put("OrderLineId", String.valueOf(i + 1));
            JSONObject deliveryMethod = new JSONObject();

            boolean storeSame = generatedInfo.get("ShipFrom").equals(generatedInfo.get("ShipTo"));
            generatedInfo.put("storeSame", String.valueOf(storeSame));
            String deliveryMethodId = shipType.equals("STS") && storeSame ? "ShipToStore" : (shipType.equals("ROPIS") ? "PickUpAtStore" : "ShipToAddress");
            deliveryMethod.put("DeliveryMethodId", deliveryMethodId);
            lineItem.put("DeliveryMethod", deliveryMethod);
            lineItem.put("IsGiftCard", false);
            lineItem.put("GiftCardValue", JSONObject.NULL);
            lineItem.put("ItemId", lines[i]);

            lineItem.put("RequestedDeliveryDate", getShipMethodWithDueDate(addType, shipMType).get(1));
            lineItem.put("Quantity", 1);
            lineItem.put("UOM", "U");
            lineItem.put("UnitPrice", 20);
            lineItem.put("ShippingMethodId", getShipMethodWithDueDate(addType, shipMType).get(0));
            if (!storeSame) {
                JSONObject shipToAddress = new JSONObject();
                shipToAddress.put("IsAddressVerified", true);
                shipToAddress.put("Address", addBillAddress(addType));
                lineItem.put("ShipToAddress", shipToAddress);
            } else {
                lineItem.put("ShipToLocationId", generatedInfo.get("ShipTo"));
            }
            orderLines.add(lineItem);
            generatedInfo.put("ItemId" + i, lines[i]);
        }
        return orderLines;
    }

    public static ArrayList<String> getShipMethodWithDueDate(String addType, String shipMType) {
        ArrayList<String> shipDate = new ArrayList<>();
        String shipMethod = "";
        String dueDate = "";
        if (addType.equals("apo_addresses") || addType.equals("fpo_addresses") || addType.equals("us_territories") || addType.equals("iship_address")) {
            dueDate = getDate((addType.equals("apo_addresses") || addType.equals("fpo_addresses")) ? 42 : (addType.equals("us_territories") ? 21 : 10));
            shipMethod = "USP1";
        } else if (shipMType.equals("Standard")) {
            shipMethod = "UGRD";
            dueDate = getDate(addType.equals("puerto_addresses") ? 12 : 7);
        } else if (shipMType.equals("Express")) {
            shipMethod = "U2DA";
            dueDate = getDate(6);
        } else if (shipMType.equals("Rush")) {
            shipMethod = "UNDA";
            dueDate = getDate(5);
        } else if (shipMType.equals("N/A")) {
            shipMethod = "CPKP";
            dueDate = getDate(0);
        }
        shipDate.add(shipMethod);
        shipDate.add(dueDate);
        return shipDate;
    }

    public Response orderSearchApi(String apiType, String orderId) throws Exception {
        RequestObject req = new RequestObject("central_path_r12_apis", "ApisR12.json", apiType);
        req.setBody(createOrderSearchJson(orderId));
        req.setHeader("Authorization", "Bearer " + generatedData.get("AccessToken"));
        req.setHeader("Content-Type", "application/json");
        response = RestCall.invoke(webUrl, "Query", req);
        validateResponse(response);
        log.info("Searched with the orderId:  " + orderId);
        return response;
    }

    private String createOrderSearchJson(String orderId) {
        JSONObject orderSearch = new JSONObject();
        String queryParam = "OrderId='" + orderId + "'";
        orderSearch.put("Query", queryParam);
        return orderSearch.toString();
    }

    /**
     * @param response
     * @param status
     */
    public void verifyOrderResponse(Response response, String status) {
        JSONObject resBody = new JSONObject(response.getBody().asString());
        if (resBody.get("data") instanceof JSONArray) {
            Assert.assertEquals("Expected FulfillmentLineStatusId is not received", status, resBody.getJSONArray("data").getJSONObject(0).get("MaxFulfillmentStatusId").toString());
            Assert.assertEquals("Expected FulfillmentLineStatusId is not received", status, resBody.getJSONArray("data").getJSONObject(0).get("MinFulfillmentStatusId").toString());
        } else {
            Assert.assertEquals("Expected FulfillmentLineStatusId is not received", status, resBody.getJSONObject("data").get("MaxFulfillmentStatusId").toString());
            Assert.assertEquals("Expected FulfillmentLineStatusId is not received", status, resBody.getJSONObject("data").get("MinFulfillmentStatusId").toString());
        }
        log.info("Verified response successfully");
    }

    public Response getTranlogInfo(String apiType, String orderId) throws Exception {
        RequestObject req = new RequestObject("central_path_r12_apis", "ApisR12.json", apiType);
        req.setBody(createTranLogJson(orderId));
        req.setHeader("Authorization", "Bearer " + generatedData.get("AccessToken"));
        req.setHeader("Content-Type", "application/json");
        response = RestCall.invoke(webUrl, "tranLog", req);
        validateResponse(response);
        log.info("Received Tran log info");
        return response;
    }

    public void validateResponse(Response response) {
        String res = response.getBody().asString();
        int statusCode = response.getStatusCode();
        Assert.assertEquals("Unable to verify the status due to status code: " + statusCode, 200, statusCode);
        JSONObject resBody = new JSONObject(res);
        Assert.assertEquals("success message is false", true, resBody.get("success"));
        Assert.assertEquals("statusCode is not OK", "OK", String.valueOf(resBody.get("statusCode")));
        log.info("Response validated with status code: " + statusCode);
    }

    public void verifyTranResponse(Response response, String orgId, DataTable table) {
        List<String> tabList = table.asList();
        JSONObject resBody = new JSONObject(response.getBody().asString());
        for (String message : tabList) {
            for (int i = 0; i < resBody.getJSONArray("data").length(); i++) {
                JSONObject tranLogdata = resBody.getJSONArray("data").getJSONObject(i);
                Assert.assertEquals("organization is not matching", tranLogdata.get("organization"), orgId);
                if (tranLogdata.get("msg_type").toString().contains(message)) {
                    Assert.assertEquals("msg_type is not matching", tranLogdata.get("msg_type"), message);
                    Assert.assertEquals("message_type is not matching", tranLogdata.get("message_type"), message);
                }
            }
        }
        log.info("Verified the Tran Log message with the given message types: " + tabList.toString());
    }

    private String createTranLogJson(String orderId) throws IOException {
        File filePath = getResourceFile("release12apis", "Tran_Log.json");
        JSONObject tranLogJson = new JSONObject(FileUtils.readFile(filePath));
        tranLogJson.getJSONObject("map").put("TextSearch", orderId);
        log.info("Tran log Json body is created");
        return tranLogJson.toString();
    }

    public void getShipConfirm(String apiType, String releaseId, String releaseLineId, String item) throws Exception {
        RequestObject req = new RequestObject("central_path_r12_apis", "ApisR12.json", apiType);
        req.setBody(createShipConfirmJson(releaseId, releaseLineId, item));
        req.setHeader("Authorization", "Bearer " + generatedData.get("AccessToken"));
        req.setHeader("Content-Type", "application/json");
        response = RestCall.invoke(webUrl, "ShipConfirm", req);
        validateResponse(response);
        log.info("Completed Ship Confirm for releaseId:  " + releaseId + " of releaseLineId: " + releaseLineId);
    }

    private String createShipConfirmJson(String releaseId, String releaseLineId, String item) throws IOException {
        File filePath = getResourceFile("release12apis", "Ship_Confirm.json");
        JSONObject shipConfirmJson = new JSONObject(FileUtils.readFile(filePath));
        JSONObject orderEvent = shipConfirmJson.getJSONArray("OrderEvent").getJSONObject(0);
        orderEvent.put("ReleaseId", releaseId);
        orderEvent.put("ReleaseLineId", releaseLineId);
        orderEvent.put("Item", item);
        log.info("Ship confirm Json body is created");
        return shipConfirmJson.toString();
    }

}
