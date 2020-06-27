package Mobile_Agents;
import java.awt.Point;
import java.util.*;
/**
 * Class: Message
 * Description: creates a message object that will hold information needed
 *               to be relayed
 * @Contributers Miguel Gonzalez, Tanner Randall Hunt
 */
public class Message {
    private String agentName;
    private long agentTimeCreation;
    private Point location;
    private Deque<Point> visitedLocations = new LinkedList<>();
    private Type messageType;

    /**
     * Defines the type of Message we need to relay.
     *
     * NODE_TO_NODE: Relays information regarding status of nodes
     *
     * NODE_TO_AGENT: Relays information regarding the status of the node
     * and how the Agent should act.
     *
     * AGENT_CREATION: Relays information regarding the creation of an agent on a given
     * node. Will be used for the base log.
     */
    public enum Type {
        NODE_DEATH,
        AGENT_CREATION,
        CATCH_FIRE,
        FORTIFICATION,
        CREATION
    }

    /**
     * Constructor that creates a message with a given type
     * @param messageType type of message
     */
    public Message(Type messageType) {
        this.messageType = messageType;
    }

    /**
     * Sets the location
     * @param location location of creation.
     */
    public void setLocation(Point location) {
        this.location = location;
    }

    /**
     * Sets the name of the agent
     * @param agentName name of agent
     */
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    /**
     * Sets the agents time of creation
     * @param agentTimeCreation time created
     */
    public void setAgentTimeCreation(long agentTimeCreation) {
        this.agentTimeCreation = agentTimeCreation;
    }

    /**
     * Gets the message type
     * @return type of message
     */
    public Type getMessageType() {
        return messageType;
    }

    /**
     * Pushes a node to its visited stack
     * @param point
     */
    public void pushVisitedNodes(Point point){
        visitedLocations.push(point);
    }


    /**
     * Get this messages visited points
     * @return visitedLocations
     */
    public Deque<Point> getVisitedLocations() {
        return visitedLocations;
    }

    /**
     * Sting representation of our message
     */
    public String toString(){
        String str = "";
        str = str.concat("\nAgent: "+agentName+"\nTime Created: "+agentTimeCreation);
        str = str.concat("\nCreated At: "+location+"\n");
        return str;
    }
}
