package mdpalgo.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {
    public static final String EX_START = "algo:explore";   // Android --> PC
    public static final String FP_START = "FP_START";   // Android --> PC
    public static final String MAP = "MAP";             // PC --> Android
    public static final String BOT_POS = "BOT_POS";     // PC --> Android
    public static final String BOT_START = "BOT_START"; // PC --> Arduino
    public static final String INSTR = "INSTR";         // PC --> Arduino
    public static final String SDATA = "SDATA";         // Arduino --> PC

    private static Connection connection = null;
    private static Socket socket = null;

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
        System.out.println("Opening connection...");

        try {
            // String HOST = "127.0.0.1";
            String HOST = "192.168.5.5";
            int PORT = 5182;
            socket = new Socket(HOST, PORT);

            writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(socket.getOutputStream())));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("openConnection() --> " + "Connection established successfully!");

            return;
        } catch (UnknownHostException e) {
            System.out.println("openConnection() --> UnknownHostException");
        } catch (IOException e) {
            System.out.println("openConnection() --> IOException");
        } catch (Exception e) {
            System.out.println("openConnection() --> Exception");
            System.out.println(e.toString());
        }

        System.out.println("Failed to establish connection!");
    }

    public void closeConnection() {
        System.out.println("Closing connection...");

        try {
            reader.close();

            if (socket != null) {
                socket.close();
                socket = null;
            }
            System.out.println("Connection closed!");
        } catch (IOException e) {
            System.out.println("closeConnection() --> IOException");
        } catch (NullPointerException e) {
            System.out.println("closeConnection() --> NullPointerException");
        } catch (Exception e) {
            System.out.println("closeConnection() --> Exception");
            System.out.println(e.toString());
        }
    }

    public void sendMsg(String msg, String msgType) {
        System.out.println("Sending a message...");

        try {
            String outputMsg;
            if (msgType.equals(INSTR) || msgType.equals(BOT_START)) {
                outputMsg = "AR" + msg + "\n";
            } else if (msgType.equals(MAP) || msgType.equals(BOT_POS)) {
                outputMsg = "AN" + "," + msg + "\n";
            } else {
                outputMsg = msg;
            }

            System.out.println("Sending out message:\n" + outputMsg);
            writer.write(outputMsg);
            writer.flush();
        } catch (IOException e) {
            System.out.println("sendMsg() --> IOException");
        } catch (Exception e) {
            System.out.println("sendMsg() --> Exception");
            System.out.println(e.toString());
        }
    }

    public String recvMsg() {
        System.out.println("Receiving a message...");

        try {
            StringBuilder sb = new StringBuilder();
            String input = reader.readLine();

            if (input != null && input.length() > 0) {
                sb.append(input);
                System.out.println("message received: " + sb.toString());
                return sb.toString();
            }
        } catch (IOException e) {
            System.out.println("recvMsg() --> IOException");
        } catch (Exception e) {
            System.out.println("recvMsg() --> Exception");
            System.out.println(e.toString());
        }

        return null;
    }

    public boolean isConnected() {
        return socket.isConnected();
    }
}
