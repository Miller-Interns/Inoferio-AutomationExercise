@HRBadlisSorting
Feature: HR - Badlis Records Sorting
  As HR I can sort columns on the Badlis Records table.

  Scenario: HR sorts Name, Email, Status, Badlis and Balance columns
    Given the user is logged in and on the dashboard to sort HR Badlis Records
    When the user opens HR Badlis Records and the Badlis Records tab for sorting
    And the user sorts by Name, Email, Status, Badlis and Balance on Badlis Records table
    Then the HR Badlis Records table supports sorting
