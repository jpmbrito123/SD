package Servidores;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Servidor {
    public App aplication = new App();

    public Servidor() {
        int n = aplication.tamanho;
        Random random = new Random();
        for (int i = 0;i<n*2;i++){
            int x = random.nextInt(0,n);
            int y = random.nextInt(0,n);
            Trotinete t = new Trotinete(i,x,y,true);
            aplication.adiciona_trotinete(t);
        }
    }

    public void clientes (Socket s) {
        Thread espera_notificacao = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            while (true){
                int i = in.read();
                if(i==1){
                    int x = in.read();
                    int y = in.read();
                    this.aplication.trotinetes_livres(x,y);
                }else if(1==2){
                } else if (1==3) {
                    int x = in.read();
                    int y = in.read();
                    this.aplication.reserva_trotinete(x,y);
                } else if (i==4) {
                    if (espera_notificacao == null){
                        espera_notificacao = new Thread(()->{espera_notificacoes(s);});
                        espera_notificacao.start();}
                } else if (i==5) {
                    if (espera_notificacao != null) {
                        espera_notificacao.interrupt();
                        espera_notificacao = null;}
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void espera_notificacoes(Socket s) {
    }

    private void recompensas() {
    }

    public void start() throws IOException {
        Thread Recompesas = new Thread(()->{recompensas();});
        Recompesas.start();
        ServerSocket ss = new ServerSocket(1100);
        while (true){
            Socket s = ss.accept();
            Thread cliente = new Thread(() -> {clientes(s);});
            cliente.start();
        }
    }

}

