@NavigateEmployeeListPagination
Feature: Employee Page - Navigate Employee List Pagination

  Scenario: User navigates through employee list pages
    Given the user is logged in and on the dashboard to test employee pagination
    When the user goes to the Employees page for pagination
    And the user goes to the second page
    And the user clicks the Next page button
    And the user goes to the last page
    And the user clicks the Previous page button
    And the user goes to the first page
    Then the user should be on page one of the employee list
