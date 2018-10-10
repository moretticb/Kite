# Kite Messenger---README file

Developed by Caio Benatti Moretti (www.moretticb.com). This project is divided in two main folders:

* **Source/**---Source code of client and server implementations
* **Environment/**---Test environment for launching the Kite Messenger Server and using the Kite Messenger Client

## Source Code

The **Source/** folder encompasses two *Eclipse* project folders:

* **Kite/**---Kite Messenger Client
* **KiteServer/**---Kite Messenger Server

## Environment

The Kite Messenger structure consists of an HSQLDB database and Java applications for client and server sides.

### Server - Launching and Installation

The Kite Messenger Server root directory (**kiteServer/** folder) is structured as follows:

* **data/**---Database files with the current server state (empty folder in first execution)
* **jars/**---JAR files for the Kite Server and the HSQLDB database
* **scripts/**---Script files (bat and sh) for managing HSQLDB database
* **kiteServer.sh**---Kite Messenger Server launcher for *Linux*
* **kiteServer.bat**---Kite Messenger Server launcher for *Windows*

Launch the Kite Messenger Server with the *sh* or *bat* file. The first execution will create the database structure for the Kite Messenger Server. Next executions will consider the previous state with the registered users and their firendship relationships if **data/** folder remains unchanged.

The current state of the server database encompasses user data (status message, profile name, profile picture, added users, etc), being stored at *data* folder. Keep these files in order to keep the database state. You can also delete everything inside **data/** (and then run the Kite Server Launcher) to go back to the initial state.

In Windows, run `kiteServer.bat` to initiate both the HSQLDB database server (background) and the Kite Messenger Server (foreground). If the Kite Server is interrupted, and the database is still in execution, then run `kiteServer.bat -a`.

In Linux, simply run `kiteServer.sh`, regardless of whether the database is already running. If so, its PID is will be shown; otherwise, the database will be initiated. Kill the PID if both servers are needed to be shut down.

### Client

Having a Kite Messenger Server available, the client can be used. To execute it, run `java -jar kite.jar`.

Once opened, go to preferences (the bottom right gear icon) to set the due IP address and port. Afterwards, go back to the main screen and sign up to have an username and password at the specified server.

After such configurations, you are ready to go Kiting. The interface is very intuitiveand easy to use. Have fun with threaded discussions :)

