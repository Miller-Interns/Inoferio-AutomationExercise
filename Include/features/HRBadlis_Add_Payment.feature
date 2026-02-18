@HRBadlisAddPayment
Feature: HR - Add Payment
  As HR I can add a payment for an employee from Badlis Records.

  Scenario: HR adds payment for an employee
    Given the user is logged in and on the dashboard to add HR Payment
    When the user opens HR Badlis Records and searches for an employee
    And the user opens Add Payment for the employee and selects amount
    And the user submits the Add Payment form
    Then the payment is added
