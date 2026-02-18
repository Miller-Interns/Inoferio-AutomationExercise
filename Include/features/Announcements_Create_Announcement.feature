@CreateAnnouncement
Feature: Announcements Management

  Scenario: HR creates a new announcement from the dashboard
    Given the user is logged in as HR and on the dashboard
    And the Announcements section is displayed
    When the user clicks the Create Announcement button
    Then a create announcement modal should be displayed

    When the user enters a valid title "Automation Test Announcement"
    And the user enters a valid message "This announcement was created using Katalon automation."
    And the user clicks the Publish button
    Then the announcement should be successfully published
    And the new announcement should appear in the Announcements list