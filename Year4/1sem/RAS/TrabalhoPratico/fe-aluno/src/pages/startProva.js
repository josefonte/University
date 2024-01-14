import React, { useState, useEffect } from "react";
import { FileTextOutlined } from "@ant-design/icons";
import { Link } from "react-router-dom";

import { AppHeader } from "../components/AppHeader";
import { useParams } from "react-router-dom";
import {
  Layout,
  Breadcrumb,
  theme,
  Col,
  Row,
  Button,
  Flex,
  Divider,
  Space,
} from "antd";
const { Header, Content } = Layout;

const descricao_placeholder =
  "Este exame é configurado no formato de escolha múltipla, sem a opção de revisão ou alteração de respostas após a seleção inicial. Uma vez que os participantes concluem o exame, ele é encerrado automaticamente. As respostas são armazenadas ao longo do exame, sendo efetivadas somente quando o aluno decide sair ou conclui integralmente a avaliação. ";

function getDuration(durationInMinutes) {
  const hours = Math.floor(durationInMinutes / 60);
  const minutes = durationInMinutes % 60;

  return { hours, minutes };
}

function calculateEndTime(initialTime, durationInMinutes) {
  const [initialHours, initialMinutes] = initialTime.split(":").map(Number);

  const totalInitialMinutes = initialHours * 60 + initialMinutes;

  const totalEndMinutes = totalInitialMinutes + durationInMinutes;

  const endHours = Math.floor(totalEndMinutes / 60);
  const endMinutes = totalEndMinutes % 60;

  const formattedEndTime = `${String(endHours).padStart(2, "0")}:${String(
    endMinutes
  ).padStart(2, "0")}`;

  return formattedEndTime;
}

function AppStartProva(props) {
  const { idProva } = useParams();

  const [exam, setExam] = useState({});

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(
          `http://localhost:8010/api/gestao/gestprovas/${props.idAluno}?id=${idProva}`
        );
        const dados = await response.json();
        console.log("aqui")
        setExam(dados);
        console.log("lista " +dados);
      } catch (error) {
        console.error("Erro ao obter o exame:", error);
      }
    };

    fetchData();
  }, []);

  const { hours: dur_hora, minutes: dur_min } = getDuration(exam.duracao);
  
  console.log("hora: "+ JSON.stringify(exam))

  const hora_fim =
    exam.hora_preferencial &&
    calculateEndTime(exam.hora_preferencial, exam.duracao);

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
          padding: "20px 20%",
          minHeight: "calc(100vh - 64px)",
        }}
      >
        <Breadcrumb
          items={[
            {
              href: "/provas-ativas",
              title: (
                <>
                  <FileTextOutlined />
                  <span>Provas Ativas</span>
                </>
              ),
            },
            {
              title: exam.nome,
            },
          ]}
        />
        <Row>
          <Col span={24}>
            <h1>{exam.nome}</h1>
          </Col>
        </Row>
        <Divider style={{ margin: "1px 0px 10px 0px" }} />
        <Row>
          <Col span={24}>
            <span>{descricao_placeholder}</span>
          </Col>
        </Row>
        <Row style={{ marginBottom: "5%" }}>
          <Col span={24}>
            <h3>Informações</h3>
            <Space
              direction="vertical"
              size="small"
              style={{
                display: "flex",
              }}
            >
              <Row>
                <span
                  style={{ fontWeight: "bold", textDecoration: "underline" }}
                >
                  Curso
                </span>
                &nbsp;: &nbsp;{exam.curso}
              </Row>

              <Row>
                <span
                  style={{ fontWeight: "bold", textDecoration: "underline" }}
                >
                  Unidade Curricular
                </span>
                &nbsp;: &nbsp;{exam.uc}
              </Row>
              <Row>
                <span style={{ fontWeight: "bold" }}>
                  1º Semestre | Ano Letivo 2023/2024
                </span>
              </Row>
            </Space>

            <Row style={{ marginTop: "5%", marginBottom: "5%" }}>
              <span style={{ fontWeight: "bold", textDecoration: "underline" }}>
                Início
              </span>
              &nbsp;: &nbsp;{exam.hora_preferencial}&nbsp;| &nbsp;
              <span style={{ fontWeight: "bold", textDecoration: "underline" }}>
                Fim
              </span>
              &nbsp;: &nbsp;{hora_fim}&nbsp;| &nbsp;
              <span style={{ fontWeight: "bold", textDecoration: "underline" }}>
                Duração
              </span>
              &nbsp;: &nbsp;{dur_hora} horas e {dur_min} minutos
            </Row>
          </Col>
        </Row>

        <Row>
          <Col span={8} offset={16}>
            <Flex gap="small" wrap="wrap" justify="flex-end">
              <Button href="/provas-ativas" style={{ color: "gray" }}>
                Voltar
              </Button>
              <Button type="primary">
                <Link to={`/provas-ativas/resolver-prova/${idProva}`}>
                  Começar
                </Link>
              </Button>
            </Flex>
          </Col>
        </Row>
      </Content>
    </Layout>
  );
}

export default AppStartProva;
