using FairthosApp.Models.Domain;
using System.Data.Entity;

namespace FairthosApp.Data
{
    public class DatabaseInitializer
    {
        public static void Initialize(AppDbContext context)
        {
            context.Database.EnsureDeleted();
            context.Database.EnsureCreated();
            GetClientes().ForEach(p => context.Clientes.Add(p));
            GetCategorias().ForEach(p => context.Categorias.Add(p));
            GetDistritos().ForEach(p => context.Distritos.Add(p));
            GetFeiras().ForEach(p => context.Feiras.Add(p));
            GetVendedores().ForEach(p => context.Vendedores.Add(p));
            GetVendedoresFeiras().ForEach(p => context.VendedoresFeiras.Add(p));
            GetProdutos().ForEach(p => context.Produtos.Add(p));
            context.SaveChanges();
        }

        private static List<Cliente> GetClientes()
        {
            var clientes = new List<Cliente>
            {
                new Cliente {
                    nome = "José Fonte",
                    email = "jsfonte",
                    password = "123"
                }
            };
            return clientes;
        }

        private static List<Categoria> GetCategorias()
        {
            var categorias = new List<Categoria>
            {
                new Categoria // 1
                {
                    nomeCategoria = "Frutas e Legumes"
                },
                new Categoria // 2
                {
                    nomeCategoria = "Sapatos"
                },
                new Categoria // 3
                {
                    nomeCategoria = "Peixe"
                },
                new Categoria // 4
                {
                    nomeCategoria = "Flores"
                },
                new Categoria // 5
                {
                    nomeCategoria = "Pastelaria"
                },
                new Categoria // 6
                {
                    nomeCategoria = "Tecidos"
                },
                new Categoria // 7
                {
                    nomeCategoria = "Jogos"
                },
                new Categoria // 8
                {
                    nomeCategoria = "Música"
                },
                new Categoria // 9
                {
                    nomeCategoria = "Decoração"
                },
                new Categoria // 10
                {
                    nomeCategoria = "Roupa"
                },
                new Categoria // 11
                {
                    nomeCategoria = "Acessórios"
                },
                new Categoria // 12
                {
                    nomeCategoria = "Livros"
                }
            };

            return categorias;
        }

        private static List<Distrito> GetDistritos()
        {
            var distritos = new List<Distrito>
            {
                new Distrito
                {
                    nome = "Braga"
                },
                new Distrito
                {
                    nome = "Porto"
                },
                new Distrito
                {
                    nome = "Aveiro"
                },
                new Distrito
                {
                    nome = "Viseu"
                },
                new Distrito
                {
                    nome = "Guarda"
                },
                new Distrito
                {
                    nome = "Castelo Branco"
                },
                new Distrito
                {
                    nome = "Santarém"
                },
                new Distrito
                {
                    nome = "Vila Real"
                },
                new Distrito
                {
                    nome = "Coimbra"
                },
                new Distrito
                {
                    nome = "Leiria"
                },
                new Distrito
                {
                    nome = "Faro"
                },
                new Distrito
                {
                    nome = "Beja"
                },
            };
            return distritos;
        }

