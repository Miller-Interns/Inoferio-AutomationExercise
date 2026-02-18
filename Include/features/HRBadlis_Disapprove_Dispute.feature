@HRBadlisDisapproveDispute
Feature: HR - Disapprove Dispute
  As HR I can disapprove a dispute with a reason.

  Scenario: HR disapproves a dispute with reason
    Given the user is logged in and on the dashboard to disapprove HR dispute
    When the user opens HR Badlis Records and the Disputes tab for disapprove
    And the user expands a dispute row and clicks Reject
    And the user enters rejection reason "Need more proof" and submits
    Then the dispute is disapproved
