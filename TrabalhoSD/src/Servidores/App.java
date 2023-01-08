package Servidores;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Math.abs;
import static java.lang.Math.nextUp;

public class App {
    List<List<List<Trotinete>>> mapa= new ArrayList<>();

    List<Trotinete> trotinetes = new ArrayList<>();

    public ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Lock readlock = lock.readLock();

    public Lock writlock = lock.writeLock();

    final Integer distancia = 2;
    final Integer tamanho = 20;

    public App() {
        for(int i = 0; i<this.tamanho; i++){
            this.mapa.add(new ArrayList<>());
            for(int j = 0; j<this.tamanho; j++){
                this.mapa.get(i).add(new ArrayList<Trotinete>());
            }
        }
    }

    public void adiciona_trotinete(Trotinete t){
        int x = t.getCorX();
        int y = t.getCorY();
        try {
            this.writlock.lock();
            trotinetes.add(t);
            mapa.get(y).get(x).add(t);
        }finally {
            this.writlock.unlock();
        }
    }
    public List<List<Integer>> trotinetes_livres(int x,int y){
        List<List<Integer>> livres= new ArrayList<>();
        try {
            this.readlock.lock();
            for(int l=y-this.distancia;l<y+this.distancia && l<this.tamanho;l++){
                if (l>=0){
                    for(int c=x-this.distancia;c<x+this.distancia && c<this.tamanho;c++){
                        if(c>=0 && this.distancia>=abs(l-y)+abs(c-x) ){
                            List<Trotinete> trotinetes = this.mapa.get(l).get(c);
                            for (Trotinete trotinete: trotinetes) {
                                if (trotinete.isLivre()) {
                                    List<Integer> cords = new ArrayList<>(c);
                                    cords.add(l);
                                    livres.add(cords);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }finally {
            this.readlock.unlock();
        }
        return livres;
    }

    public void recompensas(List<List<Integer>> A,List<List<Integer>> B){
        List<Trotinete> trotinetes;
        try {
            for(int y=0;y<this.tamanho;y++){
                for(int x=0;x<this.tamanho;x++){
                    trotinetes = this.mapa.get(y).get(x);
                    int n_livre=0;
                    for(Trotinete t:trotinetes){
                        if(t.isLivre())n_livre++;
                    }
                    if (n_livre>1){
                        List<Integer> cords = new ArrayList<>(x);
                        cords.add(y);
                        A.add(cords);
                    } else if (n_livre==0) {
                        boolean b = true;
                        for(int l=y-this.distancia;l<y+this.distancia && l<this.tamanho;l++){
                            if (l>=0){
                                for(int c=x-this.distancia;c<x+this.distancia && c<this.tamanho;c++){
                                    if(c>=0 && this.distancia>=abs(l-y)+abs(c-x) ){
                                        List<Trotinete> trotinetesss = this.mapa.get(l).get(c);
                                        for (Trotinete trotinete: trotinetesss) {
                                            if (trotinete.isLivre()) {
                                                b = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (!b) break;
                                }
                            }
                            if(!b) break;
                        }
                        if(b) {
                            List<Integer> cords = new ArrayList<>(x);
                            cords.add(y);
                            B.add(cords);
                        }
                    }
                }
            }
        }finally {
            this.readlock.unlock();
        }

    }

    public int reserva_trotinete(int x ,int y){
        Trotinete t = null;
        try {
            this.writlock.lock();
            if (x >= 0 && x < this.tamanho && y >= 0 && y < this.tamanho) {
                for (int n = 0; n < this.distancia; n++) {
                    for (int l = y - n; l < y + n && l < n; l++) {
                        if (l >= 0) {
                            for (int c = x - n; c < x + n && c < n; c++) {
                                if (c >= 0 && n >= abs(l - y) + abs(c - x)) {
                                    List<Trotinete> trotinetes = this.mapa.get(l).get(c);
                                    for (Trotinete trotinete : trotinetes) {
                                        t = trotinete;
                                        t.reserva();
                                        break;
                                    }
                                }
                                if (t != null) break;
                            }
                        }
                        if (t != null) break;
                    }
                    if (t != null) break;
                }
            }
            int codigo = -1;
            if (t != null) codigo = t.getId();
            return codigo;
        }finally {
            this.writlock.unlock();
        }
    }

    public void liverta_trotinete(int x, int y,int codigo) {
        try {
            this.writlock.lock();
            List<Trotinete> trotinetes = this.mapa.get(y).get(x);
            for(Trotinete t:trotinetes){
                if(t.getId()==codigo){
                    t.setLivre(true);
                    break;
                }
            }
        }finally {
            this.writlock.unlock();
        }
    }
}
