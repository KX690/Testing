package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static Set<String> userNames = new HashSet<>();
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Servidor iniciado...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado.");
                ClientHandler clientHandler = new ClientHandler(socket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String userName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Solicitar y validar el nombre de usuario
                while (true) {
                    String name = in.readLine();

                    if (name == null || name.isBlank() || userNames.contains(name)) {
                        out.println("El nombre ingresado ya está en uso o es inválido. Inténtalo de nuevo.");
                    } else {
                        synchronized (userNames) {
                            userNames.add(name);
                        }
                        userName = name;
                        out.println("¡Bienvenido al chat, " + userName + "!");
                        broadcastMessage("Servidor: " + userName + " se ha unido al chat.");
                        break;
                    }
                }

                // Agregar este cliente a la lista de clientes activos
                synchronized (clientHandlers) {
                    clientHandlers.add(this);
                }

                // Escuchar mensajes del cliente
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/salir")) {
                        break;
                    }
                    broadcastMessage(userName + ": " + message);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Limpiar al desconectar
                try {
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (userNames) {
                    userNames.remove(userName);
                }
                synchronized (clientHandlers) {
                    clientHandlers.remove(this);
                }
                broadcastMessage("Servidor: " + userName + " se ha desconectado.");
                System.out.println("Servidor: " + userName + " se ha desconectado.");
            }
        }

        private void broadcastMessage(String message) {
            synchronized (clientHandlers) {
                for (ClientHandler client : clientHandlers) {
                    client.out.println(message);
                }
            }
        }
    }
}
