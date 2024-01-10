import socket
import sqlite3
import rsa
from selenium import webdriver
from client_server.city import cities, attr_cities

"""
    Steps of server :
    1. Socket - creates a socket, listens for a message.
    2. Data Info - creates 3 lists of the data.
    3. URL Making - creates url for flights and url for hotels,
    according to the lists that were created.
    4. Database - creates database of 3 tables, search, flights and hotels,
    and inserts the data, encrypted with rsa - rights now non-encrypted.
    5. Opening links - opening the 2 links for flights and hotels.
"""

# start of socket
sock = socket.socket()
port = 8000
maxConnections = 999
IP = '0.0.0.0'
publicKey, privateKey = rsa.newkeys(512)
URL = ''


def socket_get():
    """
    create a connection to the client, and receives the data
    :return: data: list of all the user's input
    """
    sock.bind((IP, port))
    sock.listen(maxConnections)
    print("Server started at " + IP + " on port " + str(port))

    (client, address) = sock.accept()
    print("New connection made!")

    # Listing for message
    running = True
    data = []
    while running:
        message = client.recv(1024).decode()
        if message != '':
            print(message)
            data = message.split(' ')
            # from to dep ret passengers stops cabin price rooms adults children stars rating price
            break
    client.close()
    sock.close()
    # end of socket
    return data


# start of data info
def data_info(data):
    """
    split the data list to three lists - search, flights and hotels
    :param data: list of all the user's input
    :return: search, flights, hotels: three divided lists
    """
    print(data)
    # search - general data
    search = [str(x).strip().lower() for x in data[:5]]
    # flights - flights data
    flights = [str(x).strip().lower() for x in data[5:8]]
    # hotels - hotels data
    hotels = [str(x).strip().lower() for x in data[8:]]

    for city in cities:
        if city.replace(" ", "").lower() == search[0]:
            print("Departure: ", cities.get(city))
            search[0] = cities.get(city)
        if city.replace(" ", "").lower() == search[1]:
            print("Return: ", cities.get(city))
            search[1] = cities.get(city)

    print(search)
    print(flights)
    print(hotels)
    # end of data info
    return search, flights, hotels

# example of the lists
# search = ['TLV', 'LON', '2023-05-10', '2023-05-12', '2']
# flights = ['0', 'Economy', '1000']
# hotels = ['1', '2', '0', '3', '7', '10000']


def url_making_flights(search, flights):
    """
    making the url of the flights
    :param search: list of all search information
    :param flights: list of all flights information
    :return: url_flights: string of the url created
    """
    # start of url making
    # flights
    url_flights = f"https://www.il.kayak.com/flights/{search[0]}-{search[1]}/{search[2]}/{search[3]}"
    if flights[1].lower().strip() != 'economy' and search[4] == '1':
        url_flights += f"/{flights[1]}?sort=price_a"
    elif flights[1].lower().strip() == 'economy' and search[4] != '1':
        url_flights += f"/{search[4]}adults?sort=price_a"
    elif flights[1].lower().strip() != 'economy' and search[4] != '1':
        url_flights += f"/{flights[1]}/{search[4]}adults?sort=price_a"
    else:
        url_flights += '?sort=price_a'

    if flights[0] != '' and flights[2] == '':
        url_flights += f"&fs=stops=~{flights[0]}"
    elif flights[0] == '' and flights[2] != '':
        url_flights += f"&fs=price=-{flights[2]}"
    elif flights[0] != '' and flights[2] != '':
        url_flights += f"&fs=price=-{flights[2]};stops=~{flights[0]}"
    else:
        url_flights += ''
    return url_flights


def get_cities(search):
    """
    helping function
    :param search: list of all search information
    :return: hotel_city, attr_city: strings of city name in different formats
    """
    hotel_city = ""
    attr_city = ""
    for city in cities:
        if cities.get(city) == search[1]:
            hotel_city = city.replace(" ", "")
            attr_city = city.split(',')[0]
            print("Found: " + city)
    return hotel_city, attr_city


# https://www.il.kayak.com/flights/TLV-LON/2023-04-04/2023-04-12/2adults?sort=price_a&fs=price=-2000;stops=~0


def hotel_rating(rating):
    """
    helping function
    :param rating: string of the number of rating
    :return: s: string of the word describes the number
    """
    if rating == '6':
        s = "okay"  # noqa
    elif rating == '7':
        s = "good"  # noqa
    elif rating == '8':
        s = "great"  # noqa
    else:
        s = "excellent"  # noqa
    return s


def url_making_hotels(search, hotels, hotel_city):
    """
    making the url of the hotels
    :param search: list of all the search information
    :param hotels: list of all the hotels information
    :param hotel_city: string of the name of the city
    :return: url_hotels: string of the url created
    """
    # hotels
    url_hotels = f"https://www.il.kayak.com/hotels/{hotel_city}/{search[2]}/{search[3]}/{hotels[1]}adults?sort=rank_a"
    if hotels[2] != '0':
        url_hotels = f"https://www.il.kayak.com/hotels/{hotel_city}/{search[2]}/{search[3]}/{hotels[1]}adults/{hotels[2]}children-0?sort=rank_a"
    if hotels[0] != '1':
        url_hotels = f"https://www.il.kayak.com/hotels/{hotel_city}/{search[2]}/{search[3]}/{hotels[1]}adults/{hotels[2]}children-0/{hotels[0]}rooms?sort=rank_a"
    if hotels[3] != '':
        url_hotels += f"&fs=stars={hotels[3]}"
        if hotels[5] != '':
            url_hotels += f";price=-{hotels[5]}"
        if hotels[4] != '':
            rate = hotel_rating(hotels[4])
            url_hotels += f";extendedrating={rate}"  # noqa
    elif hotels[5] != '':
        url_hotels += f"&fs=price=-{hotels[5]}"
        if hotels[4] != '':
            rate = hotel_rating(hotels[4])
            url_hotels += f";extendedrating={rate}"  # noqa
    elif hotels[4] != '':
        rate = hotel_rating(hotels[4])
        url_hotels += f"&fs=extendedrating={rate}"  # noqa
    return url_hotels


