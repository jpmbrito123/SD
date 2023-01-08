package UI;

import Clientes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Text2UI {


    private IClientesFacade modelUtilizador;
    // Menus da aplicação
    private Menu menu;

    // Scanner para leitura
    private Scanner scin;

    /**
     * Construtor.
     * <p>
     * Cria os menus e a camada de negócio.
     */
    public Text2UI() {
        // Criar o menu
        this.menu = new Menu(new String[]{
                "Ver Trotinetes Livres",
                "Lista de Recompensas",
                "Reservar Trotinetes",
                "Estacionar",
                "Pedir Notificações",
                "Desativar Notificações"

        });
        this.menu.setHandler(1, this::trataTrotinetesLivres);
        this.menu.setHandler(2, this::trataListaRecompenas);
        this.menu.setHandler(3, this::trataReservarTrotinete);
        this.menu.setHandler(4, this::trataEstacionar);
        this.menu.setHandler(5, this::trataPedirNotificacoes);
        this.menu.setHandler(6, this::trataDesativarNotificacoes);
        this.modelUtilizador = new ClienteFacade();
        scin = new Scanner(System.in);
    }

    /**
     * Executa o menu principal e invoca o método correspondente à opção seleccionada.
     */
    public void run() {
        this.menu.run();
        System.out.println("Bota que tem...");
    }


}