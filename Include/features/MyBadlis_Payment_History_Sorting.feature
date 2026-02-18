@MyBadlisPaymentHistorySorting
Feature: My Badlis - Payment History Sorting
  As a user I can sort Payment History columns.

  Scenario: User sorts Date and Amount on Payment History
    Given the user is logged in and on the dashboard to sort Payment History
    When the user opens My Badlis and the Payment History tab for sorting
    And the user sorts by Date and Amount on Payment History table
    Then the Payment History table supports sorting