# https://www.il.kayak.com/hotels/LON/2023-04-04/2023-04-12/3adults?sort=rank_a&fs=stars=3;price=-10000;extendedrating=good


def url_making_attractions(attr_city):
    """
    making the url of the attractions
    :param attr_city: the destination city
    :return: url_attraction: string of the url created
    """
    # attractions
    url_attraction = ""
    if attr_city in attr_cities:
        if attr_city == "Washington D.C.":
            attr_city = "washington"
        attr_city = attr_city.lower()
        if len(attr_city.split(' ')) > 1:
            attr_city = attr_city.replace(' ', '-')
        url_attraction = f"https://aviewoncities.com/{attr_city}"
    else:
        url_attraction = f"https://en.wikipedia.org/wiki/{attr_city}"
    return url_attraction


# file = open(r"C:\Users\32807\AndroidStudioProjects\App2\app\src\main\res\raw\urls.txt", "w")
# file.write(f"{url_flights}\n{url_hotels}")
# file.close()


def socket_send(url_flights, url_hotels, url_attraction):
    """
    creates a connection to server and sends the links created
    :param url_flights: url of the flights
    :param url_hotels: url of the hotels
    :param url_attraction: url of the attractions
    """
    # Create a socket object
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # Bind the socket to a local address and port
    sock.bind(('0.0.0.0', 1234))

    # Listen for incoming connections
    sock.listen()

    # Accept a connection from a client
    client, address = sock.accept()
    print('Connected to', address)

    # Send data to the client
    client.send(f"{url_flights} {url_hotels} {url_attraction}".encode())

    # Close the connection when finished
    client.close()

    # end of url making


def database_insertion(search, flights, hotels):
    """
    database RSA encrypted insertion
    :param search: list of all the search information
    :param flights: list of all the flights information
    :param hotels: list of all the hotels information
    """
    # start of database
    conn = sqlite3.connect('deals.db')
    print("Opened database successfully")
    cursor = conn.cursor()

    # Encrypted insertion to databases SEARCH, FLIGHTS, HOTELS
    cursor.execute("INSERT INTO SEARCH VALUES (?, ?, ?, ?, ?)", (rsa.encrypt(search[0].encode(), publicKey),
                                                                 rsa.encrypt(search[1].encode(), publicKey),
                                                                 rsa.encrypt(search[2].encode(), publicKey),
                                                                 rsa.encrypt(search[3].encode(), publicKey),
                                                                 rsa.encrypt(search[4].encode(), publicKey)))

    cursor.execute("INSERT INTO FLIGHTS VALUES (?, ?, ?)", (rsa.encrypt(flights[0].encode(), publicKey),
                                                            rsa.encrypt(flights[1].encode(), publicKey),
                                                            rsa.encrypt(flights[2].encode(), publicKey)))

    cursor.execute("INSERT INTO HOTELS VALUES (?, ?, ?, ?, ?, ?)", (rsa.encrypt(hotels[0].encode(), publicKey),
                                                                    rsa.encrypt(hotels[1].encode(), publicKey),
                                                                    rsa.encrypt(hotels[2].encode(), publicKey),
                                                                    rsa.encrypt(hotels[3].encode(), publicKey),
                                                                    rsa.encrypt(hotels[4].encode(), publicKey),
                                                                    rsa.encrypt(hotels[5].encode(), publicKey)))

    # Non-encryption insertion
    # cursor.execute("INSERT INTO SEARCH VALUES (?, ?, ?, ?, ?)", (search[0],
    #                                                              search[1],
    #                                                              search[2],
    #                                                              search[3],
    #                                                              search[4]))
    #
    # cursor.execute("INSERT INTO FLIGHTS VALUES (?, ?, ?)", (flights[0],
    #                                                         flights[1],
    #                                                         flights[2]))
    #
    # cursor.execute("INSERT INTO HOTELS VALUES (?, ?, ?, ?, ?, ?)", (hotels[0],
    #                                                                 hotels[1],
    #                                                                 hotels[2],
    #                                                                 hotels[3],
    #                                                                 hotels[4],
    #                                                                 hotels[5],))

    print("Inserted values into tables")
    conn.commit()
    conn.close()
    # end of database


# start of opening links
# PATH = "C:\Program Files (x86)\chromedriver.exe"
# driver = webdriver.Chrome(executable_path=PATH)
# URL = url_flights
# driver.get(URL)
#
# driver.execute_script("window.open('about:blank','second_tab');")
# driver.switch_to.window("second_tab")
# URL = url_hotels
# driver.get(URL)
# end of opening links


def main():
    data = socket_get()
    search, flights, hotels = data_info(data)
    url_flights = url_making_flights(search, flights)
    hotel_city, attr_city = get_cities(search)
    url_hotels = url_making_hotels(search, hotels, hotel_city)
    url_attraction = url_making_attractions(attr_city)
    print("URL FLIGHTS: " + url_flights)
    print("URL HOTELS: " + url_hotels)
    print("URL ATTRACTIONS: " + url_attraction)
    socket_send(url_flights, url_hotels, url_attraction)
    database_insertion(search, flights, hotels)


if __name__ == "__main__":
    main()
