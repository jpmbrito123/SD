package Servidores;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.nextUp;

public class App {
    List<List<List<Trotinete>>> mapa= new ArrayList<>();

    List<Trotinete> trotinetes = new ArrayList<>();

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
        trotinetes.add(t);
        mapa.get(y).get(x).add(t);
    }
    public List<List<Integer>> trotinetes_livres(int x,int y){
        List<List<Integer>> livres= new ArrayList<>();
        for(int l=y-this.distancia;l<y+this.distancia && l<this.tamanho;l++){
            if (l>=0){
                for(int c=x-this.distancia;c<x+this.distancia && c<this.tamanho;c++){
                    if(c>=0 && this.distancia>=abs(l-y)+abs(c-x) ){
                        List<Trotinete> trotinetes = this.mapa.get(l).get(c);
                        for (Trotinete trotinete: trotinetes) {
                            if (trotinete.isLivre()) {
                                List<Integer> cords = new ArrayList<>(trotinete.getCorX());
                                cords.add(trotinete.getCorY());
                                livres.add(cords);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return livres;
    }

    public void recompensas(List<List<Integer>> A,List<List<Integer>> B){
        List<Trotinete> trotinetes;
        for(int y=0;y<this.tamanho;y++){
            for(int x=0;x<this.tamanho;x++){
                trotinetes = this.mapa.get(y).get(x);
                if (trotinetes.size()>1){
                    List<Integer> cords = new ArrayList<>(x);
                    cords.add(y);
                    A.add(cords);
                } else if (trotinetes.size()==0) {
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
    }

    public void reserva_trotinete(int x ,int y){
        Trotinete t = null;
        if (x >= 0 && x < this.tamanho && y >= 0 && y < this.tamanho) {
            for (int n = 0;n<this.distancia;n++) {
                for(int l=y-n;l<y+n && l<n;l++) {
                    if (l >= 0) {
                        for (int c = x - n; c < x + n && c < n; c++) {
                            if (c >= 0 && n >= abs(l - y) + abs(c - x)) {
                                List<Trotinete> trotinetes = this.mapa.get(l).get(c);
                                for (Trotinete trotinete: trotinetes) {
                                    t = trotinete;
                                    t.reserva();
                                    this.mapa.get(y).add(x, null);
                                    break;
                                    }
                                }
                            if (t!=null) break;
                            }
                        }
                    if (t!=null) break;
                }
                if (t!=null) break;
            }
        }
    }
}
