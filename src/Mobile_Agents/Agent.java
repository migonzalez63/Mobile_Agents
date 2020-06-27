
package Mobile_Agents;

import java.awt.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class: Agent
 * Description: Monitors location of fire spreading and sends creation message
 *              through sensor map.
 * @Contributers Miguel Gonzalez, Tanner Randall Hunt
 */
public class Agent implements Runnable {
    private BlockingQueue<Message> inbox;
    private String name;
    private long timeCreated;
    private Status agentStatus;
    private SensorNode residingNode;
    private LinkedList<Point> visited = new LinkedList<>();

    @Override
    public void run() {
        while (agentStatus != Status.DEAD){
            if (agentStatus == Status.SEARCHING) {
                move(residingNode);
            }
            if(agentStatus == Status.FORTIFIED){
                try {
                    Message message = inbox.take();

                    if(message.getMessageType() == Message.Type.NODE_DEATH){
                        this.agentStatus = Status.DEAD;
                    }
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Defines the status of the agent.
     *
     * SEARCHING: Agent is alive and is looking around the graph for a fire
     *
     * FORTIFIED: Agent has found a node that is near a fire and is beginning to monitor it
     *
     * DEAD: Agent is dead
     */
    public enum Status {
        SEARCHING, DEAD, FORTIFIED
    }

    /**
     * Constructor that creates an agent and gets the current time in which the agent
     * was created. Sends a message of creation to its residing node
     * @param name
     */
    public Agent(String name, SensorNode residingNode, Status agentStatus) {
        inbox = new LinkedBlockingQueue<>();
        this.name = name;
        timeCreated = System.currentTimeMillis();
        this.residingNode = residingNode;
        this.agentStatus = agentStatus;
        if(agentStatus==Status.FORTIFIED){
            residingNode.putInInbox(new Message(Message.Type.FORTIFICATION));
        }
        Message creation = new Message(Message.Type.CREATION);
        creation.setAgentName(name);
        creation.setAgentTimeCreation(timeCreated);
        creation.setLocation(residingNode.getLocation());
        residingNode.putInInbox(creation);
    }

    /**
     * Gets the name of the agent
     * @return Agent name
     */
    public String getName() {
        return name;
    }

    /**
     * Adds message into the inbox
     * @param message
     */
    public void putInInbox(Message message) {
        try {
            inbox.put(message);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * Handles the moving of the agent while it searches for a node that
     * is close to the fire. Does a "random" directed search, where it
     * will randomly choose on of the neighbors if it hasn't been visited
     * before or if it is not on fire.
     */
     private void move(SensorNode current) {
         if(current.getNodeStatus()== SensorNode.Status.CLOSE_TO_FIRE) {
             agentStatus = Status.FORTIFIED;
             residingNode = current;

             current.setNodeAgent(this);
             current.putInInbox(new Message(Message.Type.FORTIFICATION));
             return;

         }
        Collections.shuffle(current.getNeighborNodes());
        if(!visited.contains(current.getLocation())) {
            visited.add(current.getLocation());
            for (SensorNode currNeighbor : current.getNeighborNodes()) {
                if (currNeighbor.getNodeAgent() == null) {
                    if (!visited.contains(currNeighbor.getLocation())) {
                        move(currNeighbor);
                    }
                }
            }
        }
     }

    /**
     * String representation of our agent
     * @return name and time of creation
     */
    @Override
    public String toString() {
        return name + " created at " + timeCreated;
    }

}
