@PendingConfirmationViewAll
Feature: Pending Confirmation Navigation

  Background:
    Given the user is logged in
    And the user is on the dashboard
    And the Pending Confirmation section is displayed

  Scenario: Redirect to Pending Confirmation page using View All button
    When the user clicks the "View All" button in the Pending Confirmation section
    Then the user should be redirected to the Pending Confirmation page
    And the page should display pending confirmation entries related to the logged-in user