package UI;

import Clientes.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TextUI {

    private Cliente c;
    private Demultiplexer multi;
    // Menus da aplicação
    private Menu menu;

    // Scanner para leitura
    private Scanner scin;

    /**
     * Construtor.
     * <p>
     * Cria os menus e a camada de negócio.
     */
    public TextUI(Demultiplexer multi) {
        this.multi=multi;
        // Criar o menu
        this.menu = new Menu(new String[]{
                "Login",
                "Registar",

        });
        this.menu.setHandler(1, this::trataFazerLogin);
        this.menu.setHandler(2, this::trataRegistar);

        scin = new Scanner(System.in);
    }

    /**
     * Executa o menu principal e invoca o método correspondente à opção seleccionada.
     */
    public void run() {
        this.menu.run();
        System.out.println("Bota que tem...");
    }

    // Métodos auxiliares
    private void trataRegistar() throws InterruptedException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        Thread t = new Thread(() -> {
            try {
                System.out.println("Inserir nome utilizador: ");
                String nomeUtilizador = scin.nextLine();

                System.out.println("Palavra passe do novo utilizador: ");
                String passe = scin.nextLine();
                multi.send(2,(nomeUtilizador + " "+ passe).getBytes());
                byte[] reply = multi.receive(2);
                int error =  Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(2);
                if(error==0){
                    System.out.println(new String(reply1) + "\n\n");
                    c.setNome(nomeUtilizador);
                    new Text2UI().run();
                }else{
                    System.out.print("\033[0;31m" + new String(reply1) + ": Registo não efetuado!!" + "\n\n\033[0m");
                }
            }catch (NullPointerException | IOException |InterruptedException e) {
                System.out.print(e.getMessage() + "\n\n");
            }
        });
        t.start();
        t.join();
    }
    private void trataFazerLogin() {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        Thread t = new Thread(()-> {
            try {
            System.out.println("Insira nome utilizador: ");
            String nome = scin.nextLine();

                System.out.println("Insira palavra passe: ");
                String passe = scin.nextLine();
                    System.out.println("Login efetuado com sucesso");
                    new Text2UI().run();
                }else{
                    System.out.println("Palavra passe errada");
                }
            } else {
                System.out.println("Esse nome de utilizador não existe!");
            }
        }
        catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }
}