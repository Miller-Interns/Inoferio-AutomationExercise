@MyBadlisViewPendingList
Feature: My Badlis - View Pending Confirmation List
  As a user I can open My Badlis and view the Pending Confirmation list.

  Scenario: User views Pending Confirmation tab on My Badlis page
    Given the user is logged in and on the dashboard to view Pending Confirmation list
    When the user opens the My Badlis page and selects the Pending Confirmation tab
    Then the My Badlis Pending Confirmation tab is displayed
