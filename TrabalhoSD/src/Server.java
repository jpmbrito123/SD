import java.util.ArrayList;
import java.util.List;

public class Server {
    List<List<Trotinete>> mapa= new ArrayList<>();

    List<Trotinete> trotinetes = new ArrayList<>();

    final Integer distancia = 2;
    final Integer tamanho = 20;

    public void criaMapa(){
        for(int i = 0; i<this.tamanho; i++){
            mapa.add(new ArrayList<>());
            for(int j = 0; j<this.tamanho; j++){
                mapa.get(i).add(null);
            }
        }
    }

    public void adiciona_trotinete(Trotinete t){
        trotinetes.add(t);
        int x = t.getCorX();
        int y = t.getCorY();
        mapa.get(y).add(x, t);
    }
    public List<List<Integer>> trotinetes_livres(int x,int y){
        List<List<Integer>> livres= new ArrayList<>();
        for(int l=y-this.distancia;l<y+this.distancia && l<this.tamanho;l++){
            if (l>=0){
                for(int c=x-this.distancia;c<x+this.distancia && c<this.tamanho;c++){
                    if(c>=0){
                        Trotinete trotinete = this.mapa.get(l).get(c);
                        if (trotinete.isLivre()){
                            List<Integer> cords = new ArrayList<>(trotinete.getCorX());
                            cords.add(trotinete.getCorY());
                            livres.add(cords);
                        }
                    }
                }
            }
        }
        return livres;
    }

    public List<List<Integer>> recompensas(){}

    public void reserva_trotinete(int x ,int y){
        if (x>=0 && x<this.tamanho && y>=0 && y<this.tamanho){
            Trotinete t = this.mapa.get(y).get(x);
                    if (t != null){
                        t.reserva();
                    }
        }
    }
}
