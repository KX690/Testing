package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {

            // Leer el nombre del usuario
            System.out.print("Ingresa tu nombre: ");
            String userName = input.nextLine() ;
            out.println(userName);

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
            while ((userInput = input.nextLine()) != null) {
                out.println(userInput); // Enviar al servidor
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
