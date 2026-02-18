@HRBadlisViewBadlisRecords
Feature: HR - View Badlis Records
  As HR I can open Badlis Records and view the Badlis Records tab.

  Scenario: HR views Badlis Records tab
    Given the user is logged in and on the dashboard to view HR Badlis Records tab
    When the user opens the HR Badlis Records page and selects the Badlis Records tab
    Then the HR Badlis Records tab is displayed
