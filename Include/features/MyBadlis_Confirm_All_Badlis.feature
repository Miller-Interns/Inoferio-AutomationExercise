@MyBadlisConfirmAllBadlis
Feature: My Badlis - Confirm All Badlis
  As a user I can confirm all Badlis from Pending Confirmation.

  Scenario: User confirms all Badlis
    Given the user is logged in and on the dashboard to confirm all Badlis
    When the user opens My Badlis and the Pending Confirmation tab for confirm all
    And the user clicks the Confirm All button
    And the user confirms the confirmation dialog for Confirm All
    Then all Badlis are confirmed
