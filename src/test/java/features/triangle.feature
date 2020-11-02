@smoke
Feature: Triangle type screen

  Scenario Outline: Verify triangle type is determined correctly
    Given The user enters lado values: <lado1> and <lado2> and <lado3>
    When Click on calculate button
    Then The triangle type shown should be <triangleType>
    Examples:
      | lado1 | lado2 | lado3 | triangleType |
      | 1    | 1     | 1     | "O triângulo é Equilátero" |
      | 10   | 15    | 25    | "O triângulo é Escaleno" |
      | 2    | 2     | 6     | "O triângulo é Isósceles"|