@MyBadlisConfirmABadlis
Feature: My Badlis - Confirm a Badlis
  As a user I can confirm a single Badlis from Pending Confirmation.

  Scenario: User confirms one Badlis
    Given the user is logged in and on the dashboard to confirm a Badlis
    When the user opens My Badlis and the Pending Confirmation tab for single confirm
    And the user clicks the Confirm button for one Badlis
    And the user confirms the confirmation dialog
    Then the single Badlis is confirmed
