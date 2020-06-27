package Mobile_Agents;


import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Class: SensorNode
 * Description: Will create the sensor nodes, identify their neighbors, be able
 *              to hold agents and relay information regarding the current state of the
 *              simulation to its neighbors.
 * @Contributers Miguel Gonzalez, Tanner Randall Hunt
 */
public class SensorNode extends Observable implements Runnable{

    private boolean fireSpread = true;

    private BlockingQueue<Message> inbox;
    private String log = "";
    private Point location;
    private List<SensorNode> neighborNodes = new ArrayList<>();
    private List<SensorNode> channels = new ArrayList<>();
    private Agent nodeAgent;
    private Status nodeStatus;
    private boolean isFireStation;
    private int i = 0;

    /**
     * Defines the status of the node
     *
     * OK: Node is safe and surrounding nodes are also safe
     *
     * CLOSE_TO_FIRE: Node has at least one neighbor that has caught on fire
     *
     * ON_FIRE: Node has caught on fire and is burning
     *
     * FORTIFIED, CASUALTY, and OCCUPIED are used by the GUI to represent
     * the status of the agent and do not contribute to the logic
     * of the node
     */
    public enum Status {
        OK, CLOSE_TO_FIRE, ON_FIRE,FORTIFIED,CASUALTY,OCCUPIED
    }

