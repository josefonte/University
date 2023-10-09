using Microsoft.Data.SqlClient;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace FairthosApp.Models.Domain
{
    [Table("Feira")]
    public class Feira
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Column(Order = 1)]
        public int idFeira { get; set; }
        
        [Column(Order = 2)]
        public string nomeDistrito { get; set; }
        
        [Column(Order = 3)]
        public DateTime? horaFuncionamentoPresencial_Inicio  { get; set; }
        
        [Column(Order = 4)]
        public DateTime? horaFuncionamentoPresencial_Fim { get; set; }
        
        [Column(Order = 5)]
        public string? descricao { get; set; }
        
        [Column(Order = 6)]
        public string nome { get; set; }
        
        [Column(Order = 7)]
        public int idCategoria { get; set; }
        
        [Column(Order = 8)]
        public Boolean temporaria { get; set; }


    }
}
