package mdpalgo.constants;

public enum Direction {
    NORTH(0, 1, 0),
    EAST(1, 0, 1),
    SOUTH(2, -1, 0),
    WEST(3, 0, -1);

    private final int ordinal;
    private final int deltaX;
    private final int deltaY;

    Direction(int ordinal, int deltaX, int deltaY) {
        this.ordinal = ordinal;
        this.deltaX = deltaX;
        this.deltaY = deltaY;

    }

    public static Direction getDirectionByDelta(int deltaX, int deltaY) {
        for (Direction direction : Direction.values()) {
            if (direction.deltaX == deltaX && direction.deltaY == deltaY)
                return direction;
        }
        return null;
    }

    public static int getDirectionDiff(Direction currentDirection, Direction targetDirection) {
        int diff = targetDirection.ordinal - currentDirection.ordinal;
        if (diff == 3)
            // 3 turns equals 1 turn
            diff = -1;
        else if (diff == -3)
            diff = 1;
        return diff;
    }

    public static Movement getMovementByDirections(Direction currentDirection, Direction targetDirection) {
        int diff = getDirectionDiff(targetDirection, currentDirection);
        switch (diff) {
            case 1:
                return Movement.RIGHT;
            case -1:
                return Movement.LEFT;
            case 0:
                return Movement.FORWARD;
            case 2:
                return Movement.BACKWARD;
        }
        throw new RuntimeException("Unexpected direction");
    }

    public Direction turnRight() {
        return values()[(this.ordinal + 1) % 4];
    }

    public Direction turnLeft() {
        return values()[(this.ordinal + 3) % 4];
    }

    public int[] getRight(int x, int y) {
        return turnRight().forward(x, y);
    }

    public int[] getLeft(int x, int y) {
        return turnLeft().forward(x, y);
    }

    public int[] getFrontRight(int x, int y) {
        int[] frontPos = forward(x, y);
        return getRight(frontPos[0], frontPos[1]);
    }

    public int[] getFrontLeft(int x, int y) {
        int[] frontPos = forward(x, y);
        return getLeft(frontPos[0], frontPos[1]);
    }

    public Direction rotate(Movement movement) {
        switch (movement) {
            case LEFT:
                return turnLeft();
            case RIGHT:
                return turnRight();
            case BACKWARD:
                return turnRight().turnRight();
            default:
                return this;
        }
    }

    public int[] forward(int x, int y, int step) {
        return new int[]{x + deltaX * step, y + deltaY * step};
    }

    public int[] backward(int x, int y, int step) {
        return new int[]{x - deltaX * step, y - deltaY * step};
    }

    public int[] forward(int x, int y) {
        return forward(x, y, 1);
    }
}