import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { AppHeader } from "../components/AppHeader";

import { PlayCircleTwoTone } from "@ant-design/icons";
import { Layout, Space, Table, theme } from "antd";
const { Header, Content } = Layout;

function AppProvasAtivas(props) {
  const [data, setExamData] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(
          `http://localhost:8010/api/gestao/gestprovas/${props.idAluno}`
        );
        const dados = await response.json();
        setExamData(dados);
        console.log(dados);
      } catch (error) {
        console.error("Erro ao obter o exame:", error);
      }
    };

    fetchData();
  }, []);

  const columns = [
    {
      title: "Nome",
      dataIndex: "nome",
      key: "nome",
      render: (text) => <span>{text}</span>,
    },
    {
      title: "Data",
      dataIndex: "data",
      key: "data",
    },
    {
      title: "Hora",
      dataIndex: "hora_preferencial",
      key: "hora_preferencial",
    },

    {
      title: "Salas",
      dataIndex: "salas",
      key: "salas",
      render: () => <span>CP1 0.08</span>,
    },

    {
      title: "Ação",
      key: "action",
      render: (_, record) => (
        <Space size="small">
          <a onClick={() => handleStartExam(record._id)}>
            Começar <PlayCircleTwoTone size={"small"} />
          </a>
        </Space>
      ),
    },
  ];

  const navigate = useNavigate();

  const handleStartExam = (examId) => {
    navigate(`/provas-ativas/${examId}`);
  };

  const {
    token: { colorBgContainer },
  } = theme.useToken();

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
        <h3 style={{ paddingLeft: "10px" }}>Provas Ativas</h3>
        <Table columns={columns} dataSource={data} />
      </Content>
    </Layout>
  );
}

export default AppProvasAtivas;
