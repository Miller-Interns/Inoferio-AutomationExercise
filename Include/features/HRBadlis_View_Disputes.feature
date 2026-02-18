@HRBadlisViewDisputes
Feature: HR - View Disputes
  As HR I can open Badlis Records and view the Disputes tab.

  Scenario: HR views Disputes tab
    Given the user is logged in and on the dashboard to view HR Disputes tab
    When the user opens the HR Badlis Records page and selects the Disputes tab
    Then the HR Disputes tab is displayed
