package client;

import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            // Leer el nombre del usuario
            System.out.print("Ingresa tu nombre: ");
            String userName = consoleInput.readLine();
            out.println(userName); // Enviar el nombre al servidor

            // Leer mensajes del servidor en un hilo separado
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        // Mostrar solo mensajes que no sean del propio usuario
                        if (!serverMessage.startsWith(userName + ":")) {
                            System.out.println(serverMessage);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Enviar mensajes al servidor
            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                out.println(userInput); // Enviar al servidor
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
