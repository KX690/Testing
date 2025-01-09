package server;

import client.Client;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    @Test
    public void testMultipleClientsMessaging() throws Exception {
        Thread serverThread = new Thread(() -> Server.main(null));
        serverThread.start();

        //Se crea un cliente 1
        Socket client1Socket = new Socket("localhost", 12345);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(client1Socket.getInputStream()));
        PrintWriter out1 = new PrintWriter(client1Socket.getOutputStream(), true);

        //Se crea un cliente 2
        Socket client2Socket = new Socket("localhost", 12345);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(client2Socket.getInputStream()));
        PrintWriter out2 = new PrintWriter(client2Socket.getOutputStream(), true);

        //Yo me conecto como cliente 1
        out1.println("kevin");
        assertEquals("¡Bienvenido al chat kevin!", in1.readLine());

        //Hugo se conecta como el segundo cliente
        out2.println("hugo");
        assertEquals("¡Bienvenido al chat hugo!", in2.readLine());

        //Le envio mensaje a Hugo
        out1.println("Hola desde Kevin!");
        assertEquals("kevin: Hola desde Kevin!", in2.readLine()); // Cliente 2 recibe el mensaje

        //Y el me envia un mensaje tambien
        out2.println("Hola Kevin, soy hugo!");
        assertEquals("Servidor: hugo se ha unido al chat.", in1.readLine()); // Cliente 1 recibe el mensaje


        client1Socket.close();
        client2Socket.close();
        serverThread.interrupt();
    }
}
