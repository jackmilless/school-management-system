# School Management System
The School Management System implements a mock full-stack web development project, allowing a user to access and manipulate database records from a website. 

## Backend
The backend is implemented in Java using Maven, which communicates with an in-memory database using JDBC. This database is provided by H2Database, which in this project is used to mimic postgreSQL.

The Java portion of the code is split into a Controller class that accepts HTTP requests, a Service class, a DAO class that communicates with the database, and a number of Model classes that
represent database tables. There is also a Utility class to setup a database connection as well as a number of Trigger classes to handle complex database constraints or update conditions.

Database tables are created at runtime, after which a number of records are inserted. This is achieved by running SQL files stored in `src/main/resources/db`. 
The database is a simplified representation of entities involved in a school system. These relationships are visualized in an Entity Relationship Diagram (ERD) in `imgs`.

Additionally, Javalin is used in the Controller to create a server and set up an API to respond to various HTTP requests, 
in particular responding with JSON objects representing records obtained from or persisted to the database.


## Frontend
Javalin is also used to initialize webpages using its Vue.js plugin. The frontend thus consists of various webpages coded with Vue.js, HTML and CSS. These files can be found in `src/main/resources/vue`.

The user begins on a homepage where they can select one of teachers, classrooms, courses, or students. From these collection pages they can see a number of records in a table on the left along
with a few menu options. On the right they can either search for a particular record or several based on various criteria, or add a record to the database. 

The user can click on any of the rows in the table to open a specific record. They can also click on most of the header cells to re-order the records. 
If the user opens a specific record they can change certain fields and update the record in the database.

Webpages restrict the input to prevent the user from submitting invalid input. However, if the user attempts to persist data that violates a complex database constraint,
the program will alert the user and provide feedback as to what went wrong. On the other hand, input fields turn green to indicate a successful request.

## Usage
The server can be started by simply running the provided jar file, for example with the following terminal command from inside the root directory:
```
java -jar target/school-management-system-1.0-jar-with-dependencies.jar
```
A large number of example records are inserted during startup so it may take a few seconds to load, but a series of Javalin messages indicate the server is active.
The program is set to run on port 7000 so the home page (and entry point into the program from the frontend) can be visited with the following URL:
```
localhost:7000
```
The server (and program) can be terminated from the frontend by pressing the "Stop Server" button in the bottom left corner on the home page (which can be visited from any other page with a button on the bottom right).
