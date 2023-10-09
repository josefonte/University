using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace FairthosApp.Models.Domain
{
    [Table("Vendedor")]
    public class Vendedor
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column(Order = 1)]
        public int idVendedor { get; set; }

        [Column(Order = 2)]
        public string nome { get; set; }
    }
}
