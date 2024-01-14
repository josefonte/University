const connection = require('../mysql/conn');

const QuestaoModel = {
  getAllQuestoes: () => {
    return new Promise((resolve, reject) => {
      const selectQuery = 'SELECT * FROM Questao';
      connection.query(selectQuery, (err, results) => {
        if (err) {
          reject(err);
        } else {
          const QuestaoData = results.map(result => ({
            id_questao: result.id_questao,
            nr_questao: result.nr_questao,
            resposta: result.resposta,
            cotacaoTotal: result.cotacaoTotal,   
            Prova_id_prova_realizada: result.Prova_id_prova_realizada, 
            TipoQuestao_id_tipo: result.TipoQuestao_id_tipo, 
          }));
          resolve(QuestaoData);
        }
      });
    });
  },

  getQuestaoById: (id) => {
    return new Promise((resolve, reject) => {
      const selectQuery = 'SELECT * FROM Questao WHERE id_questao = ?';
      connection.query(selectQuery, [id], (err, results) => {
        if (err) {
          reject(err);
        } else {
          if (results.length > 0) {
            const questaoData = {
              id_questao: result[0].id_questao,
              nr_questao: result[0].nr_questao,
              resposta: result[0].resposta,
              cotacaoTotal: result[0].cotacaoTotal,   
              Prova_id_prova_realizada: result[0].Prova_id_prova_realizada, 
              TipoQuestao_id_tipo: result[0].TipoQuestao_id_tipo, 
            };
            resolve(questaoData);
          } else {
            resolve(null);
          }
        }
      });
    });
  },

  getQuestoesByProva: (id) => {
    return new Promise((resolve, reject) => {
      const selectQuery = 'SELECT * FROM Questao WHERE Prova_id_prova_realizada = ?';
      connection.query(selectQuery, [id], (err, results) => {
        if (err) {
          reject(err);
        } else {
          if (results.length > 0) {
            const questaoData = {
              id_questao: result[0].id_questao,
              nr_questao: result[0].nr_questao,
              resposta: result[0].resposta,
              cotacaoTotal: result[0].cotacaoTotal,   
              Prova_id_prova_realizada: result[0].Prova_id_prova_realizada, 
              TipoQuestao_id_tipo: result[0].TipoQuestao_id_tipo, 
            };
            resolve(questaoData);
          } else {
            resolve(null);
          }
        }
      });
    });
  },

  getQuestaoByProvaandQuestion: (id, id2) => {
    return new Promise((resolve, reject) => {
      const selectQuery = 'SELECT * FROM Questao WHERE Prova_id_prova_realizada = ? AND nr_questao = ?';
      connection.query(selectQuery, [id,id2], (err, results) => {
        if (err) {
          reject(err);
        } else {
          if (results.length > 0) {
            const questaoData = {
              id_questao: results[0].id_questao,
              nr_questao: results[0].nr_questao,
              resposta: results[0].resposta,
              cotacaoTotal: results[0].cotacaoTotal,   
              Prova_id_prova_realizada: results[0].Prova_id_prova_realizada, 
              TipoQuestao_id_tipo: results[0].TipoQuestao_id_tipo, 
            };
            resolve(questaoData);
          } else {
            resolve(null);
          }
        }
      });
    });
  },

  createQuestao: (questao) => {
    return new Promise((resolve, reject) => {
      const insertQuery = 'INSERT INTO Questao (id_questao, nr_questao ,resposta ,cotacaoTotal, Prova_id_prova_realizada, TipoQuestao_id_tipo) VALUES (?, ?, ?, ?, ?, ?)';
      const values = [questao.id_questao, questao.nr_questao, questao.resposta, questao.cotacaoTotal, questao.Prova_id_prova_realizada, questao.TipoQuestao_id_tipo];
  
      connection.query(insertQuery, values, (err, results) => {
        if (err) {
          reject(err);
        } else {
          resolve(results.insertId);
        }
      });
    });
  },

  updateQuestao: (id, updatedQuestao) => {
    console.log(updatedQuestao)
    return new Promise((resolve, reject) => {
      const keys = Object.keys(updatedQuestao);
      const values = Object.values(updatedQuestao);
  
      // Generate SET clause dynamically
      const setClause = keys.map((key) => `${key} = ?`).join(', ');
  
      // Create the update query with the dynamic SET clause
      const updateQuery = `UPDATE Questao SET ${setClause} WHERE id_questao = ?`;
  
      // Execute the query with values array including the values and id
      connection.query(updateQuery, [...values, id], (err, results) => {
        if (err) {
          console.log(err)
          reject(err);
        } else {
          resolve(results.affectedRows > 0);
        }
      });
    });
  },
  

  deleteQuestao: (id) => {
    return new Promise((resolve, reject) => {
      const deleteQuery = 'DELETE FROM Questao WHERE id_questao = ?';
      connection.query(deleteQuery, [id], (err, results) => {
        if (err) {
          reject(err);
        } else {
          resolve(results.affectedRows > 0);
        }
      });
    });
  },
};

module.exports = QuestaoModel;
