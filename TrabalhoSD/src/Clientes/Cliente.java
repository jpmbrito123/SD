package Clientes;
import java.io.*;
import java.net.Socket;

public class Cliente {
    private String nome;
    private String password;
    private Demultiplexer multi;

    public Cliente(String nome,  String password, Demultiplexer multi) {
        this.nome = nome;
        this.password = password;
        this.multi= multi;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);

    }
}