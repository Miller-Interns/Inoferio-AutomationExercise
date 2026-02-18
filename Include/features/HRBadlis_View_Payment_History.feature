@HRBadlisViewPaymentHistory
Feature: HR - View Payment History
  As HR I can open Badlis Records and view the Payment History tab.

  Scenario: HR views Payment History tab
    Given the user is logged in and on the dashboard to view HR Payment History tab
    When the user opens the HR Badlis Records page and selects the Payment History tab
    Then the HR Payment History tab is displayed
