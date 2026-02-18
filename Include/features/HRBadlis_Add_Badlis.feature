@HRBadlisAddBadlis
Feature: HR - Add Badlis
  As HR I can add a new Badlis record from the Badlis Records tab.

  Scenario: HR adds a new Badlis with description
    Given the user is logged in and on the dashboard to add HR Badlis
    When the user opens HR Badlis Records and the Badlis Records tab for adding Badlis
    And the user opens the Add Badlis form and fills date and description
    And the user submits the Add Badlis form
    Then the new Badlis is added
