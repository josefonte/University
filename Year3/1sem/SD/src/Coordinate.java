import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.CoderResult;
import java.util.Objects;

public class Coordinate {
    private int x;
    private int y;


    public Coordinate(int x, int y){
        this.x =x;
        this.y = y;
    }

    public Coordinate(Coordinate other){
        x = other.x;
        y= other.y;
    }

    public void serialize (DataOutputStream out) throws IOException {
        out.writeInt(x);
        out.writeInt(y);
    }
    public static  Coordinate deserialize (DataInputStream in) throws IOException {
        int x = in.readInt();
        int y = in.readInt();
        return new Coordinate(x,y);
    }


    public Coordinate clone(){
        return new Coordinate(this);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate coordinate = (Coordinate) o;
        return x == coordinate.x && y == coordinate.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int distance(Coordinate c){
        return (java.lang.Math.abs(x - c.getX()))
                + (java.lang.Math.abs(y - c.getY()));
    }

    @Override

    public String toString() {
        StringBuilder sb = new StringBuilder();

            sb.append("\n");
            sb.append("Trotinete(");
            sb.append(" x= ").append(x);
            sb.append(" y= ").append(y);
            sb.append(")");

        return sb.toString();
    }
}
