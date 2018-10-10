## Overview

Kite Messenger is a threaded chat for the discussion of multiple subjects, keeping track of every line of discussion. Kite Messenger was developed by [Caio Benatti Moretti](http://www.moretticb.com).

This is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the [GNU General Public License](http://www.gnu.org/licenses/) for more details. You should have received a copy of the GNU General Public License along with this program. If not, see [here](http://www.gnu.org/licenses/).

This project is divided in two main folders:

* **Source/** &mdash; Source code of client and server implementations
* **Environment/** &mdash; Test environment for launching the Kite Messenger Server and using the Kite Messenger Client

## Source Code

The **Source/** folder encompasses two *Eclipse* project folders:

* **Kite/** &mdash; Kite Messenger Client
* **KiteServer/** &mdash; Kite Messenger Server

## Environment

The Kite Messenger structure consists of an HSQLDB database and Java applications for client and server sides.

### Server 

The Kite Messenger Server root directory (**kiteServer/** folder) is structured as follows:

* **data/** &mdash; Database files with the current server state (empty folder in first execution)
* **jars/** &mdash; JAR files for the Kite Server and the HSQLDB database
* **scripts/** &mdash; Script files (bat and sh) for managing HSQLDB database
* **kiteServer.sh** &mdash; Kite Messenger Server launcher for *Linux*
* **kiteServer.bat** &mdash; Kite Messenger Server launcher for *Windows*

Launch the Kite Messenger Server with the *sh* or *bat* file. The first execution will create the database structure for the Kite Messenger Server. Next executions will consider the previous state with the registered users and their firendship relationships if **data/** folder remains unchanged.

The current state of the server database encompasses user data (status message, profile name, profile picture, added users, etc), being stored at *data* folder. Keep these files in order to keep the database state. You can also delete everything inside **data/** (and then run the Kite Server Launcher) to go back to the initial state.

In Windows, run `kiteServer.bat` to initiate both the HSQLDB database server (background) and the Kite Messenger Server (foreground). If the Kite Server is interrupted, and the database is still in execution, then run `kiteServer.bat -a`.

In Linux, simply run `kiteServer.sh`, regardless of whether the database is already running. If so, its PID is will be shown; otherwise, the database will be initiated. Kill the PID if both servers are needed to be shut down.

### Client

Having a Kite Messenger Server available, the client can be used. To execute it, run `java -jar kite.jar`.

Once opened, go to preferences (the bottom right gear icon) to set the due IP address and port. Afterwards, go back to the main screen and sign up to have an username and password at the specified server.

After such configurations, you are ready to go Kiting. The interface is very intuitiveand easy to use. Have fun with threaded discussions.


Fork me on [GitHub](http://github.com/moretticb/Kite/tree/master/Source/Kite) :)
