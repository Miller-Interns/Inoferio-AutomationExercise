@VerifySearchBar
Feature: Employee Page - Verify Search bar

  Scenario: User searches employees using the search bar
    Given the user is logged in and on the dashboard to verify the employee search bar
    When the user goes to the Employees page to verify search
    And the user types "Mary" in the employee search bar and submits
    And the user types "Jan" in the employee search bar
    Then the employee search bar should filter the list
