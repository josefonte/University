import ErrorHandling.ResolutionException;

import java.io.*;

public class Resolution implements Serializable {

    private float height;
    private float width;

    public Resolution() {
        this.width = 0;
        this.height = 0;
    }

    public Resolution(float w, float h) throws ResolutionException {
        if(w<0 || h<0) throw new ResolutionException("Valor(es) Negativos");
        this.width = w;
        this.height = h;
    }

    public Resolution(Resolution r) {
        this.width = r.getWidth();
        this.height = r.getHeight();
    }

    public Resolution(String[] res) {
        this.width = Float.parseFloat(res[0]);
        this.height = Float.parseFloat(res[1]);
    }

    public float getWidth() {return this.width;}

    public float getHeight() {return this.height;}

    public void setWidth(float width) {this.width = width;}

    public void setHeight(float height) {this.height = height;}

    public float getRes(){
        double w = this.width;
        double h = this.height;

        return  (float) Math.sqrt(h+w);
    }

    public Resolution clone(){return new Resolution(this);}

    public String toString() {
        return " | Resolution: " + this.width + " x " + this.height;
    }

    public boolean equals(Object o) {
        if (o==this) return true;
        if (o==null || o.getClass()!= this.getClass()) return false;
        Resolution res = (Resolution) o;
        return (this.width == res.getWidth() &&
                this.height == res.getHeight());
    }

    public void guardaResolution(String nomeFicheiro) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(nomeFicheiro);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.flush();
        oos.close();
        fos.close();
    }

    public static Resolution carregaResolution(String nomeFicheiro) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fos = new FileInputStream(nomeFicheiro);
        ObjectInputStream oos = new ObjectInputStream(fos);
        Resolution c = (Resolution) oos.readObject();
        oos.close();
        fos.close();
        return c;
    }
}
