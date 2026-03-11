import socket
import pyautogui

HOST = ''
PORT = 5000

last_x = None
last_y = None
sensitivity = 1.5  # adjust cursor speed

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    print("Listening for connections...")

    conn, addr = s.accept()
    print("Connected:", addr)

    with conn:
        while True:
            data = conn.recv(1024)
            if not data:
                break

            for line in data.decode().splitlines():
                parts = line.strip().split(",")

                if len(parts) != 3:
                    continue  # ignore invalid messages

                event, x_str, y_str = parts
                x = float(x_str)
                y = float(y_str)

                if event == "MOVE":

                    if last_x is None:
                        last_x = x
                        last_y = y
                        continue

                    dx = (x - last_x) * sensitivity
                    dy = (y - last_y) * sensitivity

                    pyautogui.moveRel(dx, dy)

                    last_x = x
                    last_y = y
                
                """
                elif event == "DOWN":
                    pyautogui.mouseDown()
                    last_x = x
                    last_y = y

                elif event == "UP":
                    pyautogui.mouseUp()
                    last_x = None
                    last_y = None"""