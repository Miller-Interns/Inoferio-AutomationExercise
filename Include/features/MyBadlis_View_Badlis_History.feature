@MyBadlisViewBadlisHistory
Feature: My Badlis - View Badlis History
  As a user I can open My Badlis and view the Badlis History tab.

  Scenario: User views Badlis History tab on My Badlis page
    Given the user is logged in and on the dashboard to view Badlis History
    When the user opens the My Badlis page and selects the Badlis History tab
    Then the My Badlis Badlis History tab is displayed
