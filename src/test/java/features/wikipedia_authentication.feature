@smoke
Feature: A user authenticates in
  Scenario: Login with valid credentials2
    Given A user with invalid credentials
    When I try to login
    Then Screen shows an error message
      """
      Incorrect username or password entered.
      Please try again.1
      """
  Scenario: Login with valid credentials
    Given A user with invalid credentials
    When I try to login
    Then I stay in the login screen
    And Screen shows an error message
      """
      Incorrect username or password entered.
      Please try again.
      """