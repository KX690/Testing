package server;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class ClientDisconnectionTest {

    @Test
    public void testClientDisconnection() throws Exception {

        Thread serverThread = new Thread(() -> {
            try {
                Server.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();


        Thread.sleep(1000);

        // Cliente 1
        Socket client1Socket = new Socket("127.0.0.1", 12345);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(client1Socket.getInputStream()));
        PrintWriter out1 = new PrintWriter(client1Socket.getOutputStream(), true);

        // Cliente 2
        Socket client2Socket = new Socket("127.0.0.1", 12345);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(client2Socket.getInputStream()));
        PrintWriter out2 = new PrintWriter(client2Socket.getOutputStream(), true);

        // Olaf se conecta como mi cliente 1
        out1.println("Olaf");
        String response1 = in1.readLine();
        assertEquals("¡Bienvenido al chat Olaf!", response1, "Cliente Olaf no recibió el mensaje de bienvenida.");

        // Y Anna como mi cliente 2
        out2.println("Anna");
        String response2 = in2.readLine();
        assertEquals("¡Bienvenido al chat Anna!", response2, "Cliente Anna no recibió el mensaje de bienvenida.");

        // Olaf se desconecta
        client1Socket.close();

        // Anna recibe la notificación de desconexión
        String notification = in2.readLine();
        assertNotNull(notification, "Cliente 2 no recibió la notificación de desconexión.");
        assertEquals("Servidor: Olaf se ha desconectado.", notification, "La notificación de desconexión no es correcta.");


        client2Socket.close();

        serverThread.interrupt();
        serverThread.join(1000);
    }
}
