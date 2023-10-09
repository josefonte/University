import ErrorHandling.ResolutionException;
import ErrorHandling.SmartBulbException;
import ErrorHandling.SmartCameraException;
import ErrorHandling.SmartDeviceException;

public class SmartCamera extends SmartDevice {


    private Resolution resolution;
    private float file_size;


    public SmartCamera() {
        super();
        this.resolution = new Resolution();
        this.file_size = 0;
    }

    public SmartCamera(String id) {
        super(id);
        this.resolution = new Resolution();
        this.file_size = 0;
    }

    public SmartCamera(String id, boolean state, float custo, Resolution resolution, float file_size, float consumo) throws SmartDeviceException, SmartCameraException, ResolutionException {
        super(id,state,custo);
        super.setConsumo(consumo);

        if (resolution.getHeight()<0 || resolution.getWidth()<0) throw new ResolutionException(id+ " Resolution : Valor(es) Negativo(s)");
        if (file_size<0) throw new SmartCameraException(" Valores Negativos");

        this.resolution = resolution;
        this.file_size = file_size;
    }

    public SmartCamera(SmartCamera smt)  throws SmartDeviceException, SmartCameraException, ResolutionException {
        super(smt.getID(), smt.getOn(),smt.getCusto());
        super.setConsumo(smt.getConsumo());

        if (smt.getResolution().getHeight()<0 || smt.getResolution().getWidth()<0) throw new ResolutionException(smt.getID()+" Resolution : Valor(es) Negativos");
        if (smt.getFileSize()<0) throw new SmartCameraException(smt.getID() + "Valores Negativos");

        this.resolution = smt.getResolution();
        this.file_size = smt.getFileSize();


    }

    public Resolution getResolution() {
        return this.resolution;
    }

    public float getFileSize() { return this.file_size; }


    public void setResolution(Resolution res) throws ResolutionException{
        if (resolution.getHeight()<0 || resolution.getWidth()<0) throw new ResolutionException(" Atribuição de Valor(es) Negativo de Resolução");
        this.resolution = res;
    }

    public void setFileSize(float file_size) throws SmartCameraException {
        if(file_size<0) throw new SmartCameraException(" Atribuição de Valor Negativo de Tamanho de Ficheiro");
        this.file_size = file_size; }


    public SmartCamera clone(){
        try {
            return new SmartCamera(this);
        } catch (SmartDeviceException | SmartCameraException | ResolutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o){
        if (!super.equals(o)) return false;
        if (this==o) return true;
        if (!(o instanceof SmartCamera)) return false;
        SmartCamera smt = (SmartCamera) o;
        return (this.resolution.equals(smt.getResolution()) &&
                this.file_size==smt.getFileSize());
    }

    public String toString() {
        return super.toString() +
                "\nType: SmartBulb" +
                this.resolution +
                " | File Size: " + this.file_size;
    }
}


