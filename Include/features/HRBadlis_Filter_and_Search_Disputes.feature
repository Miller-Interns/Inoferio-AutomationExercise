@HRBadlisFilterAndSearchDisputes
Feature: HR - Disputes Filter and Search Bar
  As HR I can filter and search on the Disputes tab.

  Scenario: HR filters by date and status and uses search on Disputes
    Given the user is logged in and on the dashboard to filter and search HR Disputes
    When the user opens HR Badlis Records and the Disputes tab for filter and search
    And the user applies date and status filters then clears filters on Disputes
    And the user types "maryjoy" in the Disputes search bar
    Then the HR Disputes filter and search work correctly
