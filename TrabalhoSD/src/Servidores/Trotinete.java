package Servidores;

public class Trotinete {
    private int id;
    private int corX;
    private int corY;
    private boolean livre;

    public Trotinete(int id, int corX, int corY, boolean livre) {
        this.id = id;
        this.corX = corX;
        this.corY = corY;
        this.livre = livre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCorX() {
        return corX;
    }

    public void setCorX(int corX) {
        this.corX = corX;
    }

    public int getCorY() {
        return corY;
    }

    public void setCorY(int corY) {
        this.corY = corY;
    }

    public boolean isLivre() {
        return livre;
    }

    public void setLivre(boolean livre) {
        this.livre = livre;
    }

    public void reserva() {
    }
}
