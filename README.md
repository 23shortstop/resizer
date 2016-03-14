Running the Service
===================

A MySQL database for the service should be created manually before running the application. By default its name is “resizer”, but you can override this value with db.name entry of application.conf file, as well as user name and password to access it with db.user and db.password settings.

You need SBT installed in your system to build this application. For SBT installation instructions, please, refer to SBT setup page: http://www.scala-sbt.org/0.13/docs/Setup.html.

If it has already installed, just execute the following command from the root directory of the project to run the application:

$ sbt run

The application will be launched at http://localhost:8080. You also can change host and port with appropriate entries of application.conf file.

Documentation
=============

- This API is organized around REST.
- All API calls should be made with HTTP POST. Request data is passed to the API by POSTing JSON objects to the API endpoints with the appropriate parameters. The documentation for each API call will contain more detail on the parameters accepted by the call.
- All API URLs listed in this documentation are relative to http://localhost:8080/api/. For example, the /user API call is reachable at http://localhost:8080/api/user.