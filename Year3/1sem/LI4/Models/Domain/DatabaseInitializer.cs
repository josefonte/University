using System.Collections.Generic;
using System.Data.Entity;
using FairthosApp.Data;

namespace FairthosApp.Models.Domain
{
    public class DatabaseInitializer : DropCreateDatabaseIfModelChanges<Config>
    {
        protected override void Seed(Config context)
        {
            GetProdutos().ForEach(p => context.Produtos.Add(p));
        }

        private static List<Produto> GetProdutos()
        {
            var produtos = new List<Produto> {
                new Produto
                {
                    nome = "Favas",
                    precoUnidade = 0.5,
                    idVendedor = 2,
                    idCategoria = 1
                }
            };

            return produtos;
        }
    }
}
