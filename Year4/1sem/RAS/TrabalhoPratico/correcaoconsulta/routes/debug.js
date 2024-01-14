const express = require('express');
const router = express.Router();
const Prova = require('../models/prova');
const Questao = require('../models/questao');
const TipoQuestao = require('../models/tipoquestao');
const fs = require('fs');
const axios = require('axios');
const { json } = require('sequelize');


router.get('/', async (req, res) => {
    try {
      const provas = await Prova.getAllProvas();
      const questao = await Questao.getAllQuestoes();
      const tipoquestao = await TipoQuestao.getAllTipoQuestao();
      const jsonList = {
        provas: provas,
        questoes: questao,
        tipoQuestoes: tipoquestao,
      };
      
      res.json(jsonList);
    } catch (error) {
      console.error('Error getting Provas:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });

  router.get('/drop', async (req, res) => {
    try {
        const dropdatabase=`DROP DATABASE IF EXISTS fixe;`
        // Create tables at the start
        connection.query(dropdatabase,(err, results) => {
          if (err) {
            console.error('Error deleting tables:', err);
          } else {
            console.log('Tables deleted successfully');
          }
        })
      
      res.json(jsonList);
    } catch (error) {
      console.error('Error getting Provas:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });
  router.get('/create', async (req, res) => {
    try {
        const createdatabase = `CREATE DATABASE fixe;`;
        
        const usedatabase= 'USE fixe;'
        connection.query(dropdatabase,(err, results) => {
          if (err) {
            res.json(err);
            console.error('Error deleting tables:', err);
          } else {
            res.json(results);

            console.log('Tables deleted successfully');
          }
        })

        const createTableProva = `
  CREATE TABLE IF NOT EXISTS Prova (
    id_prova_realizada VARCHAR(255) NOT NULL,
    id_prova_duplicada VARCHAR(255) NOT NULL,
    classificacao_final INT(10) NULL,
    num_Aluno VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_prova_realizada)
  );
`;

connection.query(createTableProva, (err, results) => {
  if (err) {
    console.error('Error creating table Prova:', err);
  } else {
    console.log('Prova table created successfully');
  }
});
const createTableTipoQuestao = `
  CREATE TABLE IF NOT EXISTS TipoQuestao(
    id_tipo VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_tipo)
  );
`;

connection.query(createTableTipoQuestao, (err, results) => {
  if (err) {
    console.error('Error creating table TipoQuestao:', err);
  } else {
    console.log('TipoQuestao Table created successfully');
  }
});
const createTableQuestao = `
  CREATE TABLE IF NOT EXISTS Questao (
    id_questao VARCHAR(255) NOT NULL,
    nr_questao VARCHAR(255) NOT NULL,
    resposta VARCHAR(255) NULL,
    cotacaoTotal FLOAT(10) NULL,
    Prova_id_prova_realizada VARCHAR(255) NOT NULL,
    TipoQuestao_id_tipo VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_questao),
    FOREIGN KEY (Prova_id_prova_realizada)
      REFERENCES Prova (id_prova_realizada)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
    FOREIGN KEY (TipoQuestao_id_tipo)
      REFERENCES TipoQuestao (id_tipo)
      ON DELETE CASCADE
      ON UPDATE CASCADE
  );
`;

connection.query(createTableQuestao, (err, results) => {
  if (err) {
    console.error('Error creating table Questao:', err);
  } else {
    console.log('Questao table created successfully');
  }
});

      
    } catch (error) {
      console.error('Error getting Provas:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });
module.exports = router;
