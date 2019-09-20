package mdpalgo.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Connection {
    private static Connection connection = null;
    private Socket socket = null;
    private static String HOST = "192.168.5.5";
    private static int PORT = 5182;

    private BufferedWriter writer;
    private BufferedReader reader;

    private Connection() {
    }

    public static Connection getConnection() {
        if (connection == null) {
            connection = new Connection();
        }
        return connection;
    }

    public void openConnection() {
        try {
            socket = new Socket(HOST, PORT);
            writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(socket.getOutputStream())));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connection established successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Socket connection failed ");
        }
    }

    public void closeConnection() {
        try {
            reader.close();
            if (socket != null) {
                socket.close();
                socket = null;
            }
            System.out.println("Connection closed!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String target, String message) {
        try {
            writer.write(target + ":" + message + "\n");
            writer.flush();
            System.out.println("Sent message:" + target + message);
        } catch (IOException e) {
            System.out.println("Send message IOException");
        } catch (Exception e) {
            System.out.println("Send message Exception");
            System.out.println(e.toString());
        }
    }

    public String receiveMessage() {
        try {
            String message = reader.readLine();

            if (message != null &&  message.length() > 0) {
                System.out.println("message received: " + message);
                return message;
            }
        } catch (IOException e) {
            System.out.println("receive message IOException");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
