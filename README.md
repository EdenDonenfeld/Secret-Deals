# Secret-Deals
The subject of the project is client-server software that searches for and displays as a result the most profitable deal for a selected destination in a date range, according to filters chosen by the client.

Each customer enters departure city, landing city, date of departure, date of landing and number of passengers. It is also possible to enter flight filters and hotel filters.

Flight filters :
1. Several stops
2. seat type
3. maximum price

Hotel filters :
1. Number of rooms
2. amount of adults
3. amount of children
4. Minimum number of stars
5. Minimum hotel rating
6. maximum price

**There is a recording attachment of running the system, under Recording (29-05-2023 22-59-44) folder.**

The client is an Android application that passes to the server the filters that the client entered for their vacation, and the server returns three links.

1. One for the most affordable and cheap flight.
2. Second for the most recommended hotel on the vacation booking site.
3. Third for attractions and information about the landing place for the customer.


# System installation
Required environment and required tools:

_For the server:_ 
IDLE for the python language, version 3.9 and downloading socket, sqlite3 and rsa libraries.

_For the client:_ 
Studio Android software, and downloading an Emulator, or connecting a phone with Android operating system to a computer.
A stable internet connection is required in order to create a connection between the client and the server, by sockets.

File locations:
The server files and the server database are in - server directory. 

The files are:
server.py, data.py, city.py and deals.db.

The application files are in - App2/app/src/main where there is a java folder which has the
java files, containing the Activities. In addition, there is the res folder where the design files are
.xml. And all the design additions (Vector Assets and more).
