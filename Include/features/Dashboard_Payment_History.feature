@PaymentHistory
Feature: Payment History Navigation

  Background:
    Given the user is logged in and on the dashboard
    And the Payment History section is displayed

  Scenario: Redirect to Payment History page using View All button
    When the user clicks the "View All" button in the Payment History section
    Then the user should be redirected to the Payment History page
    And the page should display payment history entries related to the logged-in user