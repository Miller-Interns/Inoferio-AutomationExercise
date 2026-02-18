@AnnouncementsViewAllPosts
Feature: Announcements - View All Posts

  Scenario: View all announcements from the dashboard
    Given the user is logged in and on the dashboard to view announcements
    When the user clicks the "View All" button in the Announcements section
    Then the user should be redirected to the Announcements page
    And the "All Posts" tab should be displayed by default
    And the list of all announcements should be visible