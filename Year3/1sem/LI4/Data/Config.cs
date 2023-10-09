using FairthosApp.Models.Domain;

using System.Data.Entity;

namespace FairthosApp.Data
{
    public class Config : DbContext
    {
        public Config() : base("FairthosApp")
        {

        }

        public DbSet<Produto> Produtos { get; set; }
        public DbSet<Categoria> Categorias { get; set; }
        public DbSet<Cliente> Clientes { get; set; }
        public DbSet<Distrito> Distritos { get; set; }
        public DbSet<EmpresaTransporte> EmpresasTransporte { get; set; }
        public DbSet<Feira> Feiras { get; set; }
        public DbSet<FeiraTemporária> FeirasTemporarias { get; set; }
        public DbSet<ProdutoCompra> ProdutoCompras { get; set; }
        public DbSet<ReservaProduto> ReservasProdutos { get; set; }

        public DbSet<Vendedor> Vendedores { get; set; }
        public DbSet<VendedorFeira> VendedoresFeiras { get; set; }
    }
}
