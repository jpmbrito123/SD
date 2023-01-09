package Servidores;

import Clientes.TaggedConnection;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.*;

import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;

public class Servidor {
    public App aplication = new App();

    public HashMap<String,String> utilizadores = new HashMap<>();

    List<List<Integer>> A= new ArrayList<>();
    List<List<Integer>> B = new ArrayList<>();

    public ReentrantLock lock = new ReentrantLock();
    public Condition pode_atualizar = lock.newCondition();

    public Condition atualizou = lock.newCondition();

    public Condition notifica = lock.newCondition();

    public boolean atualiza = false;

    public int re_es = 0;


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
                    boolean b = false;
                    String[] tokens = data.split(" ");
                    try {
                        this.lock.lock();
                        if (this.utilizadores.containsKey(tokens[0])){
                            if(this.utilizadores.get(tokens[0]).equals(tokens[1])){
                                b =true;
                            }
                        }
                    }finally {
                        this.lock.unlock();
                    }
                    if (b) c.send(1,"0".getBytes());
                    else c.send(1,"-1".getBytes());
                }else if(frame.tag == 2) {
                    boolean b = false;
                    String[] tokens = data.split(" ");
                    try {
                        this.lock.lock();
                        if (!this.utilizadores.containsKey(tokens[0])){
                            b=true;
                            this.utilizadores.put(tokens[0],tokens[1]);
                        }
                    }finally {
                        this.lock.unlock();
                    }
                    if (b) c.send(2,"0".getBytes());
                    else c.send(2,"-1".getBytes());
                }else if(frame.tag == 3){
                    String[] tokens = data.split(" ");
                    int x =  parseInt(tokens[0]);
                    int y = parseInt(tokens[1]);
                    List<List<Integer>> list =aplication.trotinetes_livres(x,y);
                    if(list.size()>0){
                        c.send(3,"0".getBytes());
                        String resposta = this.aplication.trotinetesToString(aplication.trotinetes_livres(x,y));
                        c.send(3,resposta.getBytes());
                    }else c.send(3,"-1".getBytes());
                }else if(frame.tag == 4){
                    String[] tokens = data.split(" ");
                    int x =  parseInt(tokens[0]);
                    int y = parseInt(tokens[1]);
                    String recompensas = aplication.recompensas_na_areas(x,y);
                    if (recompensas.compareTo("")!=0){
                        c.send(4,"0".getBytes());
                        c.send(4,recompensas.getBytes());
                    }else c.send(4,"-1".getBytes());
                } else if (frame.tag == 5) {
                    String[] tokens = data.split(" ");
                    int x =  parseInt(tokens[0]);
                    int y = parseInt(tokens[1]);
                    String reserva = "";
                    try {
                        this.lock.lock();
                        this.re_es++;
                        while (atualiza){
                            this.atualizou.await();
                        }
                        this.atualiza = true;
                        this.re_es--;
                        reserva = this.aplication.reserva_trotinete(x,y);
                        this.pode_atualizar.signalAll();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        this.lock.unlock();
                    }
                    if(reserva.compareTo("-1")==0){
                        c.send(5,"-1".getBytes());
                    }else {
                        c.send(5,"0".getBytes());
                        c.send(5,reserva.getBytes());
                    }
                } else if (frame.tag == 6) {
                    String[] tokens = data.split(" ");
                    int x =  parseInt(tokens[0]);
                    int y = parseInt(tokens[1]);
                    String reserva = tokens[2];
                    String[] reserva_tokens = reserva.split(" ");
                    List<Integer> cors_ant = null;
                    boolean recompensa= false;
                    try {
                        this.lock.lock();
                        this.re_es++;
                        while (atualiza){
                            this.atualizou.await();
                        }
                        this.atualiza = true;
                        this.re_es--;
                        cors_ant = this.aplication.liverta_trotinete(x,y,Integer.parseInt(reserva_tokens[0]));

                        if (cors_ant != null) {
                            for(List<Integer>as:A){
                                if(as.get(0) == cors_ant.get(0) && as.get(1)==as.get(1)){
                                    for(List<Integer>bs:B){
                                        if (bs.get(0) == x && bs.get(1) == y) {
                                            recompensa = true;
                                            break;
                                        }
                                    }
                                }
                                if(recompensa) break;
                            }
                        }
                        this.pode_atualizar.signalAll();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        this.lock.unlock();
                    }
                    if (cors_ant!=null){
                        int distancia = abs(cors_ant.get(0)-x) + abs(cors_ant.get(1)-y);
                        long tempo = getDifferenceInMinutes(tokens[3]+" "+tokens[4]);
                        double preco= tempo * 0.1 + distancia * 0.1;
                        if (recompensa) preco = preco*0.8;
                        c.send(6,"0".getBytes());
                        c.send(6,Double.toString(preco).getBytes());
                    }else c.send(6,"-1".getBytes());
                } else if (frame.tag == 7) {
                    if (espera_notificacao == null){
                        String[] tokens = data.split(" ");
                        int x =  parseInt(tokens[0]);
                        int y = parseInt(tokens[1]);
                        espera_notificacao = new Thread(()->{espera_notificacoes(c,x,y);});
                        espera_notificacao.start();
                        c.send(7,"0".getBytes());
                    }else {
                        c.send(7,"-1".getBytes());
                    }
                } else if (frame.tag == 8) {
                    if (espera_notificacao != null) {
                        espera_notificacao.interrupt();
                        espera_notificacao = null;
                        c.send(8,"0".getBytes());
                    }else {
                        c.send(8,"-1".getBytes());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void espera_notificacoes(TaggedConnection c,int x,int y) {
        while (true){
            try {
                this.lock.lock();
                this.notifica.await();
                StringBuilder notificaçao = new StringBuilder();
                for(List<Integer> cords_atual:this.A){
                    if(abs(cords_atual.get(0) -x)+abs(cords_atual.get(1)-y)<=this.aplication.distancia){
                        for (List<Integer> cords_prox:this.B){
                            notificaçao.append("Origem:").append("(").append(cords_atual.get(0)).append(",").append(cords_atual.get(1)).append(")").append(" Destino:").append("(").append(cords_prox.get(0)).append(",").append(cords_prox.get(1)).append(")").append("\n");
                        }
                    }
                }
                if(!notificaçao.toString().equals("")){
                    c.send(9,notificaçao.toString().getBytes());}
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            } finally {
                this.lock.unlock();
            }
        }
    }

    private void recompensas(){
        while (true){
            try {
                lock.lock();
                this.atualiza=false;
                this.A.clear();
                this.B.clear();
                this.aplication.recompensas(A,B);
                if(this.re_es>0) this.atualizou.signalAll();
                else this.notifica.signalAll();
                while (!this.atualiza){
                    this.pode_atualizar.await();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.lock();
            }
        }
    }

    public long getDifferenceInMinutes(String date) {
        SimpleDateFormat format = new SimpleDateFormat("HH/mm/ss dd/MM/yyyy");
        Date d = null;
        try {
            d = format.parse(date);
        } catch (ParseException e) {
            // Tratamento da exceção
            System.out.println("Erro ao converter a string para data: " + e.getMessage());
            return -1;
        }
        long diff = new Date().getTime() - d.getTime();
        return diff / (60 * 1000) % 60;
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

