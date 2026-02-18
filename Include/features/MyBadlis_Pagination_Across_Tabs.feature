@MyBadlisPaginationAcrossTabs
Feature: My Badlis - Pagination across tabs
  As a user I can navigate pagination on Pending Confirmation, Badlis History and Payment History.

  Scenario: User navigates pagination on each My Badlis tab
    Given the user is logged in and on the dashboard to test My Badlis pagination
    When the user opens My Badlis and the Pending Confirmation tab
    And the user navigates pagination on the current tab
    And the user switches to Badlis History and navigates pagination
    And the user switches to Payment History tab on My Badlis and navigates pagination
    Then pagination works across My Badlis tabs
