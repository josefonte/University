const express = require('express');
const router = express.Router();
const Prova = require('../models/prova');
const TipoQuestao = require('../models/tipoquestao');
const Questao = require('../models/questao');



router.get('/listprovas/:id/ready', async (req, res) => {
    try {
      const provasId = req.params.id;
      const provas = await Prova.getProvasReady(provasId);
      res.json(provas);
    } catch (error) {
      console.error('Error getting Provas:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });


router.get('/prova/:id', async (req, res) => {
    try {
      const provaId = req.params.id;
      const prova = await Prova.getProvaById(provaId);
      const perguntas = await Questao.getQuestoesByProva(provaId)
      const jsonObject = {
        prova: prova,
        perguntas: perguntas
      };
      res.json(jsonObject);
    } catch (error) {
      console.error('Error getting Provas:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });


  router.get('/alunoready/:id', async (req, res) => {
    try {
      const alunoID = req.params.id;
      const provas = await Prova.getProvasByAluno(alunoID);
      console.log(provas)
      res.json(provas);
    } catch (error) {
      console.error('Error getting Provas:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });


  router.get('/alunoready/:id/:id2', async (req, res) => {
    try {
      const alunoID = req.params.id2;
      const provaID = req.params.id;

      const provas = await Prova.getProvasByAlunoAndProva(alunoID,provaID);
      console.log(provas)
      res.json(provas);
    } catch (error) {
      console.error('Error getting Provas:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });
router.post('/prova/:id/recorrect', async (req, res) => {
    try {
      const provaId = req.params.id;

      console.log("RAFA : ISTO NAO FAZ SENTIDO, NAO FAÇO")
      console.log("MIGUEL : EU TMB NAO SEI COMO >:C ")
    } catch (error) {
      console.error('Error getting Provas:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });


module.exports = router;
