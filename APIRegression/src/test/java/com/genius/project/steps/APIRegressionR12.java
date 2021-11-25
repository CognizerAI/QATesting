package com.genius.project.steps;

import com.napt.tbi.central.steps.*;
import com.genius.project.actions.InventoryHelper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import static com.napt.tbi.central.utils.OrderHelper.getOrganizationId;

/**
 * Api regression R1.2
 */
public class APIRegressionR12 {

    private static Logger log = Logger.getLogger(APIRegressionR12.class);
    private InventoryHelper inventoryHelper = new InventoryHelper();
    private static Response response;
    private double updatedQty = 0.0;
    private String[] lines;

    //    @Given("I pass the below sku parameters to get the inventory information from all stores:")
//    public void iPassTheBelowSkuParametersToGetTheInventoryInformationFromAllStores(DataTable table) throws Exception {
//        String itemId = String.valueOf(table.row(1).get(2));
//        double qty = Double.parseDouble(String.valueOf(table.row(1).get(3)));
//        String locationId = String.valueOf(table.row(1).get(4));
//        inventoryResponse = inventoryHelper.checkInventory(itemId, "", "");
//        for (int i = 0; i < inventoryResponse.length(); i++) {
//            JSONObject object = new JSONObject(inventoryResponse.get(i).toString());
//            int supplyQuantity = Integer.parseInt(String.valueOf(object.get("supplyQuantity")));
//            if (object.get("location").equals(locationId)) {
//                double updateQty = supplyQuantity < 0 ? qty + Math.abs(supplyQuantity) : qty;
//                inventoryHelper.addOrRemoveInventory(itemId, object.get("location").toString(), updateQty, "add");
//            } else if (!object.get("quantity").equals(0.0)) {
//                inventoryHelper.addOrRemoveInventory(itemId, object.get("location").toString(), Double.parseDouble(String.valueOf(object.get("quantity"))), "remove");
//            }
//        }
//
//        JSONObject locObject = new JSONObject(inventoryHelper.checkInventory(itemId, locationId, "").get(0).toString());
//        boolean qtyUpdated = Double.parseDouble(locObject.get("quantity").toString()) >= qty;
//        Assert.assertTrue(locObject.get("quantity") + " is not updated properly for the location: " + locObject.get("location"), qtyUpdated);
//        log.info("Verified the inventory update for item/s: " + itemId + ",Location/s: " + locationId);
//    }
    @Given("I pass \"([^\"]*)\" to get inventory from all stores for updating inventory with \"([^\"]*)\" of \"([^\"]*)\" for \"([^\"]*)\"")
    public void iPassToGetInventoryFromAllStoresForUpdatingInventoryWithOfFor(String skus, Double qty, String locations, String orderType) throws Exception {
        String[] skuList = skus.split(",");
        String[] location = locations.split("-");
        ActiveOmni_API.orgAuthentic("auth_api_endpoint", orderType);
        for (String sku : skuList) {
            JSONArray inventoryResponse = inventoryHelper.checkInventory(sku, "", "TMW_Ecomm_Shipping_STS");
            log.info("Received the Inventory for all stores of a Sku: " + sku);
            boolean storeInvFlag = false;
            for (int j = 0; j < inventoryResponse.length(); j++) {
                JSONObject object = new JSONObject(inventoryResponse.get(j).toString());
                int supplyQuantity = Integer.parseInt(object.get("supplyQuantity").toString());
                if (!object.get("location").equals(location[0])) {
                    if (!object.get("quantity").equals(0)) {
                        inventoryHelper.addOrRemoveInventory(sku, object.get("location").toString(), Double.parseDouble(object.get("quantity").toString()), "remove");
                        log.info("The inventory for Sku: " + sku + " is removed in all locations except for location: " + location[0]);
                    }
                } else {
                    double updateQty = supplyQuantity < 0 ? qty + Math.abs(supplyQuantity) : qty;
                    inventoryHelper.addOrRemoveInventory(sku, object.get("location").toString(), updateQty, "add");
                    log.info("The inventory for Sku: " + sku + " is updated for location: " + location[0]);
                    storeInvFlag = true;
                }
            }
            if (!storeInvFlag) {
                inventoryHelper.addOrRemoveInventory(sku, location[0], qty, "add");
                log.info("The new inventory for Sku: " + sku + " is added for location: " + location[0]);
            }
            JSONObject locObject = new JSONObject(inventoryHelper.checkInventory(sku, location[0], "").get(0).toString());
            updatedQty = Double.parseDouble(locObject.get("quantity").toString());
            Assert.assertTrue(locObject.get("quantity") + " is not updated properly for the location: " + locObject.get("location"), updatedQty >= qty);
        }
        log.info("Updated the inventory of skus: " + skus + " for the location: " + locations);
    }

