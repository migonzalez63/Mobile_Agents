package Mobile_Agents.gui;

import Mobile_Agents.SensorNode;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import java.util.Observable;
import java.util.Observer;
/**
 * Class: GuiLog
 * Description: Displays the accumulation log in the fire station.
 * @Contributer: Tanner Randall Hunt
 */
public class GuiLog extends ScrollPane implements Observer {
    private FlowPane flowPane = new FlowPane();
    public GuiLog(){
        flowPane.setPrefWidth(300);
        flowPane.setPrefHeight(500);
        this.setContent(flowPane);

    }
    @Override
    public void update(Observable o, Object arg) {
        SensorNode sensorNode = (SensorNode)o;
        String log = sensorNode.getLog();
        Platform.runLater(()-> {
            if(!flowPane.getChildren().isEmpty())flowPane.getChildren().clear();
            Text text = new Text(log);
            flowPane.getChildren().add(text);
            this.layout();
            this.setVvalue( 1.0d );
        });
    }
}
