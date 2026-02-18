@DeleteAnnouncement
Feature: Delete Announcement

  Scenario: HR deletes an existing announcement
    Given the user is logged in as HR and navigates to the Announcements page
    And the user switches to the My Posts tab
    When the user clicks the delete icon of an announcement
    Then a delete confirmation modal should be displayed
    When the user confirms deletion
    Then the announcement should be successfully deleted
    And the announcement should no longer appear in the list
