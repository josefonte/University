const express = require('express');
const router = express.Router();
const TipoQuestao = require('../models/tipoquestao');

// Get all TipoQuestao
router.get('/', async (req, res) => {
  try {
    const tipoquestao = await TipoQuestao.getAllTipoQuestao();
    res.json(tipoquestao);
  } catch (error) {
    console.error('Error getting tipo questao:', error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

// Create TipoQuestao
router.post('/', async (req, res) => {
  try {
    const tipoquestao = await TipoQuestao.createTipoQuestao(req.body);
    res.status(201).json(tipoquestao);
  } catch (error) {
    console.error('Error creating tipo questao:', error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});


//Delete TipoQuestao
router.delete('/:id', async (req, res) => {
    const tipoId = req.params.id;
  
    try {
      const success = await TipoQuestao.deleteTipoQuestao(tipoId);
  
      if (!success) {
        res.status(404).json({ error: 'Prova not found' });
        return;
      }
  
      res.json({ message: 'Prova deleted successfully' });
    } catch (error) {
      console.error('Error deleting Prova:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });


module.exports = router;
