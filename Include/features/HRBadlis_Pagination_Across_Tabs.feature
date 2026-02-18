@HRBadlisPaginationAcrossTabs
Feature: HR - Pagination across Badlis Records tabs
  As HR I can navigate pagination on Badlis Records, Disputes and Payment History tabs.

  Scenario: HR navigates pagination on each tab
    Given the user is logged in and on the dashboard to test HR Badlis Records pagination
    When the user opens HR Badlis Records and the Badlis Records tab for pagination
    And the user navigates pagination on the Badlis Records tab
    And the user switches to Disputes and navigates pagination
    And the user switches to Payment History tab on HR Badlis Records and navigates pagination
    Then pagination works across HR Badlis Records tabs
