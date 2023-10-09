using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace FairthosApp.Models.Domain
{
    [Table("EmpresaTransporte")]
    public class EmpresaTransporte
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column(Order = 1)]
        public int idEmpresaTransporte { get; set; }

        [Column(Order = 2)]
        public string nomeEmpresa { get; set; }
    }
}
