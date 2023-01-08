package Servidores;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.*;

public class Servidor {
    public App aplication = new App();

    List<List<Integer>> A= new ArrayList<>();
    List<List<Integer>> B = new ArrayList<>();

    public ReentrantReadWriteLock lockc = new ReentrantReadWriteLock();

    public Lock readl = lockc.readLock();
    public Lock writel = lockc.writeLock();
    public Condition pode_atualizar = writel.newCondition();

    public Condition atualizou = writel.newCondition();
    public boolean atualiza;


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
                    try {
                        this.writel.lock();
                        while (atualiza){
                            this.atualizou.await();
                        }
                        this.atualiza = true;
                        this.aplication.reserva_trotinete(x,y);
                        this.pode_atualizar.signalAll();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        this.writel.unlock();
                    }
                } else if (i==4) {
                    int x = in.read();
                    int y = in.read();
                    int codigo = in.read();
                    try {
                        this.writel.lock();
                        while (atualiza){
                            this.atualizou.await();
                        }
                        this.atualiza = true;
                        this.aplication.liverta_trotinete(x,y,codigo);
                        this.pode_atualizar.signalAll();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        this.writel.unlock();
                    }
                } else if (i==5) {
                if (espera_notificacao == null){
                        espera_notificacao = new Thread(()->{espera_notificacoes(out);});
                        espera_notificacao.start();}
                } else if (i==6) {
                    if (espera_notificacao != null) {
                        espera_notificacao.interrupt();
                        espera_notificacao = null;}
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void espera_notificacoes(PrintWriter out) {
        while (true){
            try {
                this.readl.lock();
                while (atualiza){
                    this.atualizou.await();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                this.readl.unlock();
            }
        }
    }

    private void recompensas(){
        while (true){
            try {
                writel.lock();
                while (!this.atualiza){
                    this.pode_atualizar.await();
                }
                this.atualiza=false;
                this.A.clear();
                this.B.clear();
                this.aplication.recompensas(A,B);
                this.atualizou.signalAll();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                writel.lock();
            }
        }
    }

    public void start() throws IOException {
        Thread Recompesas = new Thread(this::recompensas);
        Recompesas.start();
        ServerSocket ss = new ServerSocket(1100);
        while (true){
            Socket s = ss.accept();
            Thread cliente = new Thread(() -> {clientes(s);});
            cliente.start();
        }
    }

}

