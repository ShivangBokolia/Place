package place.client2;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceException;
import place.PlaceTile;
import place.client.ptui.PlacePTUI;
import place.network.PlaceRequest;
import place.network.PlaceRequest.RequestType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The client side network interface to a place server.
 * Each player in the game gets its own connection to the server.
 * This class represents the controller part of a model-view-controller
 * triumvirate, in that part of its purpose is to forward user actions
 *  to the remote server.
 *
 * @author Shivang Bokolia
 * @author Thomas James Tribunella IV
 */
public class NetworkClient {

    private Socket sock;

    private ObjectInputStream networkIn;

    private ObjectOutputStream networkOut;

    private PlaceBoard board;

    private String username;

    private boolean type;

    private boolean go;

    private volatile boolean ready;

    private PlaceModel model;

    /**
     * Hook up with a Place game server already running and waiting for
     * two players to connect.
     * Afterwards a thread that listens for server messages and forwards
     * them to the game object is started.
      */

    public NetworkClient(String hostname, int port, String username, PlaceModel model, Boolean type) {
        try {
            this.sock = new Socket(hostname, port);
            this.networkOut = new ObjectOutputStream(sock.getOutputStream());
            this.networkIn = new ObjectInputStream(sock.getInputStream());
            this.go = true;
            this.username = username;
            this.model = model;
            this.type = type;

            this.loginMessage(username);
            this.ready=false;
            Thread netThread = new Thread( () -> this.run() );
            netThread.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hook up with a Place game server already running and waiting for
     * two players to connect.
     * Afterwards a thread that listens for server messages and forwards
     * them to the game object is started.
     */
    public NetworkClient(String hostname, int port, String username, PlaceModel model) {
        try {
            this.sock = new Socket(hostname, port);
            this.networkOut = new ObjectOutputStream(sock.getOutputStream());
            this.networkIn = new ObjectInputStream(sock.getInputStream());
            this.go = true;
            this.model = model;

            this.loginMessage(username);
            this.ready=false;
            Thread netThread = new Thread( () -> this.run() );
            netThread.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Accessor that takes multithreaded access into account
     *
     * @return whether it ok to continue or not
     */
    private synchronized boolean goodToGo(){
        return this.go;
    }

    /**
     * Multithread-safe mutator
     */
    private synchronized void stop(){
        this.go = false;
    }

    /**
     * This boolean method checks if the board is received by the client or not.
     * @return
     */
    public boolean isReady(){
       return ready;
    }

    /**
     * The method sends the LOGIN message to the server
     * @param username
     */
    private void loginMessage(String username) {
        try {
            this.networkOut.writeUnshared(new PlaceRequest<String>(RequestType.LOGIN, username));
            this.networkOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The method gets the LOGINSUCCESSFUL mesage from the server and
     * prints the username.
     * @param req
     */
    private void printLogInSuccess(PlaceRequest req) {
        System.out.println(req.getData() + " was connected.");
    }

    /**
     * The method gets the board from the server and allocates it to the model.
     * @param board
     * @throws PlaceException
     */
    public void getBoard(PlaceBoard board) throws PlaceException {
        model.allocate(board);
        System.out.println(board);
    }

    /**
     * This method sends the tile to the server that has to be changed.
     * @param row
     * @param col
     * @param username
     * @param color
     */
    public void sendTileChange(int row, int col, String username, PlaceColor color){
        try {
            this.networkOut.writeUnshared(new PlaceRequest<PlaceTile>(RequestType.CHANGE_TILE,new PlaceTile(row,col,username, color)));
            this.networkOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The method changes the tile in the model.
     * @param req
     */
    public void tileChanged(PlaceRequest req){
//        System.out.println(req.getData());
        if (this.type == false){
            model.setTilePTUI((PlaceTile) req.getData());
        }
        else {
            model.setTile((PlaceTile) req.getData());
            ((PlaceTile) req.getData()).setOwner(username);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(model);
//        System.out.println("Enter row, col and color: ");
    }

    /**
     * Gives the errr recieved from the server.
     * @param err
     */
    private void error(String err){
        System.out.println("Error: " + err);
        this.model.error(err);
        this.stop();
    }

    /**
     * this method closes the network client and the client side.
     */
    public void close() {
        try{
            this.sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        } this.model.close();
        this.stop();
    }

    /**
     * Run the main client loop. Intended to be started as a separate
     * thread internally. This method is made private so that no one
     * outside will call it or try to start a thread on it.
     */
    private void run() {
        while (goodToGo()) {
            try {
                PlaceRequest<?> req = (PlaceRequest<?>) networkIn.readUnshared();
                switch (req.getType()) {
                    case LOGIN_SUCCESS:
                        printLogInSuccess(req);
                        break;
                    case BOARD:
                        getBoard((PlaceBoard)req.getData());
                        ready = true;
                        System.out.println("Enter row and col: ");
                        break;
                    case TILE_CHANGED:
                        tileChanged(req);
                        Thread.sleep(500);
                        System.out.println("Enter row and col: ");
                        break;
                    case ERROR:
                        error((String) req.getData());
                        break;

                }
            } catch (IOException e) {
                this.error("IO exception occured");
                this.stop();
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                this.error("Class not found exception");
                this.stop();
                e.printStackTrace();
            } catch (PlaceException e) {
                e.printStackTrace();
                this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        this.close();
    }
}