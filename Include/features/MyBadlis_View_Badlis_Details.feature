@MyBadlisViewBadlisDetails
Feature: My Badlis - View Badlis Details
  As a user I can open and close Badlis details from Badlis History.

  Scenario: User views and closes Badlis details
    Given the user is logged in and on the dashboard to view Badlis details
    When the user opens My Badlis and the Badlis History tab
    And the user opens the first Badlis row details
    And the user closes the Badlis details dialog
    Then the Badlis details dialog is closed
