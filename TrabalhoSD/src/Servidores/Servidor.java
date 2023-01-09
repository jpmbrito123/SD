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
                    System.out.println(122);
                    String[] tokens = data.split(" ");
                    int x =  parseInt(tokens[0]);
                    int y = parseInt(tokens[1]);
                    List<List<Integer>> list =aplication.trotinetes_livres(x,y);
                    if(list.size()>0){
                        c.send(3,Integer.toString(0).getBytes());
                        String resposta = this.aplication.trotinetesToString(aplication.trotinetes_livres(x,y));
                        c.send(3,resposta.getBytes());
                    }else c.send(3,Integer.toString(-1).getBytes());
                }else if(frame.tag == 4){
                    String[] tokens = data.split(" ");
                    int x =  parseInt(tokens[0]);
                    int y = parseInt(tokens[1]);
                    String recompensas = aplication.recompensas_na_areas(x,y);
                    if (recompensas.compareTo("")!=0){
                        c.send(4,Integer.toString(0).getBytes());
                        c.send(4,recompensas.getBytes());
                    }else c.send(4,Integer.toString(-1).getBytes());
                } else if (frame.tag == 5) {
                    String[] tokens = data.split(" ");
                    int x =  parseInt(tokens[0]);
                    int y = parseInt(tokens[1]);
                    String reserva = "";
                    try {
                        this.writel.lock();
                        while (atualiza){
                            this.atualizou.await();
                        }
                        this.atualiza = true;
                        reserva = this.aplication.reserva_trotinete(x,y);
                        this.pode_atualizar.signalAll();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        this.writel.unlock();
                    }
                    if(reserva.compareTo("-1")==0){
                        c.send(5,Integer.toString(-1).getBytes());
                    }else {
                        c.send(5,Integer.toString(0).getBytes());
                        c.send(5,reserva.getBytes());
                    }
                } else if (frame.tag == 6) {
                    String[] tokens = data.split(" ");
                    int x =  parseInt(tokens[0]);
                    int y = parseInt(tokens[1]);
                    String reserva = tokens[2];
                    String[] reserva_tokens = reserva.split(" ");
                    boolean b =false;
                    try {
                        this.writel.lock();
                        while (atualiza){
                            this.atualizou.await();
                        }
                        this.atualiza = true;
                        b = this.aplication.liverta_trotinete(x,y,Integer.parseInt(reserva_tokens[0]));
                        this.pode_atualizar.signalAll();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        this.writel.unlock();
                    }
                    if (b){//falta pagamento
                        c.send(6,Integer.toString(0).getBytes());
                    }else c.send(6,Integer.toString(-1).getBytes());
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

