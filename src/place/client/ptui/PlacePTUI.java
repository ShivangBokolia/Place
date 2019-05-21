package place.client.ptui;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceTile;
import place.client2.NetworkClient;
import place.client2.PlaceModel;

import java.io.PrintWriter;
import java.util.*;

/**
 * The PlacePTUI application is the plain text UI for Place.
 *
 * @author Shivang Bokolia
 * @author Thomas James Tribunella IV
 */
public class PlacePTUI extends ConsoleApplication implements Observer {

//    private PlaceBoard board;

    private PlaceModel model;

    private NetworkClient serverConn;

    private Scanner userin;

    private PrintWriter userout;

    private String username;

    private boolean goodtogo;

    /**
     * Create the board model, create the network connection based on
     * command line parameters.
     */
    @Override
    public void init(){
        try{
            List<String> args = super.getArguments();

            //host info
            String host = args.get(0);
            int port = Integer.parseInt(args.get(1));
            String username = args.get(2);
            this.goodtogo = true;
            this.username = username;
            //Create uninitialized board
            this.model = new PlaceModel();
            //Create the network client
            this.serverConn = new NetworkClient(host,port,username,this.model,false);
            this.model.initializeGame();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This method continues running until the game is over.
     *      * It is not like {@link javafx.application.Application}.
     *      * That method returns as soon as the setup is done.
     * @param userIn
     * @param userOut
     */
    @Override
    public void go(Scanner userIn, PrintWriter userOut) {
        this.userin = userIn;
        this.userout = userOut;

        // Connect UI to model. Can't do it sooner because streams not set up.
        this.model.addObserver(this);
        // Manually force a display of all board state, since it's too late
        // to trigger update().
        this.refresh();
        while(goodtogo){
            try{
                this.wait(5000);
            }catch( InterruptedException ie) {}
        }
    }

    /**
     * GUI is closing so close the network connection. Server will get the message.
     */
    @Override
    public void stop(){
        this.userin.close();
        this.userout.close();
        this.serverConn.close();
    }

    /**
     * Update the UI when the model calls notify.
     * Currently no information is passed as to what changed,
     * so everything is redone.
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        assert o == this.model: "Update from non-model Observable";
        this.refresh();
    }

    /**
     * Update all GUI Nodes to match the state of the model.
     */
    private void refresh(){
        this.userout.print("Enter row, col and color: ");
//        this.userout.flush();
        while (goodtogo){
            int row = this.userin.nextInt();
            if (row == -1){
                goodtogo = false;
                this.stop();
                System.exit(0);
            }else {
                int col = this.userin.nextInt();
                int color = this.userin.nextInt();
                PlaceColor tilecolor = null;
                for (PlaceColor colors: PlaceColor.values()){
                    if (colors.getNumber() == color){
                        tilecolor = colors;
                    }
                }
                if (model.isValid(new PlaceTile(row, col, username, tilecolor))) {
                    serverConn.sendTileChange(row, col, username, tilecolor);
                } else {
                    System.out.println("Move is not valid. \nEnter new row, col and color: ");
                }
            }
        }
    }

    /**
     * Launch the JavaFX GUI.
     *
     * @param args not used, here, but named arguments are passed to the GUI.
     *             <code>--host=<i>hostname</i> --port=<i>portnum</i></code>
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java PlaceClient host port username");
            System.exit(0);
        }else{
            ConsoleApplication.launch(PlacePTUI.class, args);
        }
    }
}
