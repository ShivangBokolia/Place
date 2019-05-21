package place.server;

import place.PlaceBoard;
import place.PlaceTile;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * handles client requests in synchronized methods
 * @author Thomas James Tribunella IV
 * @author  Shivang Bokolia
 * */

public class NetworkServer {
    private Map<String,PlaceServerThread> serverMap;
    private PlaceBoard placeBoard;
    private  boolean wait;

    /**
     * Constructs networks server
     * @param boardSize : size of board
     * */
    public NetworkServer(int boardSize) {
        this.serverMap = new HashMap<>();
        placeBoard = new PlaceBoard(boardSize);
        wait = false;

    }
    /**
     * Handles login request in synchronized method
     * */
    public synchronized void login(PlaceServerThread placeServerThread){
        //PlaceServerThread placeServerThread = new PlaceServerThread(s, this, placeBoard);
        //placeServerThread.start();
        //addThread(placeServerThread);
        if (serverMap.containsKey(placeServerThread.getUsername()))
            placeServerThread.sendError();
        else {
            serverMap.put(placeServerThread.getUsername(), placeServerThread);
            placeServerThread.login();
            sendBoard();
        }

        // TODO: check username
        // TODO: add thread to map or send error
    }

    /**
     * Handles sends board request in synchronized method
     * */
    public synchronized void sendBoard(){
        for(String i : serverMap.keySet())
            serverMap.get(i).sendBoard();

    }
    /**
     * Handles tile change request in synchronized method
     * */
    public synchronized void tileChange(PlaceTile tile){
        for(String i : serverMap.keySet())
            serverMap.get(i).changeTile(tile);
    }
    /**
     * @return placeBoard: clients board
     * */
    public PlaceBoard getPlaceBoard() {
        return placeBoard;
    }
    /**
     * Removes username from hash map
     * */
    public synchronized void logoff(String username){
        serverMap.remove(username);
    }
}
