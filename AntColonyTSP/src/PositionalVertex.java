public class PositionalVertex extends Vertex {
    private final int x;
    private final int y;

    public PositionalVertex(int id, int x, int y) {
        super(id);
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    @Override
    public String toString() {
        return super.toString() + ": (" + x + ", " + y + ")";
    }
}
