package psd.trabalho;

import java.util.List;




public class Pair<P1, P2> {
    private final P1 pair1;
    private final P2 pair2;

    public Pair(P1 pair1, P2 pair2) {
        this.pair1 = pair1;
        this.pair2 = pair2;
    }

    public P1 getPair1() {
        return this.pair1;
    }

    public P2 getPair2() {
        return this.pair2;
    }

    public List<Object> getValues(){
        return List.of(this.pair1, this.pair2);
    }


}