@ViewEmployeeProfile
Feature: Employee Page - View Employee Profile

  Scenario: User opens an employee profile from the list
    Given the user is logged in and on the dashboard to view an employee profile
    When the user opens the Employees page from the menu
    And the user clicks the first employee in the list
    Then the employee profile page should be displayed
