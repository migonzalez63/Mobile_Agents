package Mobile_Agents.gui;

import Mobile_Agents.SensorNode;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import java.awt.*;
import java.util.HashMap;

/**
 * Class: GuiGraph
 * Description: GridPane that holds senor nodes
 * @Contributer Tanner Randall Hunt
 */
public class GuiGraph extends ScrollPane {

    private StackPane stackPane = new StackPane();
    private Canvas canvas = new Canvas();
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    private GridPane gridPane = new GridPane();


    public GuiGraph(HashMap<Point, SensorNode> sensorMap){
        gc.setFill(Color.BLACK);
        int size = findSize(sensorMap);
        canvas.setWidth(GuiSize.SIZE*size);
        canvas.setHeight(GuiSize.SIZE*size);
        for(int i = 0;i<=size;i++){
            for(int j = 0;j<=size;j++){
                SensorNode sensorNode = sensorMap.get(new Point(j,i));
                if(sensorNode!= null){
                    GuiNodes guiNodes = new GuiNodes(sensorNode);
                    sensorNode.addObserver(guiNodes);
                    addEdge(sensorNode);
                    gridPane.add(guiNodes,j,i);
                }else {
                    Canvas blank = new Canvas(GuiSize.SIZE,GuiSize.SIZE);
                    gridPane.add(blank,j,i);
                }
            }
        }

        stackPane.getChildren().addAll(canvas,gridPane);
        stackPane.setAlignment(Pos.CENTER);
        gridPane.setGridLinesVisible(false);
        this.setContent(stackPane);
        this.setPannable(true);
    }

    //Get max size of the grid to be displayed.
    private int findSize(HashMap<Point, SensorNode> sensorMap){
        int max = 0;
        for(SensorNode node:sensorMap.values()){
            int x = node.getLocation().x;
            int y = node.getLocation().y;

            if(x>max){
                max = x;
            }
            if(y>max){
                max = y;
            }
        }
        return max;
    }

    //Generate Edges between nodes
    private void addEdge(SensorNode node){
        gc.setLineWidth(5);
        double originX =  node.getLocation().x*GuiSize.SIZE;
        double originY =  node.getLocation().y* GuiSize.SIZE;
        for(SensorNode n:node.getNeighborNodes()) {

            double x =   n.getLocation().x *GuiSize.SIZE;
            double y =   n.getLocation().y *GuiSize.SIZE;

            gc.setStroke(Color.BLACK);
            gc.strokeLine(originX, originY, x, y);
        }
    }

}
