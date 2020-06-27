
package Mobile_Agents.gui;

import Mobile_Agents.SensorNode;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;
/**
 * Class: GuiNodes
 * Description: Observes and displays a sensor nodes corresponding status.
 * @Contributer Tanner Randall Hunt
 */
public class GuiNodes extends StackPane implements Observer {
    private Canvas canvas;
    private GraphicsContext gc;
    private Label point;

    public GuiNodes(SensorNode sensorNode){
        VBox vBox = new VBox();

        Point location = sensorNode.getLocation();

        canvas = new Canvas(GuiSize.SIZE,GuiSize.SIZE);
        gc = canvas.getGraphicsContext2D();

        String x = Integer.toString(location.x);
        String y = Integer.toString(location.y);
        point =new Label(x+","+y);

        switch(sensorNode.getNodeStatus()){
            case ON_FIRE:{
                gc.setFill(Color.RED);
                break;
            }
            case CLOSE_TO_FIRE:{
                gc.setFill(Color.YELLOW);
                break;
            }
            case OK:{
                gc.setFill(Color.GREEN);
                break;
            }
        }

        if(sensorNode.isFireStation())gc.setFill(Color.BLUEVIOLET);
        gc.fillOval(GuiSize.SENSOR_OFFSET,GuiSize.SENSOR_OFFSET,
                GuiSize. SENSOR_SIZE,GuiSize.SENSOR_SIZE);

        vBox.getChildren().add(point);
        vBox.setAlignment(Pos.CENTER);
        this.getChildren().addAll(canvas,vBox);
    }

    // Updates gui on sensor status changes.
    @Override
    public void update(Observable o, Object arg) {

        SensorNode sensorNode =(SensorNode)o;
        SensorNode.Status status = (SensorNode.Status)arg;

      Platform.runLater(()->{
        this.getChildren().clear();
        canvas = new Canvas(GuiSize.SIZE,GuiSize.SIZE);
        gc = canvas.getGraphicsContext2D();

        if(sensorNode.isFireStation()){
            gc.setFill(Color.BLUEVIOLET);
            gc.fillRect(0,0,GuiSize.SIZE,GuiSize.SIZE);
        }

        this.getChildren().addAll(canvas,point);
        if(status != null) {
            switch (status) {
                case ON_FIRE: {
                    gc.setFill(Color.RED);
                    gc.fillOval(GuiSize.SENSOR_OFFSET,GuiSize.SENSOR_OFFSET,
                            GuiSize.SENSOR_SIZE,GuiSize.SENSOR_SIZE);
                    break;
                }
                case CLOSE_TO_FIRE: {
                    gc.setFill(Color.YELLOW);
                    gc.fillOval(GuiSize.SENSOR_OFFSET,GuiSize.SENSOR_OFFSET,
                            GuiSize.SENSOR_SIZE,GuiSize.SENSOR_SIZE);
                    break;
                }
                case FORTIFIED: {
                    gc.setFill(Color.DARKGREEN);
                    gc.fillRect(GuiSize.AGENT_OFFSET,GuiSize.AGENT_OFFSET,
                            GuiSize.AGENT_SIZE,GuiSize.AGENT_SIZE);
                    gc.setFill(Color.YELLOW);
                    gc.fillOval(GuiSize.SENSOR_OFFSET,GuiSize.SENSOR_OFFSET,
                            GuiSize.SENSOR_SIZE,GuiSize.SENSOR_SIZE);
                    break;
                }
                case OCCUPIED: {
                    gc.setFill(Color.DARKGREEN);
                    gc.fillRect(GuiSize.AGENT_OFFSET,GuiSize.AGENT_OFFSET,
                            GuiSize.AGENT_SIZE,GuiSize.AGENT_SIZE);
                    gc.setFill(Color.GREEN);
                    gc.fillOval(GuiSize.SENSOR_OFFSET,GuiSize.SENSOR_OFFSET,
                            GuiSize.SENSOR_SIZE,GuiSize.SENSOR_SIZE);
                    break;
                }
                case CASUALTY: {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(GuiSize.AGENT_OFFSET,GuiSize.AGENT_OFFSET,
                            GuiSize.AGENT_SIZE,GuiSize.AGENT_SIZE);
                    gc.setFill(Color.RED);
                    gc.fillOval(GuiSize.SENSOR_OFFSET,GuiSize.SENSOR_OFFSET,
                            GuiSize.SENSOR_SIZE,GuiSize.SENSOR_SIZE);
                    break;
                }
                case OK: {
                    gc.setFill(Color.GREEN);
                    gc.fillOval(GuiSize.SENSOR_OFFSET,GuiSize.SENSOR_OFFSET,
                            GuiSize.SENSOR_SIZE,GuiSize.SENSOR_SIZE);
                    break;
                }
            }
        }

       });
    }
}
