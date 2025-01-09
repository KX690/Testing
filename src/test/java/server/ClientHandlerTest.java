package server;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import server.Server.*;

import static org.junit.jupiter.api.Assertions.*;

class ClientHandlerTest {

    @Test
    void testUsersCommandReturnsUserList() throws IOException {
        // Configuración del servidor
        Server.userNames.add("kevin");
        Server.userNames.add("hugo");

        // Simula una interacción cliente-servidor
        Socket mockSocket = new Socket();
        ClientHandler clientHandler = new ClientHandler(mockSocket);

        // Simula la respuesta al comando /users
        String userList = clientHandler.handleCommand("/users");
        assertEquals("Usuarios conectados: kevin, hugo", userList);
    }
}
