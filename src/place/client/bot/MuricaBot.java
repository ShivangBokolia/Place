package place.client.bot;

import place.PlaceColor;
import place.client2.NetworkClient;
import place.client2.PlaceModel;

/**
 * Bots that creates an american flag
 * @author Thomas James Tribunella IV
 * @author  Shivang Bokolia
 * */
public class MuricaBot{
    private static NetworkClient networkClient;
    private static PlaceModel model;
    private static String username;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java PlaceGUI host port username");
            System.exit(-1);
        } else {
            model = new PlaceModel();
            username = args[2];
            networkClient = new NetworkClient(args[0],Integer.parseInt(args[1]),username,model);
            changeTile1();
            model.initializeGame();
        }
    }
    /**
     * Creates the flag and exits
     * */
    public static void changeTile1() {
        while (!networkClient.isReady()){}

            boolean murica = false;
            int dim = model.getDIM();
            for(int x =0; x<dim/2; x++){
                if((dim/2)%2==0){
                    murica= !murica;
                }
                for(int y =0; y<dim/2; y++){
                    if (murica) {
                        networkClient.sendTileChange(x, y, username, PlaceColor.WHITE);
                    }
                    else {
                        networkClient.sendTileChange(x, y, username, PlaceColor.BLUE);
                    }
                    murica=!murica;
                }
            }
            murica = false;
            for(int x =0; x<dim/2; x++){
                murica=!murica;
                for(int y =dim/2; y<dim; y++){
                    if (murica) {
                        networkClient.sendTileChange(x, y, username, PlaceColor.WHITE);
                    }
                    else {
                        networkClient.sendTileChange(x, y, username, PlaceColor.RED);
                    }
                }
            }
            for(int x =dim/2; x<dim; x++){
                murica=!murica;
                for(int y =0; y<dim; y++){
                    if (murica) {
                        networkClient.sendTileChange(x, y, username, PlaceColor.WHITE);
                    }
                    else {
                        networkClient.sendTileChange(x, y, username, PlaceColor.RED);
                    }
                }
            }
    }
}
