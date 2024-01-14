const connection = require('../mysql/conn');

const TipoQuestaoModel = {
  getAllTipoQuestao: () => {
    return new Promise((resolve, reject) => {
      const selectQuery = 'SELECT * FROM TipoQuestao';
      connection.query(selectQuery, (err, results) => {
        if (err) {
          reject(err);
        } else {
          const TipoQuestaoData = results.map(result => ({
            id_tipo: result.id_tipo,
          }));
          resolve(TipoQuestaoData);
        }
      });
    });
  },

  getTipoQuestaoById: (id) => {
    return new Promise((resolve, reject) => {
      const selectQuery = 'SELECT * FROM TipoQuestao WHERE id_tipo = ?';
      connection.query(selectQuery, [id], (err, results) => {
        if (err) {
          reject(err);
        } else {
          if (results.length > 0) {
            const tipoquestaoData = {
              id_tipo: results[0].id_tipo,
            };
            resolve(tipoquestaoData);
          } else {
            resolve(null);
          }
        }
      });
    });
  },

  createTipoQuestao: (tipoquestao) => {
    return new Promise((resolve, reject) => {
      console.log(tipoquestao)
      const insertQuery = 'INSERT INTO TipoQuestao SET ?';
      const values = { id_tipo: tipoquestao.id_tipo }; 
  
      connection.query(insertQuery, values, (err, results) => {
        if (err) {
          reject(err);
        } else {
          resolve(results.insertId);
        }
      });
    });
  }
  ,

  deleteTipoQuestao: (id) => {
    return new Promise((resolve, reject) => {
      const deleteQuery = 'DELETE FROM TipoQuestao WHERE id_tipo = ?';
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

module.exports = TipoQuestaoModel;
