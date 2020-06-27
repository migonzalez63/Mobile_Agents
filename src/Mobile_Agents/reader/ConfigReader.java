package Mobile_Agents.reader;

import Mobile_Agents.SensorNode;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Class: ConfigReader
 * Description: Reads a config file and assembles a hashMap containing
 *              sensors nodes, assigns neighbors based on the configuration file
 *              and establishes paths back to the designated fire station.
 * @Contributer Tanner Randall Hunt
 */
public class ConfigReader {

    private HashMap<Point, SensorNode> sensorMap = new HashMap<>();
    private LinkedList<LinkedList<SensorNode> > channels;
    private Point stationLocation;
    private Point fireOrigin;
    private List<Point[]> edges = new ArrayList<>();

    public ConfigReader(String fileName){
        readIn(fileName);
        assignNeighbors();
        channels = new LinkedList<>();
        SensorNode fireStation = sensorMap.get(stationLocation);
        fireStation.setFireStation();
        constructChannels();
        setChannels();


    }

    //Reading in from a config file
    private void readIn(String fileName)  {
        try {
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(" ");
                switch (line[0]) {
                    case "node": {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);
                        Point location = new Point(x, y);
                        SensorNode sensorNode = new SensorNode(location);
                        sensorMap.put(location, sensorNode);
                        break;
                    }
                    case "edge": {
                        int x1 = Integer.parseInt(line[1]);
                        int y1 = Integer.parseInt(line[2]);
                        int x2 = Integer.parseInt(line[3]);
                        int y2 = Integer.parseInt(line[4]);
                        Point[] points = {new Point(x1, y1), new Point(x2, y2)};
                        edges.add(points);
                        break;
                    }
                    case "station": {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);
                        stationLocation = new Point(x, y);
                        break;
                    }
                    case "fire": {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);
                        fireOrigin = new Point(x, y);
                        break;
                    }

                }
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     * Gets the HashMap of Sensor Nodes.
     * @return
     */
    public HashMap<Point, SensorNode> getSensorMap() {
        return sensorMap;
    }

    //Assigns neighbors outlined in the config file.
    private void assignNeighbors(){
        for(Point[] points : edges){
            SensorNode neighbor1 = sensorMap.get(points[0]);
            SensorNode neighbor2 = sensorMap.get(points[1]);
            neighbor1.addNeighbor(neighbor2);
            neighbor2.addNeighbor(neighbor1);
        }
    }

    /**
     * Signals the node designated as the "Fire Station" to dispatch it's agent.
     * (Start the agent Thread -> Begin Searching)
     */
    public void dispatch(){
        SensorNode fireStation = sensorMap.get(stationLocation);
        fireStation.dispatch();
    }

    /**
     * Sets sensor node designated as the fire origin on fire.
     */
    public void ignite(){
        sensorMap.get(fireOrigin).setOnFire();
    }

    //Constructs paths from each node back to the fire station and assigns channel
    //neighbors.
    private void constructChannels(){
        SensorNode fireStation = sensorMap.get(stationLocation);
        for(SensorNode target:sensorMap.values()) {
            HashMap<SensorNode,SensorNode> parents = new HashMap<>();
            LinkedList<SensorNode> visited = new LinkedList<>();
            constructChannels(fireStation,target,visited,parents);
        }
    }
    private void constructChannels(SensorNode current, SensorNode target,
                                   LinkedList<SensorNode> visited,
                                   HashMap<SensorNode,SensorNode> parents){
        visited.add(current);

        if(current.equals(target)){

            channels.add(extractChannel(parents,target));
            return;
        }
        Collections.shuffle(current.getNeighborNodes());
        for(SensorNode node:current.getNeighborNodes()) {
            if (!visited.contains(node)) {
                parents.put(node,current);
                constructChannels(node, target, visited, parents);
            }
        }

    }
    public SensorNode getFireStation(){
        return sensorMap.get(stationLocation);
    }
    //Extracts Channel form parent child map made during dfs.
    private LinkedList<SensorNode> extractChannel(HashMap<SensorNode,
            SensorNode> parents,SensorNode start){
        if (parents ==null||parents.isEmpty()) return null;

        LinkedList<SensorNode>  channel = new LinkedList<>();
        channel.add(start);
        SensorNode current = parents.get(start);

        while(!current.getLocation().equals(stationLocation)){

            channel.add(current);
            current = parents.get(current);
        }
        if(current.getLocation().equals(stationLocation)){
            channel.add(current);
        }
        return channel;
    }

    //Set Channel neighbor nodes for each sensor node in the map.
    private void setChannels(){
        for(LinkedList<SensorNode>  channel :channels){
            setChannels(channel);
        }
    }
    private void setChannels(LinkedList<SensorNode>  channel){
        if(channel != null&&!channel.isEmpty()) {
            SensorNode node1 = channel.pop();
            if(channel.isEmpty()) return;
            SensorNode node2 = channel.peek();
            if (node1.getChannels().contains(node2)){
                channel.remove();
                setChannels(channel);
            }else{
                node1.addChannel(node2);
                setChannels(channel);
            }
        }
    }
}
