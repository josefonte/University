import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reserve {

    private Coordinate c;

    private String id;
    private boolean success;

    private LocalDateTime reserveDate;

    private Coordinate reward =null;


    public Reserve(Coordinate c,  String id, boolean b, LocalDateTime localdate){
        success=true;
        this.c=c;
        this.id=id;
        reserveDate= localdate;

    }

    public Reserve(Coordinate c, String id) {
        success = true;
        this.c = c;
        this.id = id;
        reserveDate = LocalDateTime.now();

    }

    public Reserve(Coordinate c, String id,Coordinate reward) {
        success = true;
        this.c = c;
        this.id = id;
        reserveDate = LocalDateTime.now();
        this.reward = reward;

    }

    public boolean hasReward(){
        return this.reward!=null;
    }

    public Coordinate getReward(){
        return this.reward;
    }

    public void serialize (DataOutputStream out) throws IOException {
        c.serialize(out);
        out.writeUTF(id);
        out.writeBoolean(success);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String date = formatter.format(reserveDate);
        out.writeUTF(date);
    }


    public static Reserve deserialize (DataInputStream in) throws IOException {
        Coordinate c = Coordinate.deserialize(in);
        String id = in.readUTF();
        boolean b = in.readBoolean();
        String time = in.readUTF();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
        return  new Reserve(c,id,b,dateTime);
    }

    public Coordinate getC() {
        return c;
    }

    public void setC(Coordinate c) {
        this.c = c;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "Reserva{" + c +
                ", id='" + id + '\'' +
                ", success=" + success +
                ", reserveDate=" + reserveDate +
                "\n}";
    }

}
