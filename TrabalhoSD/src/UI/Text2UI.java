package UI;

import Clientes.*;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class Text2UI {

    // Menus da aplicação
    private Menu menu;

    // Scanner para leitura
    private Scanner scin;

    private String senha;
    
    private Demultiplexer multi;

    private Thread thread = new Thread(this::trataPedirNotificacoes);

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
        this.thread.start();
        try {
            multi.send(8,"Desativar Notificacao".getBytes());
            byte[] reply = multi.receive(8);
            int error = Integer.parseInt(new String(reply));
            if(error==0){
                this.thread.join();
                System.out.println("Notificaçoes desativadas");
            }
            else System.out.println("\033[0;31m" + ": Falha ao desativar notificacoes" + "\n\n\033[0m");

        }
        catch (NullPointerException | IOException | InterruptedException e){
            System.out.print(e.getMessage() + "\n\n");

        }
    }
    

    private void trataPedirNotificacoes() {
        this.thread.start();
        try {
            multi.send(7,"Ativar Notificacao".getBytes());
            byte[] reply = multi.receive(7);
            int error = Integer.parseInt(new String(reply));
            if(error==0){
                System.out.println("Notificaçoes ativadas");
            }
            else this.thread.join();

        }
        catch (NullPointerException | IOException | InterruptedException e){
            System.out.print(e.getMessage() + "\n\n");

        }
    }

    private void trataEstacionar() throws InterruptedException {
        Thread t = new Thread(() -> {
            try{
                System.out.println("Insira as suas coordenadas: ");
                String coordenadas = scin.nextLine();
                multi.send(6,(coordenadas + " " + this.senha).getBytes());
                byte[] reply = multi.receive(6);
                int error = Integer.parseInt(new String(reply));
                System.out.println("\n\n");
                if(error==0){
                    byte[] reply1 = multi.receive(6);
                    String preco = new String(reply1);
                    System.out.println("O valor da viagem é: " + preco + "$");
                }
                else
                    System.out.println("\033[0;31m" + ": Falha ao estacionar a trotinete" + "\n\n\033[0m");


            }
            catch(NullPointerException | IOException | InterruptedException e){
                System.out.print(e.getMessage() + "\n\n");
            }

        });
        t.start();
        t.join();
    }

    public void trataTrotinetesLivres() throws InterruptedException {
        Thread t = new Thread(() -> {
            try{
                System.out.println("Insira as suas coordenadas:");
                String coordenadas = scin.nextLine();
                multi.send(3,coordenadas.getBytes());
                byte[] reply = multi.receive(3);
                int error = Integer.parseInt(new String(reply));
                if (error==0){
                    byte[] reply1 = multi.receive(3);
                    String aux = new String(reply1);
                    System.out.println(aux);
                }
                else
                    System.out.println("\033[0;31m" + ": Falha ao encontrar trotinetes livres" + "\n\n\033[0m");
            }
            catch (NullPointerException | IOException | InterruptedException e) {
                System.out.print(e.getMessage() + "\n\n");
            } {

            }
        });
        t.start();
        t.join();
    }
    
    public void trataReservarTrotinete() throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                System.out.println("Indique que trotinete pretende reservar");
                String coordenadas = scin.nextLine();
                multi.send(5,coordenadas.getBytes());
                byte[] reply = multi.receive(5);
                int error = Integer.parseInt(new String(reply));
                System.out.println("\n\n");
                if (error==0){
                    byte[] reply1 = multi.receive(5);
                    String[] s = new String(reply1).split(" ");
                    this.senha = new String(reply1);
                    System.out.println("Reservou a trotinete: " + s[0] + " \n No instante: " + s[1] + "|" + s[2]);
                }
                else System.out.println("\033[0;31m" +": Falha ao reservar trotinete" + "\n\n\033[0m");
            }
            catch(NullPointerException | IOException | InterruptedException e){
                System.out.println(e.getMessage() + "\n\n");
            }
        });
        t.start();
        t.join();
    }

    /**
     * Executa o menu principal e invoca o método correspondente à opção seleccionada.
     */
    public void run() {
        this.menu.run();
        System.out.println("Bota que tem...");
    }


}
