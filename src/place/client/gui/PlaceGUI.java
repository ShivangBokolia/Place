package place.client.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.event.EventHandler;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import place.PlaceColor;
import place.PlaceTile;
import place.client2.NetworkClient;
import place.client2.PlaceModel;
import javafx.scene.control.Button;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import static javafx.scene.paint.Color.*;

/**
 * This application is the GUI for Place
 *
 * @author Shivang Bokolia
 * @author Thomas James Tribunella IV
 */
public class PlaceGUI extends Application implements Observer {

    /**
     * Connection to the network interface to server
     */
    private NetworkClient serverConn;

    //GUI elements
    private PlaceModel model;
    private BorderPane pane = new BorderPane();
    private GridPane gridPane = new GridPane();
    private BorderPane bottomBorder = new BorderPane();
    private GridPane bottomPane = new GridPane();
    private String username;
    private int dim;
    private Button[][] buttons;
    private PlaceColor placeColor;
    private Scene scene;

    //Zooming and Panning elements
    private static final double MAX_SCALE = 10.0d;
    private static final double MIN_SCALE = .1d;
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double translateAnchorX;
    private double translateAnchorY;

    /**
     * Create the board model, create the network connection based on
     * command line parameters.
     */
    public void init(){
        List<String> args = getParameters().getRaw();
        placeColor = PlaceColor.BLACK;
        model = new PlaceModel();
        // get host info and username from command line
        model.addObserver(this);
        String host = args.get(0);
        int port = Integer.parseInt(args.get(1));
        username = args.get(2);
        serverConn = new NetworkClient(host, port, username, this.model, true);
        model.initializeGame();
    }

    /**
     * The start function is where the complete GUI is written. All the design and
     * everything else is made here
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        while (!serverConn.isReady()){}
        dim = model.getDIM();
        buttons = new Button[dim][dim];

//        pane.setScaleShape(true);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(400);
        primaryStage.setResizable(true);
        primaryStage.sizeToScene();
        primaryStage.setTitle("Place GUI");

        //forming the grid for the GUI
        gridMake(dim);
        //setting colors after clicking
        makeColor(dim);
        //setting the positions of the panes
        pane.setCenter(gridPane);
        bottomBorder.setCenter(bottomPane);
        pane.setBottom(bottomBorder);

        //setting the scene for the GUI
        scene = new Scene(pane);
        //setting the cursor
        setCursor();

        //setting the zoom through scrolling
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, getOnMousePressedEventHandler());
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, getOnMouseDraggedEventHandler());
        scene.addEventFilter(ScrollEvent.SCROLL, getOnScrollEventHandler());

        //adding scene to the primary stage
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(we -> {
            System.out.println("Stage is closing");
            System.exit(-1);
        });

        //setting up the properties of height and width
        settingHeightProperty();
        settingWidthProperty();
        refresh();
    }

    /**
     * The method is used for resizing the board
     */
    private void settingWidthProperty() {
        pane.widthProperty().addListener((observable, oldValue, newValue) -> {
            double width = (double) newValue;
            for (int x=0; x<dim ; x++) {
                for (int y = 0; y < dim; y++) {
                    buttons[x][y].setPrefWidth(width);
                    buttons[x][y].setMaxWidth(pane.getWidth()/dim);
                }
            }
        });
    }

    /**
     * The method is used for resizing the board
     */
    private void settingHeightProperty() {
        pane.heightProperty().addListener((observable, oldValue, newValue) -> {
            double height = (double) newValue;
            for (int x=0; x<dim ; x++) {
                for (int y = 0; y < dim; y++) {
                    buttons[x][y].setPrefHeight(height);
                    buttons[x][y].setMaxHeight(pane.getHeight()/dim);
                }
            }
        });
    }

    /**
     * This method is used for setting the cursor to Bob Ross.
     */
    private void setCursor() {
        scene.setCursor(new ImageCursor(new Image(getClass().getResourceAsStream("bob.png"))));
    }

