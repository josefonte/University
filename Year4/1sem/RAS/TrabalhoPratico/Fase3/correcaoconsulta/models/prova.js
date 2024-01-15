const connection = require('../mysql/conn');

const ProvaModel = {
  getAllProvas: () => {
    return new Promise((resolve, reject) => {
      const selectQuery = 'SELECT * FROM Prova';
      connection.query(selectQuery, (err, results) => {
        if (err) {
          reject(err);
        } else {
          const ProvaData = results.map(result => ({
            id_prova_realizada: result.id_prova_realizada,
            id_prova_duplicada: result.id_prova_duplicada,
            classificacao_final: result.classificacao_final,
            num_Aluno: result.num_Aluno,
          }));
          resolve(ProvaData);
        }
      });
    });
  },

  getProvaById: (id) => {
    return new Promise((resolve, reject) => {
      const selectQuery = 'SELECT * FROM Prova WHERE id_prova_realizada = ?';
      connection.query(selectQuery, [id], (err, results) => {
        if (err) {
          reject(err);
        } else {
          if (results.length > 0) {
            const questaoData = {
              id_questao: results[0].id_questao,
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
  getProvasByAluno: (id) => {
    return new Promise((resolve, reject) => {
      const selectQuery = `SELECT Prova.*, Questao.*
        FROM Prova
        LEFT JOIN Questao ON Prova.id_prova_realizada = Questao.Prova_id_prova_realizada
        WHERE Prova.num_aluno = ?;`;
  
      connection.query(selectQuery, [id], (err, results) => {
        if (err) {
          reject(err);
        } else {
          const provaDataMap = new Map();
  
          results.forEach((result) => {
            const idProvaRealizada = result.id_prova_realizada;
  
            if (!provaDataMap.has(idProvaRealizada)) {
              provaDataMap.set(idProvaRealizada, {
                id_prova_realizada: result.id_prova_realizada,
                id_prova_duplicada: result.id_prova_duplicada,
                classificacao_final: result.classificacao_final,
                num_Aluno: result.num_Aluno,
                respostas: [],
              });
            }
  
            provaDataMap.get(idProvaRealizada).respostas.push({
              id_questao: result.id_questao,
              nr_questao: result.nr_questao,
              resposta: result.resposta,
              cotacaoTotal: result.cotacaoTotal,
              TipoQuestao_id_tipo: result.TipoQuestao_id_tipo,
            });
          });
  
          const ProvaData = Array.from(provaDataMap.values());
          resolve(ProvaData);
        }
      });
    });
  },
  getProvasByAlunoAndProva: (id, id2) => {
    return new Promise((resolve, reject) => {
      const selectQuery = `SELECT Prova.*, Questao.*
        FROM Prova
        LEFT JOIN Questao ON Prova.id_prova_realizada = Questao.Prova_id_prova_realizada
        WHERE Prova.num_aluno = ? AND Prova.id_prova_duplicada = ?;`;
  
      connection.query(selectQuery, [id, id2], (err, results) => {
        if (err) {
          reject(err);
        } else {
          const provaDataMap = new Map();
  
          results.forEach((result) => {
            const idProvaRealizada = result.id_prova_realizada;
  
            if (!provaDataMap.has(idProvaRealizada)) {
              provaDataMap.set(idProvaRealizada, {
                id_prova_realizada: result.id_prova_realizada,
                id_prova_duplicada: result.id_prova_duplicada,
                classificacao_final: result.classificacao_final,
                num_Aluno: result.num_Aluno,
                questoes: [],
              });
            }
  
            provaDataMap.get(idProvaRealizada).questoes.push({
              id_questao: result.id_questao,
              nr_questao: result.nr_questao,
              resposta: result.resposta,
              cotacaoTotal: result.cotacaoTotal,
              Prova_id_prova_realizada: result.Prova_id_prova_realizada,
              TipoQuestao_id_tipo: result.TipoQuestao_id_tipo,
            });
          });
  
          const [singleProvaData] = Array.from(provaDataMap.values());
          resolve(singleProvaData);
        }
      });
    });
  },
  createProva: (prova) => {
    return new Promise((resolve, reject) => {
      const insertQuery = 'INSERT INTO Prova (id_prova_realizada, id_prova_duplicada, classificacao_final, num_Aluno) VALUES (?, ?, ?, ?)';
      const values = [prova.id_prova_realizada, prova.id_prova_duplicada, prova.classificacao_final, prova.num_Aluno];
  
      connection.query(insertQuery, values, (err, results) => {
        if (err) {
          reject(err);
        } else {
          resolve(results.insertId);
        }
      });
    });
  },

  updateProva: (id, updatedProva) => {
    return new Promise((resolve, reject) => {
      const keys = Object.keys(updatedProva);
      const values = Object.values(updatedProva);
  
      // Generate SET clause dynamically
      const setClause = keys.map((key) => `${key} = ?`).join(', ');
  
      // Create the update query with the dynamic SET clause
      const updateQuery = `UPDATE Prova SET ${setClause} WHERE id_prova_realizada = ?`;
  
      // Execute the query with values array including the values and id
      connection.query(updateQuery, [...values, id], (err, results) => {
        if (err) {
          console.log(results)

          reject(err);
        } else {
          console.log(results)

          resolve(results.affectedRows > 0);
        }
      });
    });
  },

  getProvasByDuplicateId: (duplicateId) => {
  return new Promise((resolve, reject) => {
    const selectQuery = 'SELECT * FROM Prova WHERE id_prova_duplicada = ?';

    
    connection.query(selectQuery, [duplicateId], (err, results) => {
      if (err) {
        reject(err);
      } else {
        const ProvaData = results.map(result => ({
          id_prova_realizada: result.id_prova_realizada,
          id_prova_duplicada: result.id_prova_duplicada,
          classificacao_final: result.classificacao_final,
          num_Aluno: result.num_Aluno,
        }));
        resolve(ProvaData);
        }
      });
    });
  },

  getProvasReady: (id) => {
  return new Promise((resolve, reject) => {
    const selectQuery =`
    SELECT Prova.*, Questao.*
    FROM Prova
    JOIN Questao ON Prova.id_prova_realizada = Questao.Prova_id_prova_realizada 
    WHERE Prova.classificacao_final IS NOT NULL AND Prova.id_prova_duplicada = ?;
    `;

    connection.query(selectQuery, [id], (err, results) => {
        if (err) {
          reject(err);
        } else {
          const ProvaData = results.map(result => ({
            id_prova_realizada: result.id_prova_realizada,
            id_prova_duplicada: result.id_prova_duplicada,
            classificacao_final: result.classificacao_final,
            num_Aluno: result.num_Aluno,
            id_questao: result.id_questao,
            nr_questao: result.nr_questao,
            resposta: result.resposta,
            cotacaoTotal: result.cotacaoTotal,
            Prova_id_prova_realizada: result.Prova_id_prova_realizada,
            TipoQuestao_id_tipo: result.TipoQuestao_id_tipo,
          }));
          resolve(ProvaData);
        }
      });
    });
  },

  deleteProva: (id) => {
    return new Promise((resolve, reject) => {
      const deleteQuery = 'DELETE FROM Prova WHERE id_prova_realizada = ?';
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

module.exports = ProvaModel;
