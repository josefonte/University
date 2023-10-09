using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace FairthosApp.Models.Domain
{
    [Table("Compra")]
    public class Compra
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column(Order = 1)]
        public int idCompra { get; set; }

        [Column(Order = 2)]
        public int idCliente { get; set; }

        [Column(Order = 3)]
        public string data_emissao { get; set; }

        [Column(Order = 4)]
        public double precoTotal { get; set; }

        [Column(Order = 5)]
        public string moradaFaturacao { get; set; }

        [Column(Order = 6)]
        public double custoTotalEnvio { get; set; }

        [Column(Order = 7)]
        public int idEmpresaTransporte { get; set; }

        [Column(Order = 8)]
        public string metodoPagamento { get; set; }
    }
}
