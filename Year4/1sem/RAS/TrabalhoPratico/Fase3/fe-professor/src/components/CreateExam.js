import React, { useState, useEffect } from "react";
// import * as pdfjs from 'pdfjs-dist/build/pdf';
// import 'pdfjs-dist/web/pdf_viewer.css';
import { useNavigate } from "react-router-dom";
import { TextField, FormControl, Box, Typography, Paper } from "@mui/material";
import Dialog from "@material-ui/core/Dialog";
import Button from "@material-ui/core/Button";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import { v4 as uuidv4 } from "uuid";

import "./css/createExam.css";
import zIndex from "@mui/material/styles/zIndex";
const CreateExam = (props) => {
  const [ids, setIds] = useState([]);

  {
    /* const handleFileChange = async (event) => {
    const file = event.target.files[0];

    if (file) {
      const reader = new FileReader();

      reader.onload = async (e) => {
        const arrayBuffer = e.target.result;
        const pdf = await pdfjs.getDocument(arrayBuffer).promise;

        const ids = [];

        for (let i = 1; i <= pdf.numPages; i++) {
          const page = await pdf.getPage(i);
          const textContent = await page.getTextContent();
          const text = textContent.items.map((item) => item.str).join('\n');
          const pageIds = text.split('\n').filter((id) => id.trim() !== '');
          ids.push(...pageIds);
        }

        setIds(ids);
      };

      reader.readAsArrayBuffer(file);
    }
  }; */
  }

  const [openDialog, setOpenDialog] = useState(false);

  const [formData, setFormData] = useState({
    _id: uuidv4(),
    cotacao: "",
    uc: "",
    curso: "",
    nome: "",
    id_docente: props.idDocente,
    alunos: [],
    data: "",
    hora_preferencial: "",
    tempo_admissao: "",
    duracao: "",
    acesso_autorizado: [],
    versao: "",
  });

  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      const response = await fetch(
        `http://localhost:8010/api/gestao/criar/${props.idDocente}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(formData),
        }
      );

      if (response.ok) {
        setOpenDialog(true);
      } else {
        console.error("Erro ao enviar o formulário:", response.statusText);
      }
    } catch (error) {
      console.error("Erro ao enviar o formulário:", error.message);
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    // Close the dialog
    setOpenDialog(false);
  };

  const handleCreateQuestions = () => {
    navigate(`/create-questions/${formData._id}`);
    setOpenDialog(false);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const HelperBox = ({ children }) => {
    const [helperVisible, setHelperVisible] = useState(false);

    const handleClick = () => {
      setHelperVisible(!helperVisible);
    };

    return (
      <Box >
        <Button onClick={handleClick} style={{color:"#8b0000", fontWeight: "bold", backgroundColor:"8b0000"}}>?</Button>
        {helperVisible && <Paper sx={{ padding: "16px" , marginRight:"-200px" }}>{children}</Paper>}
      </Box>
    );
  };

  return (
    <>
      <FormControl
        class="form"
        style={{ flexDirection: "column", textAlign: "center" }}
      >
        <Typography variant="h3">Criar Prova</Typography>
        <TextField
          id="titulo"
          classes={{ root: "exam-title" }}
          label="Título"
          name="nome"
          onChange={handleChange}
          // onChange={(e) => setTitulo(e.target.value)}
          variant="filled"
          margin="normal"
        />

        {/*<Typography
          style={{ margin: "10px 0", textAlign: "left" }}>
          Alunos Admitidos
        </Typography>
        <TextField
          type="file"
          onChange={handleFileChange}
        /> 
        <Button variant="contained" color="primary" component="span" style={{width: "150px", height:"28px"}}>
          Upload file
        </Button>
        */}
        <div className="helper-alunos">
          <TextField 
          id="alunos"
          label="Alunos admitidos"
          name="alunos"
          onChange={handleChange}
          // onChange={(e) => setTitulo(e.target.value)}
          variant="filled"
          margin="normal"
          style={{maxWidth:"170px"}}
          />
          <HelperBox>
            <p>O formato deve ser "id1;id2;id3".</p>
          </HelperBox>
        </div>
        
        <TextField
          id="cotacao"
          label="Cotação"
          name="cotacao"
          onChange={handleChange}
          // onChange={(e) => setDuracao(e.target.value)}
          variant="filled"
          margin="normal"
        />
        <TextField
          id="dia"
          label="Dia da Prova"
          name="data"
          onChange={handleChange}
          // onChange={(e) => setDia(e.target.value)}
          type="date"
          variant="filled"
          margin="normal"
        />

        <TextField
          id="uc"
          label="Unidade Curricular"
          name="uc"
          onChange={handleChange}
          // onChange={(e) => setDuracao(e.target.value)}
          variant="filled"
          margin="normal"
        />

        <TextField
          id="curso"
          label="Curso"
          name="curso"
          onChange={handleChange}
          // onChange={(e) => setDuracao(e.target.value)}
          variant="filled"
          margin="normal"
        />

        <TextField
          id="tempo_Admissao"
          name="tempo_admissao"
          label="Tempo de Admissão"
          onChange={handleChange}
          // onChange={(e) => setHora(e.target.value)}
          type="number"
          variant="filled"
          margin="normal"
        />

        <TextField
          id="hora"
          name="hora_preferencial"
          label="Hora da Prova"
          onChange={handleChange}
          // onChange={(e) => setHora(e.target.value)}
          variant="filled"
          margin="normal"
        />

        <TextField
          id="duracao"
          label="Duração"
          name="duracao"
          onChange={handleChange}
          // onChange={(e) => setDuracao(e.target.value)}
          type="number"
          variant="filled"
          margin="normal"
        />

        <TextField
          id="numeroDeVersoes"
          label="Versão"
          name="versao"
          onChange={handleChange}
          // onChange={(e) => setNumeroDeVersoes(e.target.value)}
          type="number"
          variant="filled"
          margin="normal"
        />
        <Box mt={2} textAlign="center">
          <Button variant="contained" color="primary" onClick={handleSubmit}>
            Guardar
          </Button>
        </Box>
      </FormControl>

      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>O seu exame ja tem calendarização</DialogTitle>
        <DialogContent>
          <DialogContentText>Data/Hora do teste. Salas</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog} color="primary">
            Fechar
          </Button>
          <Button onClick={handleCreateQuestions} color="primary">
            Criar Questões
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default CreateExam;
