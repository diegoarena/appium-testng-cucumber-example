Feature: A user signs in
  Scenario: Login with invalid credentials
    Given A user with invalid credentials
    When I try to login
    Then I stay in the login screen
    And Screen shows an error message
      """
      Incorrect username or password entered.
      Please try again.
      """
  Scenario: Login with valid credentials
    Given A user with invalid credentials
    When I try to login
    Then Screen shows an error message
      """
      Incorrect username or password entered.
      Please try again.
      """