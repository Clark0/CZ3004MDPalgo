package mdpalgo.constants;

public enum Direction {
    NORTH(0, 0, 1),
    EAST(1, 1, 0),
    SOUTH(2, 0, -1),
    WEST(3, -1, 0);

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

    public static int getDirectionDiff(Direction d1, Direction d2) {
        int diff = Math.abs(d1.ordinal - d2.ordinal);
        if (diff == 3)
            // 3 turns equals 1 turn
            diff = 1;
        return diff;
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

    public static void main(String[] args) {
        System.out.println(Direction.getDirectionByDelta(0, 1));
        System.out.println(Direction.getDirectionByDelta(1, 0));
        System.out.println(Direction.getDirectionByDelta(0, -1));
        System.out.println(Direction.getDirectionByDelta(-1, 0));

        System.out.println(Direction.getDirectionDiff(Direction.NORTH, Direction.SOUTH));
        System.out.println(Direction.getDirectionDiff(Direction.NORTH, Direction.EAST));
        System.out.println(Direction.getDirectionDiff(Direction.NORTH, Direction.WEST));
        System.out.println(Direction.getDirectionDiff(Direction.NORTH, Direction.NORTH));

    }

}