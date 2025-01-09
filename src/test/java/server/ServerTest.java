package server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {

    @BeforeEach
    public void setup() {
        Server.userNames.clear(); //Limpiar nombres de usuario antes de cada prueba
    }

    @Test
    public void testIsNameUnique_nameAlreadyExists() {
        //Simula agregar un nombre al conjunto de usuarios
        Server.userNames.add("kevin");

        //Prueba un nombre existente
        assertFalse(Server.isNameUnique("kevin"), "El nombre no debería ser único.");
    }

    @Test
    public void testIsNameUnique_nameIsUnique() {
        //Prueba un nombre único
        assertTrue(Server.isNameUnique("hugo"), "El nombre debería ser único.");
    }

    @Test
    public void testIsNameUnique_emptyOrNullName() {
        //Prueba nombres inválidos
        assertFalse(Server.isNameUnique(null), "El nombre null no debería ser aceptado.");
        assertFalse(Server.isNameUnique(""), "El nombre vacío no debería ser aceptado.");
    }
}
