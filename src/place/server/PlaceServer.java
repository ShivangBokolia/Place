package place.server;

import place.PlaceBoard;
import place.PlaceTile;
import place.network.PlaceRequest;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;



/**
 *Waits for incoming client connections and creates place server threads
 * @author Thomas James Tribunella IV
 * @author  Shivang Bokolia
 **/
public class PlaceServer extends Thread{
    static int boardSize;
    static int portNumber;
    NetworkServer networkServer;

    /**
     * Constructs PlaceServer
     * @param args : array of arguments from main
     * */
    public PlaceServer(String[] args){
        networkServer = new NetworkServer(Integer.parseInt(args[1]));

    }
    /**
     * Main method for place server
     * @param args : array of arguments <port number>, <Board Size>
     **/
    public static void main(String[] args) {
        if (args.length != 2){
            System.err.println("Usage: java PlaceServer <port number>, <Board Size>");
            System.exit(1);
        }
        boardSize = Integer.parseInt(args[1]);
        portNumber = Integer.parseInt(args[0]);
        PlaceServer placeServer = new PlaceServer(args);
        placeServer.start();


    }
    /**Waits for incoming client connections and creates place server threads*/
    @Override
    public void run() {
        boolean listening = true;

        try(ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening)
                 new PlaceServerThread(serverSocket.accept(),networkServer,networkServer.getPlaceBoard()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

