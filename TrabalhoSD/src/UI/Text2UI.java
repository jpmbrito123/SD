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

    private String senha = null;

    private Demultiplexer multi;

    private Thread thread = null;

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

    private void trataListaRecompenas() throws InterruptedException {
        Thread t = new Thread(() -> {
           try{
               System.out.println("Insira as suas coordenadas: ");
               int x = scin.nextInt();
               int y = scin.nextInt();
               multi.send(4, (x + " " + y).getBytes());
               byte[] reply = multi.receive(4);
               int error = Integer.parseInt(new String(reply));
               System.out.println("\n\n");
               if (error==0){
                   byte[] reply1 = multi.receive(4);
                   String aux = new String(reply1);
                   System.out.println(aux);
               }
               else
                   System.out.println("\033[0;31m" + ": Falha ao encontrar recompensas" + "\n\n\033[0m");

           }
           catch (NullPointerException | IOException | InterruptedException e){
               System.out.print(e.getMessage() + "\n\n");

           }
        });
        t.start();
        t.join();
    }

    private void trataDesativarNotificacoes() {
        try {
            multi.send(8,"Desativar Notificacao".getBytes());
            byte[] reply = multi.receive(8);
            int error = Integer.parseInt(new String(reply));
            if(error==0 && this.thread !=null){
                this.thread.interrupt();
                System.out.println("Notificaçoes desativadas");
            }
            else System.out.println("\033[0;31m" + ": Falha ao desativar notificacoes" + "\n\n\033[0m");

        }
        catch (NullPointerException | IOException | InterruptedException e){
            System.out.print(e.getMessage() + "\n\n");

        }
    }


    private void trataPedirNotificacoes() throws InterruptedException {
        try {
            System.out.println("Indique as suas coordenadas");
            int x = this.scin.nextInt();
            int y = this.scin.nextInt();
            multi.send(7,(x+" "+y).getBytes());
            byte[] reply = multi.receive(7);
            int error = Integer.parseInt(new String(reply));
            if(error==0 && this.thread == null){
                this.thread = new Thread(()->{
                    while (true){
                        try {
                            byte[] reply1 = multi.receive(9);
                            String notificacao = new String(reply1);
                            System.out.println("Nova Notificaçoes");
                            System.out.println(notificacao);

                        }catch (NullPointerException | IOException | InterruptedException e){
                            System.out.print(e.getMessage() + "\n\n");
                        }
                    }});
                this.thread.start();
                System.out.println("Notificaçoes ativadas");
            }
            else System.out.println("Notificaçoes nao ativadas");
        }
        catch (NullPointerException | IOException e){
            System.out.print(e.getMessage() + "\n\n");

        }
    }

    private void trataEstacionar() throws InterruptedException {
        Thread t = new Thread(() -> {
            try{
                System.out.println("Insira as suas coordenadas: ");
                int x = scin.nextInt();
                int y = scin.nextInt();
                multi.send(6,((x + " " + y) + " " + this.senha).getBytes());
                byte[] reply = multi.receive(6);
                int error = Integer.parseInt(new String(reply));
                System.out.println("\n\n");
                if(error==0 && this.senha!=null){
                    byte[] reply1 = multi.receive(6);
                    String preco = new String(reply1);
                    this.senha=null;
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
                int x = scin.nextInt();
                int y = scin.nextInt();
                multi.send(3,(x + " " + y).getBytes());
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
                int x = scin.nextInt();
                int y = scin.nextInt();
                multi.send(5,(x + " " + y).getBytes());
                byte[] reply = multi.receive(5);
                int error = Integer.parseInt(new String(reply));
                System.out.println("\n\n");
                if (error==0 && this.senha==null){
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
