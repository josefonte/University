using Microsoft.EntityFrameworkCore;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace FairthosApp.Models.Domain
{
    [Table("ProdutoCompra")]
    public class ProdutoCompra
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column(Order = 1)]
        public int Id { get; set; }

        [Column(Order = 2)]
        public int idProduto { get; set; }

        [Column(Order = 3)]
        public int idCompra { get; set; }

        [Column(Order = 4)]
        public int quantidade { get; set; }

        [Column(Order = 5)]
        public double valorTotal { get; set; }
    }
}
