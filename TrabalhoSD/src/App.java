import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.nextUp;

public class App {
    List<List<List<Trotinete>>> mapa= new ArrayList<>();

    List<Trotinete> trotinetes = new ArrayList<>();

    final Integer distancia = 2;
    final Integer tamanho = 20;

    public App(List<Trotinete> trotinetes) {
        for(int i = 0; i<this.tamanho; i++){
            this.mapa.add(new ArrayList<>());
            for(int j = 0; j<this.tamanho; j++){
                this.mapa.get(i).add(new ArrayList<Trotinete>());
            }
        }

        this.trotinetes = trotinetes;
        for (Trotinete t:this.trotinetes){
            this.adiciona_trotinete(t);
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
                            }
                        }
                    }
                }
            }
        }
        return livres;
    }

    public List<List<Integer>> recompensas(){return null;}

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
