@MyBadlisViewPaymentHistory
Feature: My Badlis - View Payment History
  As a user I can open My Badlis and view the Payment History tab.

  Scenario: User views Payment History tab on My Badlis page
    Given the user is logged in and on the dashboard to view Payment History
    When the user opens the My Badlis page and selects the Payment History tab
    Then the My Badlis Payment History tab is displayed
