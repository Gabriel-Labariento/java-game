/**
 * This class holds the essential information required in serialization and deserialization.
 */
public class NetworkProtocol {
    public static final String PLAYER = "P";  // Player data
    public static final String USER_PLAYER = "P$"; // user player data
    public static final String ENTITY = "E";  // Other entities
    public static final String MAP_DATA = "M";       // Full map
    public static final String ROOM = "R";    // Room transition
    public static final String DOOR = "D"; // Door transition
    public static final String DELIMITER = "|";
    public static final String SUB_DELIMITER = ",";
}
