package Clientes;

import UI.TextUI;

import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);
        Demultiplexer multi = new Demultiplexer( new TaggedConnection(socket));
        Cliente c = new Cliente("","",multi);
        new TextUI(multi,c).run();
    }
}
