@HRBadlisStatusAndSearchPaymentHistory
Feature: HR - Payment History Status and Search Bar
  As HR I can filter and search on the Payment History tab.

  Scenario: HR applies date filter and uses search on Payment History
    Given the user is logged in and on the dashboard to test HR Payment History status and search
    When the user opens HR Badlis Records and the Payment History tab for filter and search
    And the user applies date filter and clears filters on Payment History
    And the user types "mary" in the Payment History search bar and submits
    Then the HR Payment History filter and search work correctly
