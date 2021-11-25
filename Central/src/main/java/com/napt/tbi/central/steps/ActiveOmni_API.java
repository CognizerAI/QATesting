package com.napt.tbi.central.steps;

import com.jayway.jsonpath.JsonPath;
import com.napt.framework.api.utils.FileUtils;
import com.napt.framework.api.utils.RequestObject;
import com.napt.framework.api.utils.RestCall;
import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;

import static com.napt.tbi.central.utils.Common.getDate;
import static com.napt.tbi.central.utils.Common.getResourceFile;
import static com.napt.tbi.central.utils.CustomerData.*;
import static com.napt.tbi.central.utils.OrderHelper.*;

public class ActiveOmni_API {

    private static Logger log = Logger.getLogger(ActiveOmni_API.class);
    private static Response response;

    /**
     * Get the Authentication using auth api's.
     *
     * @param apiType   the apiEndPoint
     * @param orderType the AuthenticationType
     * @throws Exception the throwable
     */
    public static void orgAuthentic(String apiType, String orderType) throws Exception {
        response = RestCall.invoke(generateAuthUrl(), "", generateReqObj(apiType, orderType));
        String resBody = response.getBody().asString();
        int statusCode = response.getStatusCode();
        Assert.assertEquals("Unable to do Auth Api call due to the status code: " + statusCode, 200, statusCode);
        generatedData.put("AccessToken", JsonPath.parse(resBody).read("access_token"));
        log.info(orderType + " Child Admin Authentication is completed successfully");
    }

    /**
     * @param apiType is the type of API to get from tlrdAoApis
     * @param table   is the data sent from feature file for creating order
     * @throws Exception when the RequestObject is not found
     */
    public static void createOrderApi(String apiType, DataTable table) throws Exception {
        RequestObject req = new RequestObject("central_data_path", "tlrdAoApis.json", apiType);
        req.setBody(updateCreateOrderJson(table));
        req.setHeader("Authorization", "Bearer " + generatedData.get("AccessToken"));
        req.setHeader("Content-Type", "application/json");
        response = RestCall.invoke(webUrl, "CreateOrder", req);
        String returnedFulfillStatus = verifyResponse(response, "1000.000");
        Assert.assertEquals("fulfillment Status Code is not matched with status id: " + returnedFulfillStatus, "OPEN"
                , fulfillmentStatusCodes()
                        .get(returnedFulfillStatus));
        log.info("Created " + table.row(1).get(0) + "order with updated values using create API");
    }

