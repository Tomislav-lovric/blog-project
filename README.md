# blog-project

Using Java (Spring Boot framework) and PostgreSQL database, implemented REST APIs which enable user to register and login, as well as creat posts/categories/tags/comments get one or more posts/categories/tags/comments, update posts/categories/tags/comments, delete posts/categories/tags/comments and add and delete categories/tags from posts.

## Prerequisites

JAVA

IDE (IntelliJ or eclipse)

POSTGRESQL

POSTMAN

DOCKER (optional)

## Set up database
Before starting you will need to create postgres database named blog (or you can name it differently, in that case though you will also need to change the name of the database in application.yml)

## Running the application
Open application folder using some java IDE (like intellij or eclipse) and run it through said IDE or you can use docker

## Testing Endpoints
There is postman collection file in main application folder which you can import and use to test all endpoints in Postman.

## Things to note
Before you create posts you are gonna need to create category otherwise exception will be throw, however when it comes to tags, unlike categories, you do not need to create them first because even if they do not exist in the database when you create post with them they will be created and saved to the database (I did it this way since I find tags to be more arbitrary compared to the categories, for example where category would be "Food" tags could be "food", "Food", "FOOD" etc.

## Possible TODO list
This is a possible TODO list if I ever decide to add more to the app:
  - Add pagination to get all methods for posts, categories, tags and comments
  - Make it so only Admin role can create, update and delete categories and tags
  - Make it so Admin role can also delete posts and comments
  - Make users and posts blockable by Admin role
  - Make blocked posts "invisble" to users
  - Etc.

## Authors

* **Tomislav Lovrić** - [projects](https://github.com/Tomislav-lovric)
