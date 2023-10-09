import ErrorHandling.CasaInteligenteException;
import ErrorHandling.FornecedorException;
import ErrorHandling.SmartDeviceException;

public class FornecedorA extends Fornecedor{

    FornecedorA(){
        super();
    }

    FornecedorA(String name, float valor_base, float imposto, float desconto) throws FornecedorException {
        super(name, valor_base, imposto, desconto);
    }

    FornecedorA(FornecedorA f) throws FornecedorException {
        super(f.getName(),f.getValor_base(),f.getImposto(),f.getDesconto());
    }

    public String toString() {
        return  "\n\n### FornecedorA ###" +
                "\nFornecedor: " + super.getName() +
                " | ValorBase: " + super.getValor_base() +
                " | Imposto: " + super.getImposto() +
                " | Desconto: " + super.getDesconto() +
                "\n";
    }

    public FornecedorA clone(){
        try {
            return new FornecedorA(this);
        } catch (FornecedorException e) {
            throw new RuntimeException("Clone FornecedorA Failed");
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
            return (super.getValor_base()*smt.getConsumo()* (1+super.getImposto()/100))*(1-super.getDesconto()/100);
        }
    }
}
