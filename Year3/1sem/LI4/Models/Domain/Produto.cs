using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace FairthosApp
{
    [Table("Produto")]
    public class Produto
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column(Order = 1)]
        public int idProduto { get; set; }

        [Column(Order = 2)]
        public string nome { get; set; }

        [Column(Order = 3)]
        public double precoUnidade { get; set; }

        [Column(Order = 4)]
        public int idVendedor { get; set; }

        [Column(Order = 5)]
        public int idCategoria { get; set; }
    }
}
