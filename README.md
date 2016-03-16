# Resizer

This API allows to resize images. User can upload an image along with resize parameters and in response he will receive a resized image. Also, API allows to see list of user's earlier uploaded images along with resized results and parameters. Each user is associated with an unique key which is created when a new user starts to use the service.

### Documentation

- This API is organized around REST.
- All API calls should be made with HTTP POST. Request data is passed to the API by POSTing JSON objects to the API endpoints with the appropriate parameters. The documentation for each API call will contain more detail on the parameters accepted by the call.
- All API URLs listed in this documentation are relative to _http://localhost:8080/api/_. For example, the **_/user_** API call is reachable at _http://localhost:8080/api/user_.

> **_/user_** - Create a new user

- To obtain an unique key for a new user you should send a request to **_/user_**.
This endpoint requires not any parameters. A successful response will contain an unique key which should be used for subsequent interaction with API.

- Example response:
```
{
"key":"27a64feb-d5dd-436b-af30-453bac7f5e33"
}
```

> **_/resize_** - Resize an image

-

> **_/history_** - Retrieve history

-

> Errors

- You can consider any non-200 HTTP response code an error - the returned data will contain message with more detailed information.

- Example error response:
```
{
"error":"The requested resource could not be found."
}
```

### Running the Service

A MySQL database for the service should be created manually before running the application. By default its name is “resizer”, but you can override this value with _db.name_ entry of _application.conf_ file in the _main_ package, as well as user name and password to access it with _db.user_ and _db.password_ settings.

You need SBT installed in your system to build this application. For SBT installation instructions, please, refer to [SBT setup page](http://www.scala-sbt.org/0.13/docs/Setup.html).

If it has already installed, just execute the following command from the root directory of the project to run the application:
```
$ sbt run
```
The application will be launched at _http://localhost:8080_. You also can change host and port with appropriate entries of _application.conf_ file.

To test the service an addition MYSQL database should be created manually. By default its name is “resizerTest" It can be overriden with _db.name_ entry of _application.conf_ file in the _test_ package. The task for running tests is:
```
$ sbt test
```
