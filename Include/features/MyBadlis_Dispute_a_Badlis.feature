@MyBadlisDisputeABadlis
Feature: My Badlis - Dispute a Badlis
  As a user I can dispute a Badlis with a reason.

  Scenario: User disputes a Badlis with reason
    Given the user is logged in and on the dashboard to dispute a Badlis
    When the user opens My Badlis and the Pending Confirmation tab for dispute
    And the user clicks the Dispute button
    And the user enters dispute reason "I did take out the trash"
    And the user submits the dispute form
    Then the dispute is submitted
