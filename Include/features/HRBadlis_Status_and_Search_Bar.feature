@HRBadlisStatusAndSearchBar
Feature: HR - Badlis Records Status and Search Bar
  As HR I can filter by status and use the search bar on Badlis Records.

  Scenario: HR filters by Status and uses search bar
    Given the user is logged in and on the dashboard to test HR Badlis Records status and search
    When the user opens HR Badlis Records and the Badlis Records tab
    And the user applies Status filter Active and Deactivated then clears filters
    And the user types in the Badlis Records search bar "mary" and "jan"
    Then the HR Badlis Records status and search work correctly
