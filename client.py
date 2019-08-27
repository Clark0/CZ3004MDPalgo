import socket

HOST = "localhost"
PORT = 8001
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print('Socket create')

try:
    s.bind((HOST, PORT))
except socket.error as err:
    print(err)
s.listen(10)
print("Socket listening HOST: {} PORT: {}".format(HOST, PORT))
conn, addr = s.accept()
while(True):
    data = conn.recv(2048)
    if data:
        print(data.decode(encoding="UTF-8"))

