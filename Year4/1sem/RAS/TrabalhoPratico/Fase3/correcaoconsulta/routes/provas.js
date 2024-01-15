const express = require('express');
const router = express.Router();
const Prova = require('../models/prova');

// Get all Provas
router.get('/', async (req, res) => {
  try {
    const provas = await Prova.getAllProvas();
    res.json(provas);
  } catch (error) {
    console.error('Error getting Provas:', error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

// Create Prova
router.post('/', async (req, res) => {
  try {
    const prova = await Prova.createProva(req.body);
    res.status(201).json(prova);
  } catch (error) {
    console.error('Error creating Prova:', error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

router.put('/:id', async (req, res) => {
    const provaId = req.params.id;
  
    try {
      const success = await Prova.updateProva(provaId, req.body);
  
      if (!success) {
        res.status(404).json({ error: 'Prova not found' });
        return;
      }
  
      res.json({ message: 'Prova updated successfully' });
    } catch (error) {
      console.error('Error updating Prova:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });

//Delete Prova
router.delete('/:id', async (req, res) => {
    const provaId = req.params.id;
  
    try {
      const success = await Prova.deleteProva(provaId);
  
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
