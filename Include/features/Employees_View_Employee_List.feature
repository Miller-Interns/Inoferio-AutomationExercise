@ViewEmployeeList
Feature: Employee Page - View Employee List

  Scenario: User opens the Employees page and sees the employee list
    Given the user is logged in and on the dashboard to open the employee list
    When the user navigates to the Employees page from the menu
    Then the Employees page should display the employee list
