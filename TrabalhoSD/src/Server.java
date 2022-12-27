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

        for (Trotinete t : this.trotinetes) {
            int x = t.getCorX();
            int y = t.getCorY();
            mapa.get(y).add(x, t);
        }
    }

    public List<List<Integer>> trotinetes_livres(int x,int y){}

    public List<List<Integer>> recompensas(){}

    public void reserva_trotinete(int x ,int y){}
}
