package place.client.bot;

import place.PlaceColor;
import place.client2.NetworkClient;
import place.client2.PlaceModel;

import java.util.Random;

/**
 * Bots that changes random tiles
 * @author Thomas James Tribunella IV
 * @author  Shivang Bokolia
 * */
public class RandomBot{
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
     * Changes random tiles
     * */
    public static void changeTile1() {
        while (!networkClient.isReady()){}
        int randomRow;
        int randomCol;
        int randomColor;

        while(networkClient.isReady()) {
            int dim = model.getDIM();
            randomCol = (int) (Math.random()* dim);
            randomRow = (int) (Math.random()* dim);
            randomColor = (int) (Math.random()*16);
            networkClient.sendTileChange(randomRow, randomCol, username, PlaceColor.values()[randomColor]);
        }
    }
}
