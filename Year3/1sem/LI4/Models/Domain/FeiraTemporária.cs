using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace FairthosApp.Models.Domain
{
    [Table("FeiraTemporária")]
    public class FeiraTemporária
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column(Order = 1)]
        public int idFeira { get; set; }

        [Column(Order = 2)]
        public DateTime dataInicio { get; set; }

        [Column(Order = 3)]
        public DateTime dataFim { get; set; }
    }
}
