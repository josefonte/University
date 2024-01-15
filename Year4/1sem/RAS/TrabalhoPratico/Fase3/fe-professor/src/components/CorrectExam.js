import React from 'react';
import axios from 'axios';
import { FormControl} from '@mui/material';
import { Button, TextField, Typography, Switch } from '@material-ui/core';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';

import './css/CorrectExam.css'
const CorrectExam = () => {
  
  const [examNumber, setExamNumber] = React.useState('');
  const [correction, setCorrection] = React.useState(false);
  const [openDialog, setOpenDialog] = React.useState(false);
  

  const handleSubmit = async (event) => {
    // Cancela o comportamento padrão do formulário para evitar recarregamento da página
    event.preventDefault();

    // Verifica se a correção automática está ativada
    if (correction) {
      axios.get(`http://localhost:8010/api/correcaoconsulta/correct/corrAUTOprovas/${examNumber}`)
      .then(response => {
        // Success callback
        setOpenDialog(true);
      })
      .catch(error => {
        // Error callback
        console.error(error);
      });
      setOpenDialog(true);
    } else {
      // Se a correção automática não estiver ativada, você pode exibir uma mensagem ou tomar outra ação
      console.log("A correção automática não está ativada");
    }
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  return (
    <div>
      <FormControl  class="form" style={{ flexDirection: 'column' , textAlign:'center', padding: '20px'}}> 
        <Typography variant="h4" className='title'>
          Correção da Prova
        </Typography>
        <TextField
          id="exam-number"
          label="Prova (ID da Prova)"
          value={examNumber}
          onChange={(e) => setExamNumber(e.target.value)}
          className='textField'
          margin="normal"
          variant="outlined"
        />

        <div className="switch-container">
          <Typography className='title'> Correção Automática</Typography>
          <Switch
            label="Correção Automática"
            checked={correction}
            onChange={(event) => setCorrection(event.target.checked)}
            className='textField'
          />    
        </div>
    
        
        <Button variant="contained" color="primary" onClick={handleSubmit} >
          Submeter
        </Button>
       

        <Dialog open={openDialog} onClose={handleCloseDialog} className='modal'>
        <DialogTitle>Correção Automática executada com sucesso!</DialogTitle>
        <DialogContent>
          <DialogContentText> A sua prova foi corrigida automáticamente com sucesso.</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog} color="primary">
            Fechar
          </Button>
        </DialogActions>
      </Dialog>


      </FormControl>
    </div>
  );
};

export default CorrectExam;
