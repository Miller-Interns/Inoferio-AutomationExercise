@MyBadlisPendingConfirmationFilter
Feature: My Badlis - Pending Confirmation Filter
  As a user I can filter and clear filters on Pending Confirmation.

  Scenario: User applies and clears date filter on Pending Confirmation
    Given the user is logged in and on the dashboard to filter Pending Confirmation
    When the user opens My Badlis and the Pending Confirmation tab for filtering
    And the user applies date filters on Pending Confirmation
    And the user clicks Clear Filters on Pending Confirmation
    Then the Pending Confirmation filters are cleared
