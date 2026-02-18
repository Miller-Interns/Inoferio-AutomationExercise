@HRBadlisApproveDispute
Feature: HR - Approve Dispute
  As HR I can approve a dispute from the Disputes tab.

  Scenario: HR approves a dispute
    Given the user is logged in and on the dashboard to approve HR dispute
    When the user opens HR Badlis Records and the Disputes tab for approve
    And the user expands a dispute row and clicks Approve
    And the user confirms the approve dialog
    Then the dispute is approved
