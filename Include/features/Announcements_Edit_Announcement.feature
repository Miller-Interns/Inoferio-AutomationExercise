@EditAnnouncement
Feature: Edit Announcement

  Scenario: HR edits an existing announcement
    Given the user is logged in as HR and on the Announcements page for editing
    And the user opens the My Posts tab
    When the user clicks the edit button of an announcement
    Then an edit announcement form should be displayed
    When the user updates the title to "Automation Test Announcement [EDITED]"
    And the user updates the message to "This announcement was edited using Katalon automation."
    And the user clicks the Update button to save changes
    Then the announcement should be successfully updated
