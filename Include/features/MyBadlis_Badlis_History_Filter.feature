@MyBadlisBadlisHistoryFilter
Feature: My Badlis - Badlis History Filter
  As a user I can filter Badlis History and clear filters.

  Scenario: User applies and clears filters on Badlis History
    Given the user is logged in and on the dashboard to filter Badlis History
    When the user opens My Badlis and the Badlis History tab for filtering
    And the user applies Status and other filters on Badlis History
    And the user clicks Clear Filters on Badlis History
    Then the Badlis History filters are cleared
