using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace FairthosApp.Models.Domain
{
    [Table("ReservaProduto")]
    public class ReservaProduto
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column(Order = 1)]
        public int idReservaProduto { get; set; }

        [Column(Order = 2)]
        public int idProduto { get; set; }

        [Column(Order = 3)]
        public int idCliente { get; set; }

        [Column(Order = 4)]
        public int quantidade { get; set; }
    }
}
