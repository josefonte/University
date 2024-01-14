const express = require('express');
const router = express.Router();
const Questao = require('../models/questao');

// Get all Questoes
router.get('/', async (req, res) => {
  try {
    const questoes = await Questao.getAllQuestoes();
    res.json(questoes);
  } catch (error) {
    console.error('Error getting Questoes:', error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

// Create Questao
router.post('/', async (req, res) => {
  try {
    const questao = await Questao.createQuestao(req.body);
    res.status(201).json(questao);
  } catch (error) {
    console.error('Error creating Questao:', error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

router.get('/:id', async (req, res) => {
    const questaoId = req.params.id;
    try {
      const questao = await Questao.getQuestaoById(questaoId);
      if (!questao) {
        res.status(404).json({ error: 'Questao not found' });
      } else {
        res.json(questao);
      }
    } catch (error) {
      console.error('Error getting Questao by ID:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });

// Update Questao by ID
router.put('/:id', async (req, res) => {
    const questaoId = req.params.id;
    try {
      const updatedQuestao = await Questao.updateQuestao(questaoId, req.body);
      res.json(updatedQuestao);
    } catch (error) {
      console.error('Error updating Questao by ID:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });

// Delete Questao by ID
router.delete('/:id', async (req, res) => {
    const questaoId = req.params.id;
    try {
      const deletedQuestao = await Questao.deleteQuestao(questaoId);
      if (!deletedQuestao) {
        res.status(404).json({ error: 'Questao not found' });
      } else {
        res.json({ message: 'Questao deleted successfully' });
      }
    } catch (error) {
      console.error('Error deleting Questao by ID:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });


module.exports = router;
