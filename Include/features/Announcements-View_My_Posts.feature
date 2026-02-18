@AnnouncementsViewMyPosts
Feature: Announcements - View My Posts

  Scenario: HR views the My Posts list from the dashboard
    Given the user is logged in and on the dashboard to view my posts
    When the user opens the Announcements page and selects the My Posts tab
    Then the My Posts tab should be active
    And the user's announcements should be visible in the list
