@LoginInvalid
Feature: Microsoft Login – Negative Scenarios

  Background:
    Given the user is on the application login page
    And the user opens the Microsoft login page

  Scenario: Login attempt with valid email and invalid password
    When the user enters a valid Microsoft email for invalid login
    And the user clicks the Next button
    And the user enters an invalid Microsoft password
    And the user submits the login form
    Then an incorrect password error message should be displayed

  Scenario: Login attempt with invalid email
    When the user enters an invalid Microsoft email
    And the user clicks the Next button
    Then the user should remain on the Microsoft login page

  Scenario: Login attempt with blank email input
    When the user clicks the Next button without entering email
    Then a blank email validation message should be displayed
