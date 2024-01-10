import sqlite3

conn = sqlite3.connect('deals.db')
print("Opened database successfully")

try:
    conn.execute("DROP TABLE SEARCH")
    conn.execute("DROP TABLE FLIGHTS")
    conn.execute("DROP TABLE HOTELS")
except:  # noqa
    pass

conn.execute('''CREATE TABLE IF NOT EXISTS SEARCH
         (from_des TEXT NOT NULL,
         to_des TEXT NOT NULL,
         departure_date TEXT NOT NULL,
         return_date TEXT NOT NULL,
         passengers INT NOT NULL);''')

conn.execute('''CREATE TABLE IF NOT EXISTS FLIGHTS
         (stops INT,
         cabin TEXT,
         price INT);''')

conn.execute('''CREATE TABLE IF NOT EXISTS HOTELS
         (rooms INT,
         adults INT,
         children INT,
         stars INT,
         rating INT,
         price INT);''')


print("Table created successfully")
