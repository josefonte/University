package business;

import java.time.LocalTime;
import java.util.Comparator;

public class Sortbytime implements Comparator<Participante> {
    public int compare(Participante p1, Participante p2) {
        LocalTime t1 = p1.tempoTotal();
        LocalTime t2 = p2.tempoTotal();
        if (t1.getHour() != t2.getHour()) {
            return t1.getHour() - t2.getHour();
        } else if (t1.getMinute() != t2.getMinute()) {
            return t1.getMinute() - t2.getMinute();
        } else {
            return t1.getSecond() - t2.getSecond();
        }
    }
}
