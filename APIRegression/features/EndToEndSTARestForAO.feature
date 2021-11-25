Feature: Inventory update for items

  @sanity @regression @project_ao_sta_r12
  Scenario Outline: Verify the TMW inventory update of an item with place order to DC
    Given I pass "<sku>" to get inventory from all stores for updating inventory with "<qty>" of "<shipLocation>" for "<orderType>"
    When I create the the order with below params:
      | orderType   | deliveryMethod   | shipVia   | addressType   | sku   | shipLocation   |
      | <orderType> | <deliveryMethod> | <shipVia> | <addressType> | <sku> | <shipLocation> |
    Then I verify the inventory of "<sku>" for "<shipLocation>"
    And I verify the order status "3000"
    And I verify the tran log message for "<orderType>" should contain below message_types:
      | OB_XINT_PublishReleaseToDCMSGType_GCPMT |
    When I complete the shipment confirmation for the order
    Then I verify the order status "7000"
    And I verify the tran log message for "<orderType>" should contain below message_types:
      | OB_XINT_PublishOrderLineMSGType_GCPMT |
      | OB_XINT_PublishOrderMSGType_GCPMT     |
    Examples:
      | orderType | qty | deliveryMethod | shipVia  | addressType                    | sku          | shipLocation |
      | MWD       | 1.0 | STA            | Standard | 48_contiguous_states_addresses | TMW10CK40101 | 1091-Add     |
