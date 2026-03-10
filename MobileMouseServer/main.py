import socket
import pyautogui

HOST = ''       # Listen on all interfaces
PORT = 5000

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    print("Listening for taps...")
    while True:
        conn, addr = s.accept()
        with conn:
            data = conn.recv(1024)
            if data:
                print("Tap received!")
                pyautogui.click()  # simulate left click