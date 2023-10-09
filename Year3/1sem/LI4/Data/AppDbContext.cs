using FairthosApp.Models.Domain;
using Microsoft.EntityFrameworkCore;


namespace FairthosApp.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
        {

        }
        /*
        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Produto>().ToTable("Produto");
            modelBuilder.Entity<Categoria>().ToTable("Categoria");
            modelBuilder.Entity<Cliente>().ToTable("Cliente");
            modelBuilder.Entity<Distrito>().ToTable("Distrito");
            modelBuilder.Entity<EmpresaTransporte>().ToTable("EmpresaTransporte");
            modelBuilder.Entity<Feira>().ToTable("Feira");
            modelBuilder.Entity<FeiraTemporária>().ToTable("FeiraTemporária");
            modelBuilder.Entity<ProdutoCompra>().ToTable("ProdutoCompra");

            // completar
        } */

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