        private static List<Feira> GetFeiras()
        {
            var feiras = new List<Feira>
            {
                new Feira // Feira das Favas
                {
                    nomeDistrito = "Braga",
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(),
                    nome = "Mercado de Braga",
                    idCategoria = 2,
                    temporaria = false
                },

                new Feira {
                    nomeDistrito = "Braga",
                    nome = "Feira de Barcelos",
                    idCategoria = 1,

                    temporaria = false
                },
                new Feira {
                    nomeDistrito = "Braga",
                    nome = "Feira de Famalicao",
                    idCategoria = 3,
                    temporaria = false
                },
                new Feira {
                    nomeDistrito = "Braga",
                    nome = "Feira de Esposende",
                    idCategoria = 4,
                    temporaria = false
                },
                new Feira {
                    nomeDistrito = "Braga",
                    nome = "Feira de Fafe",
                    idCategoria = 5,
                    temporaria = false
                },
                new Feira {
                    nomeDistrito = "Braga",
                    nome = "Feira de Guimaraes",
                    idCategoria = 6,
                    temporaria = false
                },
                new Feira {
                    nomeDistrito = "Braga",
                    nome = "Feira de Vizela",
                    idCategoria = 7,
                    temporaria = false
                },
                new Feira {
                    nomeDistrito = "Vila Real",
                    nome = "Feira de Chaves",
                    idCategoria = 8,
                    temporaria = false
                },
                new Feira {
                    nomeDistrito = "Braga",
                    nome = "Feira de Cabeceira de Basto",
                    idCategoria = 9,
                    temporaria = false
                },
                new Feira {
                    nomeDistrito = "Braga",
                    nome = "Feira de Vieira do Minho",
                    idCategoria = 10,
                    temporaria = false
                },
                new Feira {
                    nomeDistrito = "Braga",
                    nome = "Feira de Amares",
                    idCategoria = 11,
                    temporaria = false
                },
                new Feira {
                    nomeDistrito = "Braga",
                    nome = "Feira de Terras do Bouro",
                    idCategoria = 1,
                    temporaria = false
                }
            };

            return feiras;
        }

        private static List<Vendedor> GetVendedores()
        {
            var vendedores = new List<Vendedor>
            {
                new Vendedor
                {
                    nome = "Cabeleleila Leila",
                },
                new Vendedor
                {
                    nome = "Dona Alzira"
                },
                new Vendedor
                {
                    nome = "Maria das Meias"
                },
                new Vendedor
                {
                    nome = "José Feijoas"
                },
                new Vendedor
                {
                    nome = "João Cabrita"
                },
                new Vendedor
                {
                    nome = "Lucília Meira"
                },
                new Vendedor
                {
                    nome = "Joaquim Gomes"
                },
                new Vendedor
                {
                    nome = "Josefa Quintela"
                },
                new Vendedor
                {
                    nome = "Artur Faria"
                },
                new Vendedor
                {
                    nome = "João Batista"
                },
                new Vendedor
                {
                    nome = "Jorge Moreno"
                },
                new Vendedor
                {
                    nome = "Amélia Horta"
                },
                new Vendedor
                {
                    nome = "Ana Rosa"
                },
                new Vendedor
                {
                    nome = "Ruben Vaz"
                },
                new Vendedor
                {
                    nome = "Graça Teles"
                },
                new Vendedor
                {
                    nome = "Miguel Santos"
                }

                // João Cabrita (Jogos), Lucília Meira (Roupa), Joaquim Gomes (Decoração), Josefa Quintela (Livros), Artur Faria (Decoração), João Batista (Frutas e Legumes), Jorge Moreno (Acessórios), Amélia Horta (Sapatos), Ana Rosa (Frutas e Legumes), Ruben Vaz (Acessórios), Graça Teles (Frutas e Legumes), Miguel Santos (Tecidos)

            };

            return vendedores;
        }

        private static List<VendedorFeira> GetVendedoresFeiras()
        {
            var vendedores_feiras = new List<VendedorFeira>
            {
                new VendedorFeira
                {
                    idVendedor = 1,
                    idFeira = 4
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                },
                new VendedorFeira
                {
                    idVendedor = 2,
                    idFeira = 8
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                },
                new VendedorFeira
                {
                    idVendedor = 3,
                    idFeira = 6
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                },
                new VendedorFeira
                {
                    idVendedor = 4,
                    idFeira = 2
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                },
                new VendedorFeira
                {
                    idVendedor = 5,
                    idFeira = 2
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                },
                new VendedorFeira
                {
                    idVendedor = 6,
                    idFeira = 2
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                },
                new VendedorFeira
                {
                    idVendedor = 7,
                    idFeira = 2
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                },
                new VendedorFeira
                {
                    idVendedor = 8,
                    idFeira = 2,
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                },
                new VendedorFeira
                {
                    idVendedor = 9,
                    idFeira = 2
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                },
                new VendedorFeira
                {
                    idVendedor = 10,
                    idFeira = 2
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                },
                new VendedorFeira
                {
                    idVendedor = 11,
                    idFeira = 2
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                },
                new VendedorFeira
                {
                    idVendedor = 12,
                    idFeira = 2
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                },
                new VendedorFeira
                {
                    idVendedor = 13,
                    idFeira = 2
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                },
                new VendedorFeira
                {
                    idVendedor = 14,
                    idFeira = 2
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                },
                new VendedorFeira
                {
                    idVendedor = 15,
                    idFeira = 2
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                },
                new VendedorFeira
                {
                    idVendedor = 16,
                    idFeira = 2
                    //horaFuncionamentoPresencial_Inicio = new DateTime(8, 0),
                    //horaFuncionamentoPresencial_Fim = new DateTime(8, 0)
                }
            };

            return vendedores_feiras;
        }

