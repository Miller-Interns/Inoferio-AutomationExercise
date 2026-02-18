@MyBadlisConfirmSelectionBadlis
Feature: My Badlis - Confirm Selection Badlis
  As a user I can select rows and confirm selected Badlis.

  Scenario: User selects rows and confirms selected Badlis
    Given the user is logged in and on the dashboard to confirm selection Badlis
    When the user opens My Badlis and the Pending Confirmation tab for confirm selection
    And the user selects rows using the checkboxes on Pending Confirmation
    And the user clicks Confirm Selected and confirms the dialog
    Then the selected Badlis are confirmed
