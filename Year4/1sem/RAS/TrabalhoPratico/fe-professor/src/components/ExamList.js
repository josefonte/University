import React , { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import {
  Button,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  AppBar,
  Toolbar,
} from "@mui/material";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import "./css/ExamList.css";

const maxWidth = window.innerWidth * 0.7;
//const data = [
//  {
//    _id: "1",
//    nome: "Teste de Ras",
//    descricao: "Teste de escolhas Mutiplas de 10 questões",
//    data: "10/12/2020",
//    tags: ["Done"],
//  },
//  {
//    _id: "2",
//    nome: "Teste de CP",
//    descricao: "Teste de escolhas Mutiplas de 10 questões",
//    data: "13/12/2020",
//    tags: ["To be corrected"],
//  },
//];
//


const ExamList = (props) => {

  const [data, setExamData] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(`http://localhost:8010/api/gestao/gestprovas/${props.idDocente}`);
        const dados = await response.json();
        setExamData(dados);
      } catch (error) {
        console.error("Erro ao obter a lista de exames:", error);
      }
    };

    fetchData();
  }, []); 


  const format = (value, rowData) => {
    if (value === "Done") {
      return <span style={{ color: "green" }}>Done</span>;
    } else {
      return <span style={{ color: "orange" }}>value</span>;
    }
  };

  const withContrastText = ({ theme, children }) => {
    const contrastText = theme.palette[children.color].contrastText;
    return (
      <Chip color={children.color} contrastText={contrastText}>
        {children.children}
      </Chip>
    );
  };
  const theme = createTheme();
  const columns = [
    {
      id: "_id",
      label: "ID",
      format: (value, record) => (
        <Link to={`/choice-page/${value}`} className="teste-nome">
        <p
          style={{
            margin: "0 0 0 0",
            maxWidth: "600px",
            textOverflow: "ellipsis",
            overflow: "hidden",
            whiteSpace: "nowrap",
          }}
        >
          {value}
        </p>
      </Link>
    ),
    },
    {
      id: "nome",
      label: "Nome",
      format: (value, record) => (
          <p
            style={{
              margin: "0 0 0 0",
              maxWidth: "600px",
              textOverflow: "ellipsis",
              overflow: "hidden",
              whiteSpace: "nowrap",
            }}
          >
            {value}
          </p>
      ),
    },
    {
      id: "data",
      label: "Data",
    },
    {
      id: "status",
      label: "Status",
      format: (value) => {
        const textColor = (value || "").includes("Done") ? "green" : "orange";

        return (
          <p
            style={{
              margin: "0 0 0 0",
              maxWidth: "600px",
              textOverflow: "ellipsis",
              overflow: "hidden",
              whiteSpace: "nowrap",
              color: textColor,
            }}
          >
            {value}
          </p>
        );
      },
    },
  ];

  return (
    <div
      className="table"
      style={{ textAlign: "center", flexDirection: "column" }}
    >
      <Typography variant="h5" className="title">
        Lista de Provas
      </Typography>
      <ThemeProvider theme={theme}>
        <TableContainer
          component={Paper}
          style={{ padding: "20px 2%", maxWidth }}
          className="table-container"
        >
          <Table>
            <TableHead className="table-header">
              <TableRow>
                {columns.map((column) => (
                  <TableCell key={column.id}>{column.label}</TableCell>
                ))}
              </TableRow>
            </TableHead>
            <TableBody>
              {data.map((row) => (
                <TableRow key={row.key}>
                  {columns.map((column) => (
                    <TableCell key={column.id}>
                      {column.format
                        ? column.format(row[column.id])
                        : row[column.id]}
                    </TableCell>
                  ))}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </ThemeProvider>

      <Link to="/create-exam" style={{ cursor: "pointer" }}>
        <Button
          variant="contained"
          color="primary"
          style={{ margin: "10px 0" }}
        >
          Criar Prova
        </Button>
      </Link>
    </div>
  );
};

export default ExamList;
