@HRBadlisSortingDisputes
Feature: HR - Disputes Sorting
  As HR I can sort columns on the Disputes table.

  Scenario: HR sorts Date, Employee Name, Badlis, Balance, Reason and Status on Disputes
    Given the user is logged in and on the dashboard to sort HR Disputes
    When the user opens HR Badlis Records and the Disputes tab for sorting
    And the user sorts by Date, Employee Name, Badlis, Balance, Reason and Status on Disputes table
    Then the HR Disputes table supports sorting
