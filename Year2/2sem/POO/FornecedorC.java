import ErrorHandling.CasaInteligenteException;
import ErrorHandling.FornecedorException;
import ErrorHandling.SmartDeviceException;

public class FornecedorC extends Fornecedor{

    FornecedorC(){
        super();
    }

    FornecedorC(String name, float valor_base, float imposto, float desconto) throws FornecedorException {
        super(name, valor_base, imposto, desconto);
    }

    FornecedorC(FornecedorC f) throws FornecedorException {
        super(f.getName(),f.getValor_base(),f.getImposto(),f.getDesconto());
    }

    public String toString() {
        return  "\n### FornecedorC ###" +
                "\nFornecedor: " + super.getName() +
                " | ValorBase: " + super.getValor_base() +
                " | Imposto: " + super.getImposto() +
                " | Desconto: " + super.getDesconto() +
                "\n";
    }

    public FornecedorC clone(){
        try {
            return new FornecedorC(this);
        } catch (FornecedorException e) {
            throw new RuntimeException("Clone FornecedorC Failed");
        }
    }

    public float formulaPreco(SmartDevice smt, CasaInteligente casa) throws CasaInteligenteException, SmartDeviceException, FornecedorException {
        if (smt == null || smt.getConsumo() < 0)
            throw new SmartDeviceException("SmartDevice não existe/Valores Inválidos");
        else if (super.getDesconto() < 0 || super.getImposto() < 0 || super.getValor_base() < 0)
            throw new FornecedorException("Valores Negativos");
        else if (casa==null || !casa.getDevices().containsKey(smt.getID())){
            throw new CasaInteligenteException("CasaNULL/SmartDevice não existe na casa " + smt.getID());}
        else {
            if (casa.numberDevices() < 10)
                return (super.getValor_base() * smt.getConsumo() * (1 + super.getImposto() / 100)) * (1 - (super.getDesconto()) / 100);
            else if (casa.numberDevices() < 20)
                return (super.getValor_base() * smt.getConsumo() * (1 + super.getImposto() / 100)) * (1 - (2 * super.getDesconto()) / 100);
            else
                return (super.getValor_base() * smt.getConsumo() * (1 + super.getImposto() / 100)) * (1 - (3 * super.getDesconto()) / 100);
        }
    }
}
