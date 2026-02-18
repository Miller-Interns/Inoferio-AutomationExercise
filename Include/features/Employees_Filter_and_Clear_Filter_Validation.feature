@FilterAndClearFilterValidation
Feature: Employee Page - Filter and Clear Filter Validation

  Scenario: User applies Status filter then clears it
    Given the user is logged in and on the dashboard to validate employee filters
    When the user goes to the Employees page for filter validation
    And the user opens the Status filter and selects Active
    And the user opens the Status filter again
    And the user selects Deactivated in the Status filter
    And the user clicks the Clear Filters button
    Then the employee filter should be cleared
