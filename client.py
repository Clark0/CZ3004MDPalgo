import socket

# HOST = "localhost"
HOST = "192.168.5.5"
PORT = 5182
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print('Socket create')

try:
    s.connect((HOST, PORT))
except socket.error as err:
    print(err)
print("Socket listening HOST: {} PORT: {}".format(HOST, PORT))
# conn, addr = s.accept()
while(True):
    msg = "mov:" + input("Send command: ").rstrip() + "\n"
    s.sendall(msg.encode())

    # data = conn.recv(2048)
    # if data:
    #     print(data.decode(encoding="UTF-8"))