        private static List<Produto> GetProdutos()
        {
            var produtos = new List<Produto> {
                new Produto
                {
                    nome = "Favas",
                    precoUnidade = 0.5,
                    idVendedor = 3,
                    idCategoria = 1
                },
                new Produto
                {
                    nome = "Meias das Marias das Meias",
                    precoUnidade = 1,
                    idVendedor = 3,
                    idCategoria = 10,
                },
                new Produto
                {
                    nome = "Sapato Branco",
                    precoUnidade = 12,
                    idVendedor = 11,
                    idCategoria = 10,
                },
                new Produto
                {
                    nome = "Meias das Marias das Meias",
                    precoUnidade = 1,
                    idVendedor = 9,
                    idCategoria = 10,
                },
                new Produto
                {
                    nome = "Feijoas",
                    precoUnidade = 5.99,
                    idVendedor = 9,
                    idCategoria = 1
                },
                new Produto
                {
                    nome = "Batata Doce",
                    precoUnidade = 1.79,
                    idVendedor = 9,
                    idCategoria = 1
                },
                new Produto
                {
                    nome = "Alho Francês",
                    precoUnidade = 1.99,
                    idVendedor = 9,
                    idCategoria = 1
                },
                new Produto
                {
                    nome = "Ameixa Preta",
                    precoUnidade = 2.89,
                    idVendedor = 9,
                    idCategoria = 1
                },
                new Produto
                {
                    nome = "Cenoura",
                    precoUnidade = 0.99,
                    idVendedor = 9,
                    idCategoria = 1
                },
                new Produto
                {
                    nome = "Alface",
                    precoUnidade = 2.19,
                    idVendedor = 9,
                    idCategoria = 1
                },
                new Produto
                {
                    nome = "Cebola Branca",
                    precoUnidade = 1.45,
                    idVendedor = 9,
                    idCategoria = 1
                },
                new Produto
                {
                    nome = "Cebola Roxa",
                    precoUnidade = 1.99,
                    idVendedor = 9,
                    idCategoria = 1
                },
                new Produto
                {
                    nome = "Maçã Gala",
                    precoUnidade = 2.19,
                    idVendedor = 9,
                    idCategoria = 1
                },
                new Produto
                {
                    nome = "Pêra Rocha",
                    precoUnidade = 2.19,
                    idVendedor = 9,
                    idCategoria = 1
                },
                new Produto
                {
                    nome = "Laranja",
                    precoUnidade = 1.39,
                    idVendedor = 9,
                    idCategoria = 1
                },
                new Produto
                {
                    nome = "Clementina",
                    precoUnidade = 2.39,
                    idVendedor = 9,
                    idCategoria = 1
                }
                // Feijoas
                // unhas cabelos hitratação e unhas
            };
            // Produtos, João Batista
            // Feijoas, Batata Doce, Alho Francês, Ameixa Preta, Cenoura, Alface, Cebola Branca, Cebola Roxa, Maçã Gala, Pêra Rocha, Laranja, Clementina

            return produtos;
        }
    }
}
