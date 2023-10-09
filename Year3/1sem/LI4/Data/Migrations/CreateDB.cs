using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace FairthosApp.Data.Migrations
{
    /// <inheritdoc />
    public partial class CreateIdentitySchema : Migration
    {
        /// <inheritdoc />
        // [DbContext(typeof(AppDbContext))]
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Categoria",
                columns: table => new
                {
                    idCategoria = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    nomeCategoria = table.Column<string>(type: "nvarchar(max)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Categoria", x => x.idCategoria);
                });

            migrationBuilder.CreateTable(
                name: "Cliente",
                columns: table => new
                {
                    idCliente = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    email = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    password = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    nome = table.Column<string>(type: "nvarchar(max)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Cliente", x => x.idCliente);
                });

            migrationBuilder.CreateTable(
                name: "Vendedor",
                columns: table => new
                {
                    idVendedor = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    nome = table.Column<string>(type: "nvarchar(max)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Cliente", x => x.idVendedor);
                });

            migrationBuilder.CreateTable(
                name: "EmpresaTransporte",
                columns: table => new
                {
                    idEmpresaTransporte = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    nomeEmpresa = table.Column<string>(type: "nvarchar(max)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_EmpresaTransporte", x => x.idEmpresaTransporte);
                });

            migrationBuilder.CreateTable(
                name: "Distrito",
                columns: table => new
                {
                    nome = table.Column<int>(type: "nvarchar(max)", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Distrito", x => x.nome);
                });


            migrationBuilder.CreateTable(
                name: "Feira",
                columns: table => new
                {
                    idFeira = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    nome = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    descricao = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    //PathFotografia = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    horaFuncionamentoPresencial_Inicio = table.Column<DateTime>(type: "datetime2", nullable: false),
                    horaFuncionamentoPresencial_Fim = table.Column<DateTime>(type: "datetime2", nullable: false),
                    temporario = table.Column<bool>(type: "bit", nullable: false),
                    idCategoria = table.Column<int>(type: "int", nullable: false),
                    nomeDistrito = table.Column<int>(type: "nvarchar(max)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Feira", x => x.idFeira);
                    table.ForeignKey(
                        name: "FK_idCategoria",
                        column: x => x.idCategoria,
                        principalTable: "Categoria",
                        principalColumn: "idCategoria",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_nomeDistrito",
                        column: x => x.nomeDistrito,
                        principalTable: "Distrito",
                        principalColumn: "nome",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "FeiraTemporaria",
                columns: table => new
                {
                    idFeira = table.Column<int>(type: "int", nullable: false),
                    dataInicio = table.Column<DateTime>(type: "datetime2", nullable: false),
                    dataFim = table.Column<DateTime>(type: "datetime2", nullable: false),
                },
                constraints: table =>
                {
                    //table.PrimaryKey("PK_Feira", x => x.idFeiraTemporaria);
                    table.ForeignKey(
                        name: "FK_idFeira",
                        column: x => x.idFeira,
                        principalTable: "Feira",
                        principalColumn: "idFeira",
                        onDelete: ReferentialAction.Cascade);
                });

            

            migrationBuilder.CreateTable(
                name: "Produto",
                columns: table => new
                {
                    idProduto = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    nome = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    precoUnidade = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    idVendedor = table.Column<int>(type: "int", nullable: false),
                    idCategoria = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Produto", x => x.idProduto);
                    table.ForeignKey(
                        name: "FK_idVendedor",
                        column: x => x.idVendedor,
                        principalTable: "Vendedor",
                        principalColumn: "idVendedor");
                    table.ForeignKey(
                        name: "FK_idCategoria",
                        column: x => x.idCategoria,
                        principalTable: "Categoria",
                        principalColumn: "idCategoria");
                });

            migrationBuilder.CreateTable(
                name: "ReservaProduto",
                columns: table => new
                {
                    idReservaProduto = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    quantidade = table.Column<int>(type: "int", nullable: false),
                    idProduto = table.Column<int>(type: "int", nullable: false),
                    idCliente = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ReservaProduto", x => x.idReservaProduto);
                    table.ForeignKey(
                        name: "FK_idProduto",
                        column: x => x.idProduto,
                        principalTable: "Produto",
                        principalColumn: "idProduto");
                    table.ForeignKey(
                        name: "FK_idCliente",
                        column: x => x.idCliente,
                        principalTable: "Cliente",
                        principalColumn: "idCliente");
                });

            migrationBuilder.CreateTable(
                name: "Compra",
                columns: table => new
                {
                    idCompra = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    data_emissao = table.Column<DateTime>(type: "datetime2", nullable: false),
                    precoTotal = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    moradaFaturacao = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    custoTotalEnvio = table.Column<int>(type: "int", nullable: false),
                    metodoPagamento = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    idCliente = table.Column<int>(type: "int", nullable: false),
                    idEmpresaTransporte = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Compra", x => x.idCompra);
                    table.ForeignKey(
                        name: "FK_idCliente",
                        column: x => x.idCliente,
                        principalTable: "Cliente",
                        principalColumn: "idCliente");
                    table.ForeignKey(
                        name: "FK_idEmpresaTransporte",
                        column: x => x.idEmpresaTransporte,
                        principalTable: "EmpresaTransporte",
                        principalColumn: "idEmpresaTransporte");
                });

             migrationBuilder.CreateTable(
                name: "VendedorFeira",
                columns: table => new
                {
                    horaFuncionamentoPresencial_Inicio = table.Column<DateTime>(type: "datetime2", nullable: false),
                    horaFuncionamentoPresencial_Fim = table.Column<DateTime>(type: "datetime2", nullable: false),
                    idVendedor = table.Column<int>(type: "int", nullable: false),
                    idFeira = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.ForeignKey(
                        name: "FK_idVendedor",
                        column: x => x.idVendedor,
                        principalTable: "Vendedor",
                        principalColumn: "idVendedor");
                    table.ForeignKey(
                        name: "FK_idFeira",
                        column: x => x.idFeira,
                        principalTable: "Feira",
                        principalColumn: "idFeira");
            
                });

            migrationBuilder.CreateTable(
                name: "ProdutoCompra",
                columns: table => new
                {
                    quantidade = table.Column<int>(type: "int", nullable: false),
                    valorTotal = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    idVendedor = table.Column<int>(type: "int", nullable: false),
                    idCompra = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.ForeignKey(
                        name: "FK_idProduto",
                        column: x => x.idVendedor,
                        principalTable: "Vendedor",
                        principalColumn: "idVendedor");
                    table.ForeignKey(
                        name: "FK_idCompra",
                        column: x => x.idCompra,
                        principalTable: "Compra",
                        principalColumn: "idCompra");
            
                });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Categoria");

            migrationBuilder.DropTable(
                name: "Cliente");

            migrationBuilder.DropTable(
                name: "Compra");

            migrationBuilder.DropTable(
                name: "Distrito");

            migrationBuilder.DropTable(
                name: "EmpresaTransporte");

            migrationBuilder.DropTable(
                name: "Feira");

            migrationBuilder.DropTable(
                name: "FeiraTemporaria");

            migrationBuilder.DropTable(
                name: "Produto");

            migrationBuilder.DropTable(
                name: "ProdutoCompra");

            migrationBuilder.DropTable(
                name: "ReservaProduto");

            migrationBuilder.DropTable(
                name: "Vendedor");

            migrationBuilder.DropTable(
                name: "VendedorFeira");
        }
    }
}