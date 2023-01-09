package UI;

import Clientes.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Text2UI {

    // Menus da aplicação
    private Menu menu;

    // Scanner para leitura
    private Scanner scin;
    
    private Demultiplexer multi;

    /**
     * Construtor.
     * <p>
     * Cria os menus e a camada de negócio.
     */
    public Text2UI(Demultiplexer multi) {
        this.multi = multi;
        // Criar o menu
        this.menu = new Menu(new String[]{
                "Ver Trotinetes Livres",
                "Lista de Recompensas",
                "Reservar Trotinetes",
                "Estacionar",
                "Pedir Notificações",
                "Desativar Notificações"

        });
        this.menu.setHandler(1, this::trataTrotinetesLivres);
        this.menu.setHandler(2, this::trataListaRecompenas);
        this.menu.setHandler(3, this::trataReservarTrotinete);
        this.menu.setHandler(4, this::trataEstacionar);
        this.menu.setHandler(5, this::trataPedirNotificacoes);
        this.menu.setHandler(6, this::trataDesativarNotificacoes);
        scin = new Scanner(System.in);
    }

    private void trataListaRecompenas() {
    }

    private void trataDesativarNotificacoes() {
    }

    private void trataPedirNotificacoes() {
    }

    private void trataEstacionar() {
    }

    public void trataTrotinetesLivres(){
        Thread t = new Thread(() -> {
            try{
                System.out.println("Insira as suas coordenadas:");
                String coordenadas = scin.nextLine();
                multi.send(3,coordenadas.getBytes());
                byte[] reply = multi.receive(3);
                int error = Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(3);
                System.out.println("\n");
                if (error==0){
                    String aux = new String(reply1);
                    System.out.println(aux);
                }
                else
                    System.out.println("\033[0;31m" + new String(reply1) + ": Falha ao apresentar coordenadas" + "\n\n\033[0m");
            }
            catch (NullPointerException | IOException | InterruptedException e) {
                System.out.print(e.getMessage() + "\n\n");
            } {

            }
        });
    }
    
    public void trataReservarTrotinete(){
        Thread t = new Thread(() -> {
            try {
                System.out.println("Indique que trotinete pretende reservar");
                String coordenadas = scin.nextLine();
                multi.send(5,coordenadas.getBytes());
                byte[] reply = multi.receive(5);
                int error = Integer.parseInt(new String(reply));
                byte[] reply1 = multi.receive(5);
                System.out.println("\n\n");
                if (error==0){
                    String[] s = new String(reply1).split(" ");
                    System.out.println("Reservou a trotinete: " + s[0] + "\nNo instante: " + s[1]);
                }
                else System.out.println("\033[0;31m" + new String(reply1) + ": Falha ao reservar trotinete" + "\n\n\033[0m");
            }
            catch(NullPointerException | IOException | InterruptedException e){
                System.out.println(e.getMessage() + "\n\n");
            }
        });




    }

    /**
     * Executa o menu principal e invoca o método correspondente à opção seleccionada.
     */
    public void run() {
        this.menu.run();
        System.out.println("Bota que tem...");
    }


}
