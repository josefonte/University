import React, { useState, useEffect } from "react";
import { ProfileOutlined, ArrowLeftOutlined } from "@ant-design/icons";
import ConsultaQuestao from "../components/consultaquestao";
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
  Checkbox,
} from "antd";
const { Header, Content } = Layout;
const CheckboxGroup = Checkbox.Group;

/*const exam = {
  nome: "Teste de Ras",
  classificacao_total: "14/20",
  questions: [
    {
      enunciado:
        "Lorem ipsum dolor sit amet consectetur. Ultricies consequat massa platea amet ornare. Fermentum in ornare ac velit ut leo risus diam risus. Habitant ut vitae morbi libero purus facilisi velit ac mi. Diam nunc eu tempus vel pretium. Quis odio risus eget commodo maecenas. Lacus praesent at tellus amet sed sit velit. Faucibus libero semper viverra sit. ",
      cotação_total: "3",
      cotação_resposta: "1.5",
      respostas: [
        {
          selected: true,
          content: "Lorem ipsum dolor sit amet consectetur",
          correct: true,
        },
        {
          selected: true,
          content: "Lorem ipsum asd    sadasd sectetur",
          correct: false,
        },
        {
          selected: false,
          content:
            "Lorem ipsum dolasd a sd asd asd rLorem ipsum asd    sadasd secteturLorem ipsum asd    sadasd secteturLorem ipsum asd    sadasd sectetur",
          correct: true,
        },
        {
          selected: false,
          content: "Lorem ipsum dolor sit amet consectetur",
          correct: false,
        },
      ],
    },
    {
      title: "principios de interfaces",
      enunciado:
        "Lorem ipsum asdasjndjsan amet ornare. Fermentum in ornare ac velit ut leo risus diam risus. Habitant ut vitae morbi libero purus facilisi velit ac mi. Diam nunc eu tempus vel pretium. Quis odio risus eget commodo maecenas. Lacus praesent at tellus amet sed sit velit. Faucibus libero semper viverra sit. ",
      cotação_total: "3",
      cotação_resposta: "0",
      respostas: [
        {
          selected: true,
          content: "Lorem ipsuasf nsectetur",
          correct: false,
        },
        {
          selected: true,
          content: "Lorem asd sit amet consectetur",
          correct: false,
        },
        {
          selected: false,
          content: "Lorem asd consectetur",
          correct: true,
        },
        {
          selected: false,
          content: "Lorem ipsumasdas met consectetur",
          correct: true,
        },
      ],
    },
    {
      title: "principios de requisitos",
      enunciado:
        "Lorem ipsum asdasjndjsan amet ornare. Fermentum in ornare ac velit ut leo risus diam risus. Habitant ut vitae morbi libero purus facilisi velit ac mi. Diam nunc eu tempus vel pretium. Quis odio risus eget commodo maecenas. Lacus praesent at tellus amet sed sit velit. Faucibus libero semper viverra sit. ",
      cotação_total: "3",
      cotação_resposta: "3",
      respostas: [
        {
          selected: true,
          content: "Lorem ipsuasf nsectetur",
          correct: true,
        },
        {
          selected: true,
          content: "Lorem asd sit amet consectetur",
          correct: true,
        },
        {
          selected: false,
          content: "Lorem asd consectetur",
          correct: false,
        },
        {
          selected: false,
          content: "Lorem ipsumasdas met consectetur",
          correct: false,
        },
      ],
    },
    {
      title: "principios de asndasndj",
      enunciado:
        "Lorem ipsum asdasjndjsan amet ornare. Fermentum in ornare ac velit ut leo risus diam risus. Habitant ut vitae morbi libero purus facilisi velit ac mi. Diam nunc eu tempus vel pretium. Quis odio risus eget commodo maecenas. Lacus praesent at tellus amet sed sit velit. Faucibus libero semper viverra sit. ",
      cotação_total: "3",
      cotação_resposta: "0",
      respostas: [
        {
          selected: true,
          content: "Lorem ipsuasf nsectetur",
          correct: false,
        },
        {
          selected: true,
          content: "Lorem asd sit amet consectetur",
          correct: false,
        },
        {
          selected: false,
          content: "Lorem asd consectetur",
          correct: true,
        },
        {
          selected: false,
          content: "Lorem ipsumasdas met consectetur",
          correct: true,
        },
      ],
    },
  ],
};
*/

function AppConsultarProva(props) {
  // route get the exam from its id
  const { id } = useParams();

  const [exam, setExam] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true)
        const response = await fetch(
          `http://localhost:8010/api/correcaoconsulta/alunoready/${id}/${props.idAluno}`
        );
        const dados = await response.json();
        setExam(dados);
        setLoading(false)
        console.log("Dados", dados);
      } catch (error) {
        console.error("Erro ao obter o exame:", error);
      }
    };

    fetchData();
  }, []);

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
      >{!loading && (
  <>
    <Breadcrumb
      items={[
        {
          href: "/provas-concluidas",
          title: (
            <>
              <ProfileOutlined />
              <span>Provas Concluídas</span>
            </>
          ),
        },
        {
          title: exam.nome,
        },
      ]}
    />
    <Row>
      <Col span={24} style={{ marginTop: "2%" }}>
        <a href="/provas-concluidas">
          <ArrowLeftOutlined /> Voltar
        </a>
      </Col>
    </Row>
    <Row align="middle">
      <Col span={18}>
        <h1>{exam.nome}</h1>
      </Col>
      <Col span={6}>
        <span style={{ fontWeight: "bold" }}>Classificação Total</span>
        &nbsp;: &nbsp;{exam.classificacao_total}
      </Col>
    </Row>
    <Divider style={{ margin: "1px 0px 10px 0px" }} />
    {exam.questoes.map((questao, index) => (
      <ConsultaQuestao key={index} questao={questao} id={index + 1} />
    ))}
  </>
)}

        
      </Content>
    </Layout>
  );
}

export default AppConsultarProva;
