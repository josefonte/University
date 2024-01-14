import React, { useState, useEffect } from "react";
import { ArrowLeftOutlined } from "@ant-design/icons";

import { useNavigate } from "react-router-dom";
import { useParams } from "react-router-dom";

import axios from 'axios';

import {
  Layout,
  Modal,
  Row,
  Col,
  Steps,
  Button,
  theme,
  Divider,
  Checkbox,
  Space,
  Statistic,
} from "antd";
const { Header, Content } = Layout;

const { Countdown } = Statistic;

const createNavItems = (questoes, step) => {
  return questoes.map((_, index) => ({
    status: index === step ? "progress" : index < step ? "finish" : "wait",
  }));
};

//const getQuestoes = () => {
//  return questoes;
//};

function conversorDuracao(hours, minutes) {
  const totalMilliseconds = hours * 60 * 60 * 1000 + minutes * 60 * 1000;
  return totalMilliseconds;
}

function AppResolverProva(props) {
  const {
    token: { colorBgContainer },
  } = theme.useToken();

  const { idProva } = useParams();
  const [questoesProva, setQuestoesProva] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(
          `http://localhost:8010/api/gestao/gestprovas/${props.idAluno}?id=${idProva}`
        );
        const dados = await response.json();
        setQuestoesProva(dados.questoes);
        setNavItems(createNavItems(dados.questoes, current));
        setLoading(false); // Set loading to false when data is fetched
        console.log("AQUI2")
        console.log(dados.questoes);
      } catch (error) {
        console.error("Erro ao obter o exame:", error);
        setLoading(false); // Set loading to false in case of an error
      }
    };
    fetchData();
  }, [props.idAluno, idProva]); // Include props.idAluno and idProva in the dependency array

  const [duracao] = useState(Date.now() + conversorDuracao(1, 30));
  const [NavItems, setNavItems] = useState(createNavItems(questoesProva));
  const [current, setCurrent] = useState(0);
  const [respostas, setRespostas] = useState([]); // [ {id: 1, resposta: 1}, {id: 2, resposta: 2}
  //const [selectedOptions, setSelectedOptions] = useState([]); // [ {id: 1, resposta: 1}, {id: 2, resposta: 2}
const [selectedOptions, setSelectedOptions] = useState([]);
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();

  const onFinish = () => {
    handleOk();
  };

  const handleOk = () => {
    respostas.push({
  questionId: questoesProva[current]._id,
  selectedIndexes: selectedOptions
    //.filter(([, isSelected]) => isSelected)
    //.map(([id]) => id),
});
    console.log("depois "+JSON.stringify(respostas))
    submitProva();
    //submeter respostas
    //informar que a prova foi preenchida
    setTimeout(() => {
      navigate("/provas-ativas");
    }, 1000);
  };

  const handleCancel = () => {
    setOpen(false);
  };

  const nextQuestion = () => {
    respostas.push({
  questionId: questoesProva[current]._id,
    selectedIndexes: selectedOptions
  //selectedIndexes: Object.entries(selectedOptions)
    //.filter(([, isSelected]) => isSelected)
    //.map(([id]) => id),
});
    console.log(respostas);
    setSelectedOptions([]);

    if (current + 1 < questoesProva.length) {
      setCurrent(current + 1);
      setNavItems(createNavItems(questoesProva, current + 1));
    } else {
      Modal.confirm({
        title: "Finalizar a Prova",
        content: "Ao confirmar a prova será finalizada e submetida.",
        footer: (_, { OkBtn, CancelBtn }) => (
          <>
            <CancelBtn />
            <OkBtn />
          </>
        ),
      });
    }
    //if current == questoes.length => submit respostas
  };
	
/*
  const handleCheckboxChange = (index) => {
  	console.log("estou aqui ")
    const updatedSelectedOptions = [...selectedOptions];
    updatedSelectedOptions[index] = !updatedSelectedOptions[index];
    setSelectedOptions(updatedSelectedOptions);
  	console.log(selectedOptions[index] )
  };
*/

const handleCheckboxChange = (optionId) => {
  setSelectedOptions((prevSelectedOptions) => {
    if (prevSelectedOptions.includes(optionId)) {
      return prevSelectedOptions.filter((id) => id !== optionId);
    } else {
      return [...prevSelectedOptions, optionId];
    }
  });
};

  const submitProva = () => {
    console.log("submit prova");
    console.log(respostas);
    var obj={"_id":idProva,"_id_aluno":props.idAluno,"respostas":respostas}
    	//respostas.forEach(item => {
  	//obj.respostas.p({"questionId":item.questionId,"selectedIndexes":item.selectedIndexes});
	//});

    axios.post("http://localhost:8010/api/realizacao/save/${idProva}",obj)
  };

  return (
    <Layout>
      <Header
        style={{
          background: colorBgContainer,
        }}
      >
        <Row align={"middle"}>
          <Col span={4}>
            <a onClick={() => setOpen(true)}>
              <ArrowLeftOutlined /> Sair
            </a>
          </Col>
          <Col span={16}>
            <Steps
              type="navigation"
              size="small"
              current={current}
              className="site-navigation-steps"
              items={NavItems}
            />
          </Col>
          <Col span={4} align={"middle"}>
            <Countdown value={duracao} onFinish={onFinish} />
          </Col>
        </Row>
      </Header>

      <Content
        style={{
          padding: "20px 20%",
          minHeight: "calc(100vh - 64px)",
        }}
      >
        <Row>
          <h2>Pergunta {current + 1}</h2>
        </Row>
        <Divider style={{ margin: "1px 0px 10px 0px" }} />
        {!loading ? (
          <>
            <Row>
              <span>{questoesProva[current].enunciado}</span>
            </Row>
            <Row>
              <h3>Respostas</h3>
            </Row>
            <Row>
              <Space
                direction="vertical"
                size="small"
                style={{
                  display: "flex",
                }}
              >
                {questoesProva[current].options.map((opção, index) => (
                  <Checkbox
                    key={opção._id}
                    onChange={() => handleCheckboxChange(opção._id)}
                    checked={selectedOptions.includes(opção._id)}
                  >
                    {opção.texto}
                  </Checkbox>
                ))}
              </Space>
            </Row>
          </>
        ) : null}

        <Row justify={"end"}>
          {current < questoesProva.length - 1 ? (
            <Button onClick={nextQuestion} type="primary">
              Guardar e Prosseguir
            </Button>
          ) : (
            <Button onClick={() => setOpen(true)} type="primary">
              Guardar e Finalizar Prova
            </Button>
          )}
        </Row>
        <Modal
          open={open}
          title="Finalizar a Prova"
          content="Ao confirmar a prova será finalizada e submetida."
          onOk={handleOk}
          onCancel={handleCancel}
          footer={(_, { OkBtn, CancelBtn }) => (
            <>
              <CancelBtn />
              <OkBtn />
            </>
          )}
        >
          <p>Ao confirmar a prova será finalizada e submetida.</p>
        </Modal>
      </Content>
    </Layout>
  );
}

export default AppResolverProva;
