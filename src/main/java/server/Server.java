package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    static Set<String> userNames = new HashSet<>();
    private static final Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Servidor iniciado...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + socket.getInetAddress()); //++++++++
                ClientHandler clientHandler = new ClientHandler(socket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage()); //++++++++
            e.printStackTrace();
        }
    }

    public static synchronized boolean isNameUnique(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return !userNames.contains(name);
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

                while (true) {
                    String name = in.readLine();

                    if (name == null) {
                        System.out.println("Cliente desconectado antes de enviar un nombre.");
                        return;
                    }

                    System.out.println("Nombre recibido: " + name);

                    if (name.isEmpty() || !Server.isNameUnique(name)) {

                        out.println("El nombre ingresado ya está en uso o es inválido. Inténtalo de nuevo.");

                    } else {
                        synchronized (userNames) {
                            userNames.add(name);
                        }
                        userName = name;
                        out.println("¡Bienvenido al chat " + userName + "!");
                        broadcastMessage("Servidor: " + userName + " se ha unido al chat.");
                        break;
                    }
                }

                synchronized (clientHandlers) {
                    clientHandlers.add(this);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/salir")) {
                        break;
                    } else if (message.startsWith("/")) {
                        String response = handleCommand(message);
                        out.println(response);
                    } else {
                        broadcastMessage(userName + ": " + message);
                    }
                }

            } catch (IOException e) {
                System.err.println("Error con el cliente " + userName + ": " + e.getMessage());
            } finally {
                cleanUp();
            }
        }

        private void cleanUp() {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("Error cerrando el socket: " + e.getMessage());
            }
            synchronized (userNames) {
                userNames.remove(userName);
            }
            synchronized (clientHandlers) {
                clientHandlers.remove(this);
            }
            if (userName != null) { //++++++++
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

        String handleCommand(String command) {
            if (command.equalsIgnoreCase("/users")) {
                synchronized (Server.userNames) {
                    return "Usuarios conectados: " + String.join(", ", Server.userNames);
                }
            }
            return "Comando desconocido.";
        }
    }
}
