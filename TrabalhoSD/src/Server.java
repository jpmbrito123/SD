import java.util.ArrayList;
import java.util.List;

public class Server {
    List<List<String>> mapa= new ArrayList<>();

    public void criaMapa(){
        for(int i = 0; i<20; i++){
            for(int j = 0; j<20; j++){
                mapa.get(i).add(null);
            }
        }
    }
}
