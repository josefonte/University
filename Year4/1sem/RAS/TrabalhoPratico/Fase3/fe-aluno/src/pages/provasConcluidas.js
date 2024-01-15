import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { AppHeader } from "../components/AppHeader";
import { Link } from "react-router-dom";

import { Layout, Table, Tag, theme } from "antd";
const { Header, Content } = Layout;

const data = [
  {
    key: "1",
    nome: "Teste de Ras",
    descricao: "Teste de escolhas Mutiplas de 10 questões",
    data: "10/12/2020",
    tags: ["Done"],
    classificacao: "A+",
  },
  {
    key: "2",
    nome: "Teste de Ras",
    descricao:
      "Teste de escolhas Mutiplas de 10 questões descriçao muito muito muito muito muito grannnnnnnnde muitomuito",
    data: "12/12/2020",
    tags: ["Done"],
    classificacao: "14.3/20",
  },
  {
    key: "3",
    nome: "Teste de Ras",
    descricao: "Teste de escolhas Mutiplas de 10 questões",
    data: "13/12/2020",
    tags: ["Done"],
    classificacao: "15/20",
  },
  {
    key: "4",
    nome: "Teste de Ras",
    descricao: "Teste de escolhas Mutiplas de 10 questões",
    data: "13/12/2020",
    tags: ["Finished"],
    classificacao: "",
  },
];

function AppProvasConcluidas(props) {
  const [listExams, setListExams] = useState([]);
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true)
        const response = await fetch(
          `http://localhost:8010/api/correcaoconsulta/alunoready/1`
        );
        const dados = await response.json();
        setListExams(dados);
        setLoading(false)
        console.log("DASASDOOS",dados);
        console.log("LISTa", listExams)
      } catch (error) {
        console.error("Erro ao obter o exame:", error);
      }
    };

    fetchData();
  }, []);
  const {
    token: { colorBgContainer },
  } = theme.useToken();

  const navigate = useNavigate();

  const handleStartExam = (examId) => {
    navigate(`/provas-concluidas/${examId}`);
  };
  const columns = [
    {
      title: "Nome",
      dataIndex: "nome",
      key: "nome",
      render: (_, record) => (
          <a onClick={() => handleStartExam(record.idProva)}>
            {record.nome}
          </a>
      )
    },
    {
      title: "Data",
      dataIndex: "data",
      key: "data",
    },
    {
      title: "Classificação",
      dataIndex: "classificação_total",
      key: "classificação_total",
    },
  ];

  return (
    <Layout>
      <Header
        style={{
          background: colorBgContainer,
        }}
      >
        <AppHeader />
      </Header>

      <Content
        style={{
          padding: "20px 10%",
          minHeight: "calc(100vh - 64px)",
        }}
      >
        <h3 style={{ paddingLeft: "10px" }}>Provas Concluídas</h3>
       
        <Table loading={loading} columns={columns} dataSource={listExams} />
      </Content>
    </Layout>
  );
}

export default AppProvasConcluidas;
