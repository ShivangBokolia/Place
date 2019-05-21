package place.server;

import place.PlaceBoard;
import place.PlaceTile;
import place.client2.PlaceModel;
import place.network.PlaceRequest;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Thread that handles client requests
 * @author Thomas James Tribunella IV
 * @author  Shivang Bokolia
 * */
class PlaceServerThread extends Thread{
    private PlaceBoard model;

    private Socket clientSocket;
    private String username;
    private ObjectOutputStream oos;
    private NetworkServer networkServer;
    public boolean hasUsername;
    /**
     * Constructs PlaceServerThread
     * @param clientSocket : Client connections
     * @param model : Model of PlaceBoard
     * @param networkServer : hands client requests
     * */
    public PlaceServerThread(Socket clientSocket, NetworkServer networkServer, PlaceBoard model){
        this.clientSocket = clientSocket;
        this.networkServer = networkServer;
        this.model = model;
        hasUsername = false;
    }
    /**
     * run method
     * */
    @Override
    public void run() {
        //model = new PlaceBoard(PlaceServer.boardSize);

            try(ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())) {
                oos = new ObjectOutputStream( clientSocket.getOutputStream());
                while (clientSocket.isConnected()){
                    PlaceRequest<?> req = (PlaceRequest<?>) ois.readUnshared();
                    handleRequest(req);
                }

            } catch (EOFException  |SocketException e) {

            }catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    clientSocket.close();
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//        try {
//            clientSocket.close();
//            oos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        networkServer.logoff(username);
    }
    /**
     * @return username: username of client
     * */
    public String getUsername() {
        return username;
    }
    /**
     * Handles login request
     * */
    public void login(){
        try {
            oos.writeUnshared(new PlaceRequest<String>(PlaceRequest.RequestType.LOGIN_SUCCESS,
                    username+" connected successfully"));
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * Sends Error
     * */
    public void sendError(){
        try {
            oos.writeUnshared(new PlaceRequest<String>(PlaceRequest.RequestType.ERROR,
                    username + " is already used"));
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * Sends Board
     * */
    public void sendBoard() {
        try {
            oos.writeUnshared(new PlaceRequest<PlaceBoard>(PlaceRequest.RequestType.BOARD,model));
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * Handles change tile request
     * */
    public void changeTile(PlaceTile tile){
        model.setTile(tile);
        try {
            oos.writeUnshared(new PlaceRequest<PlaceTile>(PlaceRequest.RequestType.TILE_CHANGED,tile));
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calls methods to hands requests based on request message
     * @param req : Place request from client*/
    private void handleRequest(PlaceRequest req){
        switch (req.getType()){
            case LOGIN:
                username = req.getData().toString();
                System.out.println(username+" connected: "+ clientSocket.getLocalAddress());
                hasUsername = true;
                networkServer.login(this);
                break;
            case CHANGE_TILE:
                PlaceTile tile = (PlaceTile) req.getData();
                tile.setOwner(username);
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                networkServer.tileChange(tile);
                break;
        }
    }
}
