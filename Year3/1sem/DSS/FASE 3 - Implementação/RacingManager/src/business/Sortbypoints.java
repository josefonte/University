package business;

import java.time.LocalTime;
import java.util.Comparator;

public class Sortbypoints implements Comparator<Participante> {
    public int compare(Participante p1, Participante p2) {
        return p2.getPontuacao() - p1.getPontuacao();
    }
}

