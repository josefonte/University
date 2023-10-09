using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace FairthosApp.Models.Domain
{
    [Table("VendedorFeira")]
    public class VendedorFeira
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column(Order = 1)]
        public int idVendedorFeira { get; set; }

        [Column(Order = 2)]
        public int idVendedor { get; set; }

        [Column(Order = 3)]
        public int idFeira { get; set; }

        [Column(Order = 4)]
        public DateTime horaFuncionamentoPresencial_Inicio { get; set; }

        [Column(Order = 5)]
        public DateTime horaFuncionamentoPresencial_Fim { get; set; }
    }
}
