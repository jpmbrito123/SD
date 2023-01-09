package Servidores;

import Clientes.TaggedConnection;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.*;

import static java.lang.Integer.parseInt;

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

    public void clientes (Socket s) throws IOException {
        TaggedConnection c = new TaggedConnection(s);
        Thread espera_notificacao = null;
        try {
            while (true){
                TaggedConnection.Frame frame = c.receive();
                String data = new String(frame.data);
                if(frame.tag == 1){
                }else if(frame.tag == 2) {
                }else if(frame.tag == 3){
                    String[] tokens = data.split(" ");
                    int x =  parseInt(tokens[0]);
                    int y = parseInt(tokens[1]);
                    this.aplication.trotinetesToString(aplication.trotinetes_livres(x,y));
                }else if(frame.tag == 4){
                } else if (frame.tag == 5) {
                    String[] tokens = data.split(" ");
                    int x =  parseInt(tokens[0]);
                    int y = parseInt(tokens[1]);
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
                } else if (frame.tag == 6) {
                    String[] tokens = data.split(" ");
                    int x =  parseInt(tokens[0]);
                    int y = parseInt(tokens[1]);
                    int codigo = parseInt(tokens[2]);
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
                } else if (frame.tag == 7) {
                    if (espera_notificacao == null){
                        espera_notificacao = new Thread(()->{espera_notificacoes(c);});
                        espera_notificacao.start();
                        c.send(7,Integer.toString(0).getBytes());
                    }else {
                        c.send(7,Integer.toString(-1).getBytes());
                    }
                } else if (frame.tag == 8) {
                    if (espera_notificacao != null) {
                        espera_notificacao.interrupt();
                        espera_notificacao = null;
                        c.send(8,Integer.toString(0).getBytes());
                    }else {
                        c.send(8,Integer.toString(-1).getBytes());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void espera_notificacoes(TaggedConnection c) {
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
            Thread cliente = new Thread(() -> {
                try {
                    clientes(s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            cliente.start();
        }
    }

}

