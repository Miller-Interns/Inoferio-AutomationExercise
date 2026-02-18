@HRBadlisSortingPayHistory
Feature: HR - Payment History Sorting
  As HR I can sort columns on the Payment History table.

  Scenario: HR sorts Date, Employee Name and Amount on Payment History
    Given the user is logged in and on the dashboard to sort HR Payment History
    When the user opens HR Badlis Records and the Payment History tab for sorting
    And the user sorts by Date, Employee Name and Amount on Payment History table
    Then the HR Payment History table supports sorting