    /**
     * These methods are used for getting the Events that occur during the
     * @return
     */
    public EventHandler<ScrollEvent> getOnScrollEventHandler(){
        return onScrollEventHandler;
    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler(){
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler(){
        return onMouseDraggedEventHandler;
    }

    /**
     * This methid gets the colors for the bottompane.
     * @param dim
     */
    private void makeColor(int dim){
        int y =0;
        int newDim = dim+1;
        for (PlaceColor colors:PlaceColor.values()) {
            Button button = new Button();
            button.setText(Integer.toHexString(y).toUpperCase());
            button.setBackground(new Background(new BackgroundFill(Color.rgb(colors.getRed()
                    ,colors.getGreen(),colors.getBlue()),CornerRadii.EMPTY,Insets.EMPTY)));
            button.setOnMouseClicked(event -> placeColor = colors);
            bottomPane.add(button,y,1);
            y++;
        }

    }

    /**
     * This method makes the grid and sets all buttons in it.
     * @param dim
     */
    private void gridMake(int dim) {
        for (int x=0; x<dim ; x++){
            for (int y=0; y<dim ; y++){
                buttons[x][y] = new Button();
                buttons[x][y].setBackground(new Background(new BackgroundFill(WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
//                buttons[x][y].setMinHeight(10);
//                buttons[x][y].setMinWidth(10);
//                buttons[x][y].setPrefWidth(1);
//                buttons[x][y].setPrefHeight(1);
                buttons[x][y].setMaxWidth(pane.getWidth()/dim);
                buttons[x][y].setMaxHeight(pane.getHeight()/dim);

                Tooltip tilehover = hoverTile(model.getTile(x,y));
                Tooltip.install(buttons[x][y], tilehover);

                gridPane.add(buttons[x][y],y,x);
                final int row = x;
                final int col = y;
                buttons[x][y].setOnMouseClicked(event ->{ //model.initializeGame();
                serverConn.sendTileChange(row, col,username,placeColor);
                });
            }
        }
    }

    /**
     * This method creates the hover over the tiles.
     * @param pTile
     * @return
     */
    private Tooltip hoverTile(PlaceTile pTile){
        String coordinate = Integer.toString(pTile.getRow()) + Integer.toString(pTile.getCol());
//        System.out.println(pTile.getOwner() + "fshdjfcklsd");
        String owner = pTile.getOwner();
        Date tileDate = new Date(pTile.getTime());
        Format dateFormat = new SimpleDateFormat("MM/dd/yyyy \n (HH:mm:ss)");
        String time = ((SimpleDateFormat) dateFormat).format(tileDate);
        Tooltip tileInfo = new Tooltip(coordinate + "\n" + owner + "\n" + time);
        return tileInfo;

    }

    /**
     * Zooming on scrolling up and down.
     * Scroll up to zoom out.
     * Scroll down to zoom in.
     */
    private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {
        @Override
        public void handle(ScrollEvent event) {
            double delta = 1.2;
            gridPane.setScaleY(gridPane.getScaleX());
            double scale = gridPane.getScaleX();
            double oldScale = scale;

            if (event.getDeltaY() < 0){
                scale /= delta;
            }else{
                scale *= delta;
            }

            scale = clamp(scale, MIN_SCALE, MAX_SCALE);

            double f = (scale / oldScale) - 1;

            double dx = (event.getSceneX() - (gridPane.getBoundsInParent().getWidth()/2 + gridPane.getBoundsInParent().getMinX()));
            double dy = (event.getSceneY() - (gridPane.getBoundsInParent().getHeight()/2 + gridPane.getBoundsInParent().getMinY()));

            gridPane.setScaleX(scale);

            gridPane.setTranslateX(gridPane.getTranslateX() - (f*dx));
            gridPane.setTranslateY(gridPane.getTranslateY() - (f*dy));

            event.consume();
        }
    };

    public static double clamp(double value, double min, double max){
        if (Double.compare(value,min) < 0)
            return min;
        if (Double.compare(value,max) > 0)
            return max;
        return value;
    }

    /**
     * Selecting the board and using Drag to move it around.
     */
    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (!event.isPrimaryButtonDown()){
                return;
            }
            mouseAnchorX = event.getSceneX();
            mouseAnchorY = event.getSceneY();

            translateAnchorX = gridPane.getTranslateX();
            translateAnchorY = gridPane.getTranslateY();
        }
    };

    /**
     * Selecting the board and Dragging it around when zoomed in.
     */
    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (!event.isPrimaryButtonDown())
                return;

            gridPane.setTranslateX(translateAnchorX + event.getSceneX() - mouseAnchorX);
            gridPane.setTranslateY(translateAnchorY + event.getSceneY() - mouseAnchorY);
        }
    };

    /**
     * The method calls refresh.
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        javafx.application.Platform.runLater(this::refresh);
    }

    /**
     * The buttons are set to the color selected.
     */
    private void refresh(){
        for (int x=0; x<dim ; x++) {
            for (int y = 0; y < dim; y++) {
                PlaceTile tile = model.getTile(x,y);
                Color color= Color.rgb(tile.getColor().getRed(),tile.getColor().getGreen(),tile.getColor().getBlue());
                buttons[x][y].setBackground(new Background(new BackgroundFill(color,CornerRadii.EMPTY,Insets.EMPTY)));
            }
        }
    }

    /**
     * Launch the JavaFX GUI.
     * @param args not used, here, but named arguments are passed to the GUI.
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java PlaceGUI host port username");
            System.exit(-1);
        } else {
            Application.launch(args);
        }
    }
}
