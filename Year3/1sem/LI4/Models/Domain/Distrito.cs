using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace FairthosApp.Models.Domain
{
    [Table("Distrito")]
    public class Distrito
    {
        [Key]
        [Column(Order = 1)]
        public string nome { get; set; }
    }
}
