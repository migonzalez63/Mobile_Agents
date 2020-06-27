package Mobile_Agents;

import Mobile_Agents.gui.GuiGraph;
import Mobile_Agents.gui.GuiLog;
import Mobile_Agents.reader.ConfigReader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.util.HashMap;



public class Main extends Application {
    private Stage stage;
    public static void main(String[] args){
       launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        ConfigReader configReader;
        File file = promptSelection();
        if(file != null) {
            configReader = new ConfigReader(file.getPath());

            BorderPane borderPane = new BorderPane();
            HashMap<Point, SensorNode> sensorMap = configReader.getSensorMap();
            GuiGraph guiGraph = new GuiGraph(sensorMap);
            GuiLog guiLog = new GuiLog();
            configReader.getFireStation().addObserver(guiLog);
            borderPane.setCenter(guiGraph);
            borderPane.setLeft(guiLog);
            Scene scene = new Scene(borderPane);


            configReader.ignite();
            for (SensorNode sensorNode : sensorMap.values()) {
                Thread thread = new Thread(sensorNode);
                thread.start();
            }
            configReader.dispatch();

            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(event -> {
                System.exit(0);
            });
            primaryStage.show();
        }

    }

    private File promptSelection(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showOpenDialog(stage);
        if(file != null && file.exists()){
            return file;
        }else return null;
    }
}
