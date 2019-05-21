package place.client2;

import place.PlaceBoard;
import place.PlaceException;
import place.PlaceTile;
import java.util.Observable;

/**
 * A copy of the board state kept by client code at the site of the player.
 * It acts as a model in an MVC pattern because the local UI observes changes
 * in it.
 */
public class PlaceModel extends Observable {

    private int DIM;

    private PlaceTile[][] tile;

    private String error;

    public final static int MIN_DIM = 1;

    /**
     * Gets the board and initiaizes the model for the client side.
     * @param model
     * @throws PlaceException
     */
    public void allocate(PlaceBoard model) throws PlaceException{
        if (model.DIM < PlaceModel.MIN_DIM){
            throw new PlaceException("Board too small to play");
        }

        this.DIM = model.DIM;
        this.tile = model.getBoard();
    }

    public void initializeGame(){
        super.setChanged();
        super.notifyObservers();
    }

    /**
     * Get the square dimension of the board
     *
     * @return number of rows
     */
    public int getDIM(){
        return this.DIM;
    }

    public PlaceTile[][] getBoard() {
        return this.tile;
    }

    /**
     * Get the tile for row and col
     * @param row
     * @param col
     * @return
     */
    public PlaceTile getTile(int row, int col) {
        return this.tile[row][col];
    }

    /**
     * Set the tile at the given row and col in GUI
     * @param tile
     */
    public void setTile(PlaceTile tile) {
        this.tile[tile.getRow()][tile.getCol()] = tile;
        setChanged();
        notifyObservers();
    }

    /**
     * Set the tile at the given row and col in PTUI
     * @param tile
     */
    public void setTilePTUI(PlaceTile tile){
        this.tile[tile.getRow()][tile.getCol()] = tile;
    }

    /**
     * It will check whether the tile given is valid or not.
     * @param tile
     * @return
     */
    public boolean isValid(PlaceTile tile) {
        return tile.getRow() >=0 &&
                tile.getRow() < this.DIM &&
                tile.getCol() >= 0 &&
                tile.getCol() < this.DIM;
    }

    /**
     * Called when an error is sent from the server.
     *
     * @param error The error message sent from the Place.server.
     */
    public void error(String error){
        this.error = error;
        super.setChanged();
        super.notifyObservers();
    }

    /**
     * Tell user s/he may close at any time.
     */
    public void close(){
        super.setChanged();
        super.notifyObservers();
    }

    /**
     * Returns a string representation of the board, suitable for printing out.
     * @return
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("");
        for (int row=0; row<DIM; ++row) {
            builder.append("\n");
            for (int col=0; col<DIM; ++col) {
                builder.append(this.tile[row][col].getColor());
            }
        }
        return builder.toString();
    }
}
