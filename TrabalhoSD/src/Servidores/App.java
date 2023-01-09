package Servidores;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
        if(x >= 0 && x < this.tamanho && y >= 0 && y < this.tamanho){
            try {
                this.writlock.lock();
                trotinetes.add(t);
                mapa.get(y).get(x).add(t);
            }finally {
                this.writlock.unlock();
            }
        }else {
            System.out.println("cordenadas invalidas");
        }
    }
    public List<List<Integer>> trotinetes_livres(int x,int y){
        List<List<Integer>> livres= new ArrayList<>();
        try {
            this.readlock.lock();
            for(int l=y-this.distancia;l<=y+this.distancia && l<this.tamanho;l++){
                if (l>=0){
                    for(int c=x-this.distancia;c<=x+this.distancia && c<this.tamanho;c++){
                        if(c>=0 && this.distancia>=abs(l-y)+abs(c-x) ){
                            List<Trotinete> trotinetes_1 = this.mapa.get(l).get(c);
                            for (Trotinete trotinete: trotinetes_1) {
                                if (trotinete.isLivre()) {
                                    List<Integer> cords = new ArrayList<>();
                                    cords.add(c);
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
            this.readlock.lock();
            for(int y=0;y<this.tamanho;y++){
                for(int x=0;x<this.tamanho;x++){
                    List<Trotinete> trotinetes_1 = this.mapa.get(y).get(x);
                    int n_livre=0;
                    for(Trotinete t:trotinetes_1){
                        if(t.isLivre())n_livre++;
                    }
                    if (n_livre>1){
                        List<Integer> cords = new ArrayList<>();
                        cords.add(x);
                        cords.add(y);
                        A.add(cords);
                    } else if (n_livre==0) {
                        boolean b = true;
                        for(int l=y-this.distancia;l<=y+this.distancia && l<this.tamanho;l++){
                            if (l>=0){
                                for(int c=x-this.distancia;c<=x+this.distancia && c<this.tamanho;c++){
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
                            List<Integer> cords = new ArrayList<>();
                            cords.add(x);
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

    public String recompensas_na_areas(int X,int Y){
        List<List<Integer>> A = new ArrayList<>();
        List<List<Integer>> B = new ArrayList<>();
        try {
            this.readlock.lock();
            for(int l=Y-this.distancia;l<=Y+this.distancia && l<this.tamanho;l++) {
                if (l >= 0) {
                    for (int c = X - this.distancia; c <= X + this.distancia && c < this.tamanho; c++) {
                        if (c >= 0 && this.distancia >= abs(l - Y) + abs(c - X)) {
                            List<Trotinete> trotinetes_1 = this.mapa.get(l).get(c);
                            int n_livre=0;
                            for(Trotinete t:trotinetes_1){
                                if(t.isLivre())n_livre++;
                            }
                            if (n_livre>1){
                                List<Integer> cords = new ArrayList<>();
                                cords.add(c);
                                cords.add(l);
                                A.add(cords);
                            }
                        }
                    }
                }
            }

            for(int y=0;y<this.tamanho;y++){
                for(int x=0;x<this.tamanho;x++){
                    List<Trotinete> trotinetes_1 = this.mapa.get(y).get(x);
                    int n_livre=0;
                    for(Trotinete t:trotinetes_1){
                        if(t.isLivre())n_livre++;
                    }
                    if (n_livre==0) {
                        boolean b = true;
                        for(int l=y-this.distancia;l<=y+this.distancia && l<this.tamanho;l++){
                            if (l>=0){
                                for(int c=x-this.distancia;c<=x+this.distancia && c<this.tamanho;c++){
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
                            List<Integer> cords = new ArrayList<>();
                            cords.add(x);
                            cords.add(y);
                            B.add(cords);
                        }
                    }
                }
            }
        }finally {
            this.readlock.unlock();
        }
        String recompensas = "";
        for (List<Integer> origem :A){
            for (List<Integer> destino :B){
                recompensas = recompensas + "Origem:" + "("+origem.get(0)+","+origem.get(1)+")" +" Destino:"+ "("+destino.get(0)+","+destino.get(1)+")"+"\n";
            }
        }
        return recompensas;
    }

    public String reserva_trotinete(int x ,int y){
        Trotinete t = null;
        int codigo = -1;
        String reserva = "";
        try {
            this.writlock.lock();
            if (x >= 0 && x < this.tamanho && y >= 0 && y < this.tamanho) {
                for (int n = 0; n <= this.distancia; n++) {
                    for (int l = y - n; l <= y + n && l < this.tamanho; l++) {
                        if (l >= 0) {
                            for (int c = x - n; c <= x + n && c < this.tamanho; c++) {
                                if (c >= 0 && n >= abs(l - y) + abs(c - x)) {
                                    List<Trotinete> trotinetes_1 = this.mapa.get(l).get(c);
                                    for (Trotinete trotinete : trotinetes_1) {
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
            if (t != null) codigo = t.getId();
        }finally {
            this.writlock.unlock();
        }
        reserva = reserva + codigo;
        if (codigo!=-1){
            Date dataHoraAtual = new Date();
            String data = new SimpleDateFormat("dd/MM/yyyy").format(dataHoraAtual);
            String hora = new SimpleDateFormat("HH/mm/ss").format(dataHoraAtual);
            reserva = reserva +" "+ hora+" " +data;
        }
        return reserva;
    }

    public List<Integer> liverta_trotinete(int x, int y,int codigo) {
        List<Integer>cords = new ArrayList<>();
        boolean b = false;
        if (x>=0 && y>=0 && x<this.tamanho && y<this.tamanho) {
            try {
                this.writlock.lock();
                for (Trotinete t : this.trotinetes) {
                    if (t.getId() == codigo) {
                        List<Trotinete> trotinetes_1 = this.mapa.get(t.getCorY()).get(t.getCorX());
                        for (Trotinete ts : trotinetes_1) {
                            if (ts.getId() == codigo) {
                                ts.setLivre(true);
                                break;
                            }
                        }
                        int X = t.getCorX();
                        int Y = t.getCorY();
                        cords.add(X);
                        cords.add(Y);
                        this.mapa.get(Y).get(X).remove(t);
                        t.setCorX(x);
                        t.setCorY(y);
                        this.mapa.get(y).get(x).add(t);
                        b = true;
                        System.out.println(b);
                        break;
                    }
                }
            } finally {
                this.writlock.unlock();
            }
        }
        if (b) return cords;
        return null;
    }
    public String trotinetesToString(List<List<Integer>> l){
        StringBuilder s = new StringBuilder();
        for(List<Integer>subl :l){
                String aux= "Trotinete em X: " + subl.get(0) + "Y: " + subl.get(1);
            s.append(aux).append(" | ");
        }
        return s.toString();
    }
}
