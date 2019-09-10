package mdpalgo.constants;

public enum Movement {
    FORWARD, RIGHT, LEFT, BACKWARD;
	
	public static String print(Movement m) {
        switch (m) {
            case FORWARD:
                return "forward";
            case BACKWARD:
                return "backward";
            case RIGHT:
                return "right";
            case LEFT:
                return "left";
            default:
                return " ";
        }
    }
}
