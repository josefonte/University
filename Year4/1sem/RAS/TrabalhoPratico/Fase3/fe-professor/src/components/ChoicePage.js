import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { Link } from "react-router-dom";
import { Button, Typography, Paper } from "@mui/material";
import { Box, List, ListItem, ListItemText, Checkbox } from "@mui/material";
import "./css/ChoicePage.css";

const ChoicePage = (props) => {
  //const optionsSchema1 = [
  //  {
  //    _id: "a",
  //    texto: "dao afonso henriques",
  //    cotacao: "3",
  //    resolucao: "certa",
  //  },
  //  {
  //    _id: "b",
  //    texto: "manuel I",
  //    cotacao: "0",
  //    resolucao: "errada",
  //  },
  //  {
  //    _id: "c",
  //    texto: "dao sancho",
  //    cotacao: "0",
  //    resolucao: "errada",
  //  },
  //];

  //const optionsSchema2 = [
  //  {
  //    _id: "a",
  //    texto: "nop",
  //    cotacao: "-1",
  //    resolucao: "errada",
  //  },
  //  {
  //    _id: "b",
  //    texto: "siiiiiiiiiiiiiim",
  //    cotacao: "4",
  //    resolucao: "certa",
  //  },
  //  {
  //    _id: "c",
  //    texto: "meh",
  //    cotacao: "0",
  //    resolucao: "errada",
  //  },
  //  {
  //    _id: "d",
  //    texto: "meh 2.0",
  //    cotacao: "0",
  //    resolucao: "errada",
  //  },
  //];

  //const optionsSchema3 = [
  //  {
  //    _id: "a",
  //    texto: "nop",
  //    cotacao: "-1",
  //    resolucao: "errada",
  //  },
  //  {
  //    _id: "b",
  //    texto: "siiiiiiiiiiiiiim",
  //    cotacao: "4",
  //    resolucao: "errada",
  //  },
  //  {
  //    _id: "c",
  //    texto: "meh",
  //    cotacao: "0",
  //    resolucao: "errada",
  //  },
  //  {
  //    _id: "d",
  //    texto: "meh 2.0",
  //    cotacao: "4",
  //    resolucao: "certa",
  //  },
  //];

  //const questoes = [
  //  {
  //    _id: "1",
  //    enunciado: "primeiro rei de portugal",
  //    imagem: "",
  //    cotacaoTotal: "3",
  //    tipo_Questao: "EC",
  //    options: [optionsSchema1],
  //  },
  //  {
  //    _id: "2",
  //    enunciado: "pergunta muuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuito grande",
  //    imagem: "",
  //    cotacaoTotal: "4",
  //    tipo_Questao: "EC",
  //    options: [optionsSchema2],
  //  },
  //  {
  //    _id: "3",
  //    enunciado: "pergunta ",
  //    imagem: "",
  //    cotacaoTotal: "4",
  //    tipo_Questao: "EC",
  //    options: [optionsSchema3],
  //  },
  //];

  const { idProva } = useParams(); // Está aqui o id da prova
  const [checked, setChecked] = useState(false);

  const [exame, setExamData] = useState({}); // Está aqui a prova

  const [questoes, setQuestoes] = useState([]);

  // GET das informações da prova
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(
          `http://localhost:8010/api/gestao/gestprovas/${props.idDocente}?id=${idProva}`
        );
        const dados = await response.json();
        setExamData(dados);
        setQuestoes(dados.questoes)
        console.log(dados.questoes)
      } catch (error) {
        console.error("Erro ao obter o exame:", error);
      }
    };

    fetchData();
  }, []);

  return (
    <div
      className="main-page"
      style={{
        textAlign: "center",
        flexDirection: "column",
        alignItems: "center",
      }}
    >
      {/* Mostrar algumas informações da prova
      <p>ID da Prova: {exame._id}</p>
      <p>Nome da Prova: {exame.nome}</p> */}

      <Paper className="paper" elevation={4}>
        <Typography variant="subtitle1" style={{ fontWeight: "bold" }}>
          ID da Prova: {exame._id}
        </Typography>
        <Typography variant="subtitle1" style={{ fontWeight: "bold" }}>
          Nome da Prova: {exame.nome}
        </Typography>
      </Paper>

      <div className="button-container">
        <Link to="/correct-exam" style={{ cursor: "pointer" }}>
          <Button
            variant="contained"
            color="primary"
            style={{ margin: "10px 0" }}
          >
            Corrigir Prova
          </Button>
        </Link>
      </div>
      
      {/* Lista de questões */}
      <Typography variant="h4" gutterBottom>
        Questões Da Prova
      </Typography>

      <div className="questions-container">
        <List style={{ flexDirection: "column" }}>
          {questoes.map((questao) => (
            <React.Fragment key={questao._id}>
              <Typography variant="h6" className="enunciado">
                {questao.enunciado}
              </Typography>
              {questao.options.map((opcao) => (
              <div className="answers">
                <ListItem key={opcao._id}>
                  <Checkbox
                    checked={opcao.cotacao > 0}
                    onChange={(event) => setChecked(event.target.checked)}                    
                    />
                  <ListItemText>
                    {opcao.texto}
                  </ListItemText>
                </ListItem>
              </div>
              ))}
            </React.Fragment>
          ))}
        </List>
      </div>
    </div>
  );
};

export default ChoicePage;
