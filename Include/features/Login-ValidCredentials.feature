Feature: Microsoft Login Authentication

  @LoginValid
  Scenario: Successful login with valid Microsoft credentials
    Given the user is on the login page
    When the user clicks the "Login with Microsoft" button
    Then the user is redirected to the Microsoft login page
    When the user enters a valid Microsoft email
    And the user clicks the "Next" button
    And the user enters a valid Microsoft password
    And the user clicks the "Sign in" button
    And the user confirms "Stay signed in"
    Then the user should be redirected to the dashboard
