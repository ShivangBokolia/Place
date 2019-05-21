package place.client.bot;

import place.PlaceColor;
import place.client2.NetworkClient;
import place.client2.PlaceModel;

/**
 * Bots that creates a tiger
 * @author Thomas James Tribunella IV
 * @author  Shivang Bokolia
 * */
public class TigerBot {
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
            createTiger();
            model.initializeGame();
        }

    }
    /**
     *Creates the tiger
     * */
    private static void createTiger() {
        if(model.getDIM()<= 11) {
            for (int x = 0; x < 11; x++) {
                for (int y = 0; y < 11; y++) {
                    networkClient.sendTileChange(x, y, username, PlaceColor.YELLOW);
                }
            }
            for (int y = 3; y < 8; y++) {
                networkClient.sendTileChange(0, y, username, PlaceColor.WHITE);
            }
            Boolean color = false;
            for (int y = 0; y < 11; y++) {
                if (color) {
                    networkClient.sendTileChange(1, y, username, PlaceColor.BLACK);
                }
                color = !color;
            }
            networkClient.sendTileChange(3, 3, username, PlaceColor.BLACK);
            networkClient.sendTileChange(3, 7, username, PlaceColor.BLACK);
            networkClient.sendTileChange(10, 0, username, PlaceColor.WHITE);
            networkClient.sendTileChange(10, 10, username, PlaceColor.WHITE);
            networkClient.sendTileChange(10, 5, username, PlaceColor.BLACK);
            networkClient.sendTileChange(9, 5, username, PlaceColor.BLACK);
            networkClient.sendTileChange(9, 6, username, PlaceColor.BLACK);
            networkClient.sendTileChange(9, 4, username, PlaceColor.BLACK);
        }else {
            System.out.println("Board is too small dim must >= 11");
        }

    }
}
