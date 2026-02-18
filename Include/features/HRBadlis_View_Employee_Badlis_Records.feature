@HRBadlisViewEmployeeBadlisRecords
Feature: HR - View an Employee Badlis Records
  As HR I can open and view a specific employee's Badlis records.

  Scenario: HR views an employee Badlis records
    Given the user is logged in and on the dashboard to view an employee HR Badlis records
    When the user opens HR Badlis Records and the Badlis Records tab for viewing employee records
    And the user opens the employee "Albert Cruz" Badlis records row
    Then the employee Badlis records are displayed
