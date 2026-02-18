@MyBadlisPaymentHistoryFilter
Feature: My Badlis - Payment History Filter
  As a user I can filter and clear filters on Payment History.

  Scenario: User applies and clears filter on Payment History
    Given the user is logged in and on the dashboard to filter Payment History
    When the user opens My Badlis and the Payment History tab for filtering
    And the user applies a date filter on Payment History
    And the user clicks Clear Filters on Payment History
    Then the Payment History filters are cleared