    /**
     * Constructor that creates a sensor node
     */
    public SensorNode(Point location) {
        this.nodeAgent = null;
        this.nodeStatus = Status.OK;
        this.inbox = new LinkedBlockingQueue<>();
        this.location = location;
        this.isFireStation = false;
    }
    /*
     * Puts a message into the nodes inbox to be processed
     */
    public synchronized void putInInbox(Message message) {
        try{
            inbox.put(message);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isFireStation() {
        return isFireStation;
    }

    /**
     * While a node is not on fire, we will parse each message communicated
     * to us and propagate necessary information to our neighbors.
     */
    @Override
    public void run() {
        while(nodeStatus != Status.ON_FIRE) {
            try {
                Message incomingMessage = inbox.take();
                processMessage(incomingMessage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifyNeighbors(Message.Type.NODE_DEATH);
        notifyAgent(Message.Type.NODE_DEATH);
        updateStatus();
    }

    /*
     * Whenever the node receives a message, it will process the type and
     * act accordingly.
     */
    private void processMessage(Message message) {
        switch (message.getMessageType()) {
            case FORTIFICATION:{
                /*
                 * Agent has been created in node and is fortified, meaning
                 * that one of our neighbors has caught on fire
                 */
                if(nodeAgent!= null && nodeStatus==Status.CLOSE_TO_FIRE){
                    notifyNeighbors(Message.Type.AGENT_CREATION);
                }

                break;
            }
            case NODE_DEATH:{
                /*
                 * Node has caught on fire, will notify neighbors of its death
                 */
                this.nodeStatus = Status.CLOSE_TO_FIRE;

                if(nodeAgent!= null){
                    notifyNeighbors(Message.Type.AGENT_CREATION);
                }
                catchFire();
                break;
            }
            case AGENT_CREATION: {
                /*
                 * An agent has been created on another node and has to be
                 * propagated back to the base station
                 */
                createAgent();
                break;

            }
            case CATCH_FIRE:{
                /*
                 * Node has caught on fire.
                 */
                nodeStatus = Status.ON_FIRE;

                break;
            }
            case CREATION:{
                /*
                 * We have received a message about a creation of the node.
                 * If we are the base station, we will add that information to
                 * our log, else we will attempt to propagate it to the
                 * base station
                 */
                if(isFireStation){
                    log  = log.concat(message.toString());
                    setChanged();
                    notifyObservers();
                }else sendOnChannel(message);
            }

        }
        updateStatus();

    }



    /*
     * Helper method in order to catch the current node on fire.
     * It creates a separate thread that will sleep for a random
     * number of seconds before sending a message to the node to
     * catch itself on fire
     */
    private void catchFire(){
        if(fireSpread) {
            Runnable runnable = ()-> {

                Random random = new Random();
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(random.nextInt(2)+2));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                putInInbox(new Message(Message.Type.CATCH_FIRE));
            };
            new Thread(runnable).start();
        }
    }



    /*
     * Helper method used to create a new message and send it residing agent.
     */
    private void notifyAgent(Message.Type type) {
        if(nodeAgent != null) {
            Message message = new Message(type);
            nodeAgent.putInInbox(message);
        }
    }

    /*
     * Sends a message to neighboring nodes in order to properly
     * propagate information
     */
    private void notifyNeighbors(Message.Type type) {
        Message message = new Message(type);
        for (SensorNode node : neighborNodes) {
            if(node.getNodeStatus() != Status.ON_FIRE) {
                node.putInInbox(message);
            }
        }

    }

    //Sends message  to a random neighbor node given that is on a channel path.
    private void sendOnChannel(Message message ){
        Collections.shuffle(channels);
        for(SensorNode node:channels){
            if(node.getNodeStatus()!=Status.ON_FIRE
                &&!message.getVisitedLocations().contains(node.getLocation())){
                message.pushVisitedNodes(node.getLocation());
                node.putInInbox(message);
                break;
            }
        }
    }


    /**
     * Returns our log
     * @return log
     */
    public String getLog() {
        return log;
    }

    /**
     * Gets the status of the node
     * @return Status of Node
     */
    public Status getNodeStatus() {
        return nodeStatus;
    }

    /**
     * Gets the current agent residing in the node
     * @return
     */
    public synchronized Agent getNodeAgent() {
        return nodeAgent;
    }

    /**
     * Sets an agent for the current node
     * @param newAgent
     */
    public synchronized void setNodeAgent(Agent newAgent) {
        nodeAgent = newAgent;
    }


    /**
     * Declares a node to be a fire station. Will flip a flag to indicate as such and create an
     * agent on the node so it can begin to look for fires.
     */
    public void setFireStation() {
        isFireStation = true;
    }

    /**
     * Adds a neighboring node to the list
     * @param neighbor neighboring node
     */
    public void addNeighbor(SensorNode neighbor){
        neighborNodes.add(neighbor);
    }

    /**
     * Adds a designated channel neighbor.
     * @param neighbor neighboring node that is component in a channel
     */
    public void addChannel(SensorNode neighbor){
        channels.add(neighbor);
    }

    /**
     * Manually sets the node on fire.
     */
    public void setOnFire(){
        nodeStatus = Status.ON_FIRE;
        notifyNeighbors(Message.Type.NODE_DEATH);
    }


    /**
     * Gets the node Location
     * @return location
     */
    public Point getLocation(){
        return location;
    }

    /**
     * String representation of our node
     */
    public synchronized String toString(){
        String str = "Sensor Location: ".concat(location.toString()+"\n");
        str = str.concat("Is Fire Station? " + isFireStation + "\n");
        str = str.concat("Status: "+nodeStatus+"\n");
        if(nodeAgent != null) {
            str = str.concat("Current Agent: " + nodeAgent.getName() + "\n");
        } else {
            str = str.concat("Current Agent: null\n");
        }
        str = str.concat("Neighbors:\n");
        for(SensorNode sensorNode:neighborNodes){
            str = str.concat("\t"+sensorNode.getLocation().toString()+"\n");
        }
        str = str.concat("Channels:\n");
        for(SensorNode sensorNode:channels){
            str = str.concat("\t"+sensorNode.getLocation().toString()+"\n");
        }

        return str;
    }

    /**
     * Gets the neibors of this sensor
     */
    public  List<SensorNode> getNeighborNodes() {
        return neighborNodes;
    }

    public List<SensorNode> getChannels() {
        return channels;
    }

    /**
     * Starts the agents thread
     */
    public  void dispatch(){
        isFireStation = true;
        String hash = "[" + location.x + ", " + location.y + "] #" + i++;
        Agent agent = new Agent(Integer.toString(hash.hashCode()), this, Agent.Status.SEARCHING);
        new Thread(agent).start();
    }

    /*
     * Creates an agent on the given node
     */
    private void createAgent(){
        if(nodeAgent==null){
            String hash = "[" + location.x + ", " + location.y + "] #" + i++;
            nodeAgent = new Agent(Integer.toString(hash.hashCode()),this, Agent.Status.FORTIFIED);
            new Thread(nodeAgent).start();
        }
    }

    /**
     * Updates the status of the node with its proper GUI enums in order
     * to be represented properly
     */
    public void updateStatus(){
        Status status = nodeStatus;
        if(nodeAgent != null && nodeStatus == Status.ON_FIRE){
            status = Status.CASUALTY;
        }else if(nodeAgent != null && nodeStatus == Status.CLOSE_TO_FIRE){
            status = Status.FORTIFIED;
        }else if(nodeAgent != null && nodeStatus != Status.ON_FIRE){
            status = Status.OCCUPIED;
        }
        setChanged();
        notifyObservers(status);
    }
}
