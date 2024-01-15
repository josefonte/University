var express = require('express');
var axios = require('axios');
var router = express.Router();

var Prova = require('../controllers/prova')

/* GET /api/gestao */
router.get('/api/gestao', function(req, res, next) {
  Prova.getAll()
    .then(dados => res.status(200).json(dados))
    .catch(erro => res.status(520).json({erro: erro, mensagem: "Erro na obtenção do número das provas!"}))
});


/* GET /api/gestao/gestprovas/<IDU>?id=<IDP> */
router.get('/api/gestao/gestprovas/:idUtilizador', function(req, res, next){
  if (req.query.id != undefined){
    Prova.getProva(req.query.id, req.params.idUtilizador)
      .then(dados => res.status(200).json(dados))
      .catch(erro => res.status(521).json({erro: erro, mensagem: "Erro na obtenção da prova com o id: " + req.query.id}))
  }
  else{
    Prova.getProvas(req.params.idUtilizador)
      .then(dados => res.status(200).json(dados))
      .catch(erro => res.status(522).json({erro: erro, mensagem: "Erro na obtenção das provas"}))
  }
});

/* POST /api/gestao/addUserToProva/:idUtilizador?id=<ID_PROVA> */
router.post('/api/gestao/addUserToProva/:idUtilizador', function(req, res, next) {
  const idProva = req.query.id;
  if (idProva !== undefined) {
    adicionarAlunoAProva( idProva,req.params.idUtilizador)
      .then(() => res.status(200).json({ mensagem: "Utilizador adicionado com sucesso à prova." }))
      .catch(erro => res.status(523).json({ erro: erro, mensagem: "Erro ao adicionar utilizador à prova." }));
  } else {
    res.status(524).json({ mensagem: "Rota Inválida" });
  }
});

router.get('/api/gestao/gestprovas/getprova/:idProva', function(req, res, next){
  Prova.getProvaById(req.params.idProva)
    .then(dados => res.status(200).json(dados))
    .catch(erro => res.status(500).json({ erro: erro, mensagem: "Erro na obtenção da prova com o id: " + req.params.idProva }));
});

/* GET /api/gestao/edit/:idProva?idDocente=<ID_DOCENTE> */
router.get('/api/gestao/editar/:idProva', function(req, res, next){
  const idDocente = req.query.idDocente;

  Prova.obterDetalhesProvaParaEdicao(req.params.idProva, idDocente)
      .then(dados => res.status(200).json(dados))
      .catch(erro => res.status(527).json({ erro: erro, mensagem: "Erro ao obter detalhes da prova para edição" }));
});


/* POST /api/gestao/CREATE/<IDU> */
router.post('/api/gestao/criar/:idUtilizador', function(req, res, next){
  console.log(req.body)
  Prova.criarProva(req.body)
      .then(dados => res.status(200).json(dados))
      .catch(erro => res.status(523).json({ erro: erro, mensagem: "Erro na criação da prova" }));
});

/* POST /api/gestao/CREATE/questao/<IDU>?idProva=<IDP> */
router.post('/api/gestao/criar/questao/:idUtilizador', function(req, res, next){

  Prova.adicionarQuestao(req.query.idProva, req.params.idUtilizador, req.body)
      .then(dados => res.status(200).json(dados))
      .catch(erro => res.status(523).json({ erro: erro, mensagem: "Erro na criação da prova" }));

});


/* PUT /api/gestao/gestprovas/edit/:idProva?idDocente=<ID_DOCENTE> */
router.put('/api/gestao/editar/:idProva', function(req, res, next){
  const idDocente = req.query.idDocente;

  Prova.atualizarProvaExistente(req.params.idProva, idDocente, req.body)
      .then(dados => res.status(200).json(dados))
      .catch(erro => res.status(528).json({ erro: erro, mensagem: "Erro ao atualizar a prova existente" }));
});


/* DELETE /api/gestao/gestprovas/apagar/:idProva?idDocente=<ID_DOCENTE> */
router.delete('/api/gestao/apagar/:idProva', function(req, res, next){
  const idDocente = req.query.idDocente;

  Prova.verificarCriadorProva(req.params.idProva, idDocente)
      .then(docenteEhCriador => {
          if (docenteEhCriador) {
              return Prova.apagarProva(req.params.idProva);
          } else {
              throw new Error("O docente não tem permissão para apagar esta prova.");
          }
      })
      .then(dados => res.status(200).json(dados))
      .catch(erro => res.status(526).json({ erro: erro.message, mensagem: "Erro ao apagar a prova" }));
});



module.exports = router;
