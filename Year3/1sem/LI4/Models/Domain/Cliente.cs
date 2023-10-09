using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace FairthosApp.Models.Domain
{
    [Table("Cliente")]
    public class Cliente
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column(Order = 1)]
        public int idCliente { get; set; }

        [Column(Order = 2)]
        public string email { get; set; }

        [Column(Order = 3)]
        public string password { get; set; }

        [Column(Order = 4)]
        public string nome { get; set; }
    }
}
