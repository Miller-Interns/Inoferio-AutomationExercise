@MyBadlisBadlisHistorySorting
Feature: My Badlis - Badlis History Sorting
  As a user I can sort Badlis History columns.

  Scenario: User sorts columns on Badlis History table
    Given the user is logged in and on the dashboard to sort Badlis History
    When the user opens My Badlis and the Badlis History tab for sorting
    And the user sorts by Date, Badlis Count, Balance, Reason and Status on Badlis History
    Then the Badlis History table supports sorting
