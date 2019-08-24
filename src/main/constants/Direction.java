package constants;

public enum Direction {
    North(0, 0, -1),
    East(1, 1, 0),
    Sorth(2, 0, 1),
    West(3, -1, 0);

    private final int ordinal;
    private final int deltaX;
    private final int deltaY;

    Direction(int ordinal, int deltaX, int deltaY) {
        this.ordinal = ordinal;
        this.deltaX = deltaX;
        this.deltaY = deltaY;

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
        return turnRight().forward(frontPos[0], frontPos[1]);
    }

    public int[] getFrontLeft(int x, int y) {
        int[] frontPos = forward(x, y);
        return turnLeft().forward(frontPos[0], frontPos[1]);
    }

    public Direction rotate(Movement movement) {
        switch (movement) {
            case LEFT:
                return turnLeft();
            case RIGHT:
                return turnRight();
            default:
                return this;
        }
    }

    public int[] forward(int x, int y, int step) {
        return new int[]{x + deltaX * step, y + deltaY * step};
    }

    public int[] forward(int x, int y) {
        return forward(x, y, 1);
    }
}