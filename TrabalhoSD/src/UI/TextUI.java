package UI;

import Clientes.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TextUI {


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
    private void trataRegistar() {
        try {
            System.out.println("Inserir nome utilizador: ");
            String nomeUtilizador = scin.nextLine();

            System.out.println("Palavra passe do novo utilizador: ");
            String passe = scin.nextLine();
            multi.send(2,(nomeUtilizador + " "+ passe).getBytes());
            System.out.println("Utilizador registado adicionado");

        }catch (NullPointerException | IOException e) {
            System.out.print(e.getMessage() + "\n\n");
    }
    private void trataFazerLogin() {
        try {
            System.out.println("Insira nome utilizador: ");
            String nome = scin.nextLine();
            if (this.modelUtilizador.existeUtilizador(nome)) {
                System.out.println("Insira palavra passe: ");
                String passe = scin.nextLine();
                if (this.modelUtilizador.passecorreta(nome,passe)){
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