    /**
     * @param table is the data sent from feature file for creating order
     * @return it returns the order JSON as body to Rest Service
     * @throws IOException when the JSON file is not found
     */
    private static String updateCreateOrderJson(DataTable table) throws IOException {
        // Store the detail from the datatable
        String orderType = table.row(1).get(0);
        String vasIns = table.row(1).get(1);
        String giftMsg = table.row(1).get(2);
        String shipType = table.row(1).get(3);
        String shipMType = table.row(1).get(4);
        String addType = table.row(1).get(5);
        String[] lines = table.row(1).get(6).split(",");
        String[] shipDetails = table.row(1).get(7).split("-");
        generatedData.put("ShipFrom", shipDetails[0]);
        generatedData.put("ShipTo", shipDetails[1].replace("Add", "null"));
        //Generating Random order, fulfillment, name and email
        generatedData.put("Email", getRandomEmail());
        generatedData.put("OrderId", String.valueOf(getRandomNumber(0, 1000000000)));
        generatedData.put("OrderLineId", String.valueOf(getRandomNumber(0, 1000000000)));
        generatedData.put("FulfillmentId", String.valueOf(getRandomNumber(0, 10000000)));
        generatedData.put("FirstName", getRandomFirstName());
        generatedData.put("LastName", getRandomLastName());
        log.info("Generated order, fulfillment, name and email for updating order json");

        File filePath = getResourceFile("release11apis", shipType + "_CreateOrder.json");

        JSONObject createOrderBody = new JSONObject(FileUtils.readFile(filePath));

        createOrderBody.put("OrganizationId", getOrganizationId(orderType));
        createOrderBody.put("OrderTypeId", orderType);
        createOrderBody.put("IsVASRequired", vasIns.equals("No VAS") ? "false" : "true");
        createOrderBody.put("FulfillmentId", generatedData.get("FulfillmentId"));

        if (!vasIns.equals("No VAS")) {
            createOrderBody.getJSONArray("FulfillmentLineInstruction").put(0, addVadInstructionText());
        }

        if (createOrderBody.getJSONArray("FulfillmentAddress").length() == 1 || createOrderBody
                .getJSONArray("FulfillmentAddress").getJSONObject(1).getJSONObject("AddressTypeId").get("AddressTypeId")
                .equals("Billing")) {
            if (shipType.equals("ROPIS")) {
                JSONObject add = addAddress(addType);
                add.remove("State");
                add.remove("Phone");
                add.remove("Address2");
                add.remove("PostalCode");
                add.remove("Country");
                add.remove("Address1");
                add.remove("City");
                createOrderBody.getJSONArray("FulfillmentAddress").getJSONObject(0)
                        .put("Address", add);
            } else {
                createOrderBody.getJSONArray("FulfillmentAddress").getJSONObject(0)
                        .put("Address", addAddress(addType)).put("Email", generatedData.get("Email"));
            }
        }
        if (createOrderBody.getJSONArray("FulfillmentAddress").length() > 1 && createOrderBody
                .getJSONArray("FulfillmentAddress").getJSONObject(0).getJSONObject("AddressTypeId").get("AddressTypeId")
                .equals("Shipping")) {
            createOrderBody.getJSONArray("FulfillmentAddress").getJSONObject(1).put("Address", addAddress(addType));
        }

        if (createOrderBody.getJSONArray("FulfillmentLine").length() == 1) {
            createOrderBody.put("FulfillmentLine", addLineItems(lines, vasIns));
        }

        createOrderBody.put("CustomerFirstName", generatedData.get("FirstName"));
        createOrderBody.put("CustomerLastName", generatedData.get("LastName"));

        String shipMethod = "";
        String dueDate = "";
        if (addType.equals("apo_addresses") || addType.equals("fpo_addresses") || addType
                .equals("us_territories") || addType.equals("iship_address")) {
            dueDate = getDate((addType.equals("apo_addresses") || addType.equals("fpo_addresses")) ? 42 : (addType
                    .equals("us_territories") ? 21 : 10));
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

        generatedData.put("shipMethod", shipMethod);

        createOrderBody.put("ShipFromLocationId", generatedData.get("ShipFrom"));

        boolean storeSame = generatedData.get("ShipFrom").equals(generatedData.get("ShipTo"));
        generatedData.put("storeSame", String.valueOf(storeSame));
        String deliveryMethod = (shipType.equals("STS") || shipType.equals("BOPIS"))? (storeSame ? "PickUpAtStore" : "ShipToStore") : (shipType.equals("ROPIS") ? "PickUpAtStore" : "ShipToAddress");
        createOrderBody.put("DeliveryMethodId", deliveryMethod);

        if (shipType.equals("STA")) {
            createOrderBody.put("DestinationActionId", JSONObject.NULL);
        } else if (shipType.equals("ROPIS")) {
            createOrderBody.put("CustomerPhone", generatedData.get("phoneNumber"));
            createOrderBody.put("ShipToLocationId", generatedData.get("ShipTo"));
        } else {
            String destinationAction = storeSame ? "PICKUP" : "MERGE";
            createOrderBody.put("DestinationActionId", destinationAction);
            createOrderBody.put("ShipToLocationId", generatedData.get("ShipTo"));
//            createOrderBody.put("ServiceLevelCode", JSONObject.NULL);
        }

//        if (!shipType.equalsIgnoreCase("ROPIS")) {
        createOrderBody.put("ShipViaId", generatedData.get("shipMethod"));
        createOrderBody.getJSONObject("Extended").put("GiftMessage", addGiftMsg(giftMsg));
        createOrderBody.put("CustomerEmail", generatedData.get("Email"));
        createOrderBody.put("OrderCaptureDate", getDate(0));
        createOrderBody.put("ShippingDueDate", dueDate);
        createOrderBody.put("DeliveryDueDate", dueDate);
        for (int i = 0; i < lines.length; i++) {
            createOrderBody.getJSONArray("FulfillmentPackages").put(i, addFulfillmentPackages(i, addType, shipType));
        }
//        }

        log.info("Create order json is updated successfully");
        return createOrderBody.toString();
    }

    /**
     * @param apiType           is the type of API to get from tlrdAoApis
     * @param fulfillmentStatus is passed for verification
     * @return it returns the FulfillmentLineStatusId for asserting
     * @throws Exception when the RequestObject is not found
     */
    public static String getFulfillmentStatus(String apiType, String fulfillmentStatus) throws Exception {
        if (fulfillmentStatus.equals("5000.000")) {
            Thread.sleep(10000);
            executeShipmentJob();
        }
        Thread.sleep(4000);
        RequestObject req = new RequestObject("central_data_path", "tlrdAoApis.json", apiType);
        req.setHeader("Authorization", "Bearer " + generatedData.get("AccessToken"));
        req.setHeader("Content-Type", "application/json");
        req.setPathParam("FullfilmentNo", generatedData.get("FulfillmentId"));
        response = RestCall.invoke(webUrl, "", req);
        log.info("Received the fulfillment status for fulfillment no: " + generatedData.get("FulfillmentId"));
        return verifyResponse(response, fulfillmentStatus);
    }
}