    @And("I create the the order with below params:")
    public void iCreateTheTheOrderWithBelowParams(DataTable table) throws Exception {
        lines = table.row(1).get(4).split(",");
        inventoryHelper.createOrderR12Api("place_order", table);
        log.info("Placed order: " + InventoryHelper.generatedInfo.get("OrderId") + " with all the required info");
    }

    @Then("I verify the inventory of \"([^\"]*)\" for \"([^\"]*)\"")
    public void iVerifyTheInventoryOfFor(String sku, String locations) throws Exception {
        JSONObject locObject = new JSONObject(inventoryHelper.checkInventory(sku, locations.split("-")[0], "TMW_Ecomm_Shipping_STS").get(0).toString());
        boolean qtyUpdated = updatedQty > Double.parseDouble(locObject.get("quantity").toString());
        String assertMsg = "updatedQty: " + updatedQty + " is not matching with the current qty: " + Double.parseDouble(locObject.get("quantity").toString()) + " of location: " + locObject.get("location");
        Assert.assertTrue(assertMsg, qtyUpdated);
        log.info("Verified the inventory of a sku: " + sku + "for location: " + locations + " is updated to quantity: " + updatedQty);
    }

    @And("I verify the order status \"([^\"]*)\"")
    public void iVerifyTheOrderStatus(String status) throws Exception {
        response = inventoryHelper.orderSearchApi("order_search", InventoryHelper.generatedInfo.get("OrderNumber"));
        inventoryHelper.verifyOrderResponse(response, status);
        log.info("Verified the order status is: " + status);
    }

    @And("I verify the tran log message for \"([^\"]*)\" should contain below message_types:")
    public void iVerifyTheTranLogMessageForOrderTypeShouldContainBelowMessage_types(String orderType, DataTable table) throws Exception {
        ActiveOmni_API.orgAuthentic("auth_api_endpoint", orderType);
        Response tranResponse = inventoryHelper.getTranlogInfo("tran_log", InventoryHelper.generatedInfo.get("OrderNumber"));
        inventoryHelper.verifyTranResponse(tranResponse, getOrganizationId(orderType), table);
        log.info("Verified the tran log with the given messages");
    }

    @When("I complete the shipment confirmation for the order")
    public void iCompleteTheShipmentConfirmationForTheOrder() throws Exception {
        JSONObject resBody = new JSONObject(response.getBody().asString());
        String releaseLineId = resBody.getJSONArray("data").getJSONObject(0).getJSONArray("Release").getJSONObject(0).getJSONArray("ReleaseLine").getJSONObject(0).get("ReleaseLineId").toString();
        String releaseId = resBody.getJSONArray("data").getJSONObject(0).getJSONArray("Release").getJSONObject(0).get("ReleaseId").toString();
        inventoryHelper.getShipConfirm("ship_confirm", releaseId, releaseLineId, lines[0]);
        log.info("Completed the shipment confirmation for the order of releaseId: " + releaseId);
    }

}
