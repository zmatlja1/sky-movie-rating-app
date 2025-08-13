Feature: Movie Ratings API

  Scenario: Get movies with pagination and sorting by movie name ASC
    Given user "jan@test.com" with password "123"
    When the user sends GET request to "/rest/v1/movies?page=0&size=10&sortBy=DEFAULT&ascending=true"
    Then the response status should be 200 and first movie name Alien with rating 0

  Scenario: Add a rating to movie with ID 3 - The Alien
    Given user "jan@test.com" with password "123"
    When the user sends POST request to "/rest/v1/movies/3/ratings" with body:
      """
      {
        "rating": 5
      }
      """
    Then the response status should be 201

  Scenario: Get movies with pagination and sorting by movie name ASC and check movie's rating
    Given user "jan@test.com" with password "123"
    When the user sends GET request to "/rest/v1/movies?page=0&size=10&sortBy=DEFAULT&ascending=true"
    Then the response status should be 200 and first movie name Alien with rating 5

  Scenario: Update the user's rating for movie 3 - Alien
    Given user "jan@test.com" with password "123"
    When the user sends PUT request to "/rest/v1/movies/3/ratings/" with body:
      """
      {
        "rating": 3
      }
      """
    Then the response status should be 204

  Scenario: Get movies with pagination and sorting by movie name ASC and check movie's rating
    Given user "jan@test.com" with password "123"
    When the user sends GET request to "/rest/v1/movies?page=0&size=10&sortBy=DEFAULT&ascending=true"
    Then the response status should be 200 and first movie name Alien with rating 3

  Scenario: Delete user's rating for movie 3 - Alien
    Given user "jan@test.com" with password "123"
    When the user sends DELETE request to "/rest/v1/movies/3/ratings/"
    Then the response status should be 204
