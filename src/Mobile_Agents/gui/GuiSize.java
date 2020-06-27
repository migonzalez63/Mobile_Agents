package Mobile_Agents.gui;
/**
 * Class: GuiSize
 * Description: Holds node sizes used in the gui. Ensure easy with
 *              uniform sizing through out the gui.
 */
public class GuiSize {
    public static final double SIZE = 45;
    public static final double SCALE = .1;
    public static final double SENSOR_SIZE = SIZE - SIZE*SCALE;
    public static final double AGENT_SIZE = SIZE - SIZE*SCALE;
    public static final double SENSOR_OFFSET = SENSOR_SIZE*(SCALE/2);
    public static final double AGENT_OFFSET = AGENT_SIZE*(SCALE/2);
}
