var express = require('express');
var router = express.Router();
var axios = require('axios');

//ze isto não existe




router.get('/correct/listaprova/:id', function(req, res, next) {
    axios.get(`http://localhost:7778/api/correct/listaprova/${req.params.id}`).then(resp => {
      res.status(200).json(resp.data)
    })
    .catch(erro => res.status(523).json({erro: erro, mensagem: "Erro na obtenção das provas corrigidas com id " + req.params.id}))

})

router.get('/correct/corrAUTOprovas/:id', function(req, res, next){  
    axios.get(`http://localhost:7778/api/correct/corrAUTOprovas/${req.params.id}`, req.body).then(resp => {
      res.status(200).json(resp.data)
    })
    .catch(erro => res.status(526).json({erro: erro, mensagem: "Erro na correçao auto com id " + req.params.idProva}))
});


router.put('/correct/corriMANUALprovas/:id', function(req, res, next){
    axios.put(`http://localhost:7778/api/correct/corriMANUALprovas/${req.params.id}`, req.body).then(resp => {
      res.status(200).json(resp.data)
    })
    .catch(erro => res.status(526).json({erro: erro, mensagem: "Erro na correçao manual com id " + req.params.idProva}))
});

router.put('/correct/prova/:id', function(req, res, next){
  axios.put(`http://localhost:7778/api/correct/prova/${req.params.id}`, req.body).then(resp => {
    res.status(200).json(resp.data)
  })
  .catch(erro => res.status(526).json({erro: erro, mensagem: "Erro na edição da prova corrigida com id " + req.params.idProva}))
});




router.get('/see/listprovas/:id/ready', async (req, res) => {
  axios.get(`http://localhost:7778/api/see/listprovas/${req.params.id}/ready`, req.body).then(resp => {
    res.status(200).json(resp.data)
  })
  .catch(erro => res.status(526).json({erro: erro, mensagem: "Erro na edição da prova corrigida com id " + req.params.idProva}))
});


router.get('/prova/:id', async (req, res) => {
  axios.get(`http://localhost:7778/api/see/prova/${req.params.id}`, req.body).then(resp => {
    res.status(200).json(resp.data)
  })
  .catch(erro => res.status(526).json({erro: erro, mensagem: "Erro na edição da prova corrigida com id " + req.params.idProva}))
});


router.get('/alunoready/:id', async (req, res) => {
  console.log("ENTROU NO ALUNO READY / ID ")
  axios.get(`http://localhost:7778/api/see/alunoready/${req.params.id}`, req.body).then(async resp => {
    var resp = resp.data
    console.log("1")
    var l = resp.length
    var objectlist = []
    var object
    console.log("1.1",l)
    for(i=0;i<l;i++){
      console.log("1.2",resp[i])
      let resp2 = await axios.get(`http://localhost:8011/api/gestao/gestprovas/getprova/${resp[i].id_prova_duplicada}`)

        resp2 = resp2.data
        console.log(resp2)
      
        console.log("2", resp[i].id_prova_duplicada)
        object = {idProva:resp[i].id_prova_duplicada, nome:resp2.nome, data: resp2.data, classificação_total: resp[i].classificacao_final}
        console.log("3")
      //n sei se queres o id da prova realizada ou duplicada
      console.log("4")
      objectlist.push(object)
      console.log("5")
    }
    console.log("6")
    res.status(200).json(objectlist)
    console.log("7")
  })
  .catch(erro => res.status(526).json({erro: erro, mensagem: "Erro na edição da prova corrigida com id " + req.params.idProva}))
  

});




//ConsultarProva(idProva,idAluno) -> { nomeProva, classificação_total, questoes:[{enunciado,  cotaçaototal, cotação obtida, resposta:{selected,content,correct} }] } 
router.get('/alunoready/:id/:id2', async (req, res) => {
  console.log("1")
  axios.get(`http://localhost:7778/api/see/alunoready/${req.params.id}/${req.params.id2}`, req.body).then(resp => {
    var resp=resp.data
  console.log("resp:",resp)
    axios.get(`http://localhost:8011/api/gestao/gestprovas/getprova/${req.params.id}`).then(resp2 => {
      var resp2=resp2.data
      console.log("resp2:",resp2)
      qlist = []
      const cotacaoDict = {};
      for (const questao of resp.questoes) {
        cotacaoDict[(questao.nr_questao,questao.resposta)] = questao.cotacaoTotal;
      }
      console.log("4")
      console.log("resp2",resp2.questoes)
      for(const questao of resp2.questoes){
        rlist=[]
        var qnum = 0
      console.log("4.1")
      console.log("questao",questao)
      console.log("questao.options",questao.options)
        for(const resposta of questao.options){
          console.log("opt", resposta)
          var isSelected = ((questao._id,resposta._id) in cotacaoDict)
          var isCorrect = ( resposta.cotacao > 0)
          obj3={
            selected:isSelected,
            content:resposta.texto,
            correct:isCorrect,
          }
          rlist.push(obj3)
        }
      console.log("4.2")
        let sum = 0;
        for (const [key, cotacao] of Object.entries(cotacaoDict)) {
          console.log(key,cotacao)
          const [questaoKey, _] = key;
          if (questaoKey === questao) {
            sum += cotacao;
          }
        }
      console.log("4.3")
      console.log( resp2 )
        obj2={
          enunciado:questao.enunciado,
          cotaçaototal: questao.cotacaoTotal ,
          cotação_obtida: sum,
          resposta:rlist,
        }
      console.log("4.4")
        qlist.push(obj2)
        console.log("4.5")
      }
      console.log("5")
      var object = {nome:resp2.nome, classificação_total:resp.classificacao_final, questoes: qlist}
      console.log("6")
      res.status(200).json(object)
    })
    .catch(erro => res.status(526).json({erro: erro, mensagem: "Erro na edição da prova corrigida com id " + req.params.idProva}))
    //n sei se queres o id da prova realizada ou duplicada
    
  })
  .catch(erro => res.status(526).json({erro: erro, mensagem: "Erro na edição da prova corrigida com id " + req.params.idProva}))
  });


router.post('/prova/:id/recorrect', async (req, res) => {
  console.log("NAO FAZ NADA")
});





router.get('/provas', async (req, res) => {
  axios.get(`http://localhost:7778/api/provas`).then(resp => {
    res.status(200).json(resp.data)
  })
  .catch(erro => res.status(528).json({erro: erro, mensagem: "Erro na pedir as provas"}))
});

router.post('/provas/', function(req, res, next){ 
    axios.post(`http://localhost:7778/api/provas`, req.body).then(resp => {
      res.status(200).json(resp.data)
    })
    .catch(erro => res.status(526).json({erro: erro, mensagem: "Erro a criar prova "}))
});

router.put('/provas/:id', function(req, res, next){
  axios.put(`http://localhost:7778/api/provas/${req.params.id}`, req.body).then(resp => {
    res.status(200).json(resp.data)
  })
  .catch(erro => res.status(526).json({erro: erro, mensagem: "Erro na edição da prova corrigida com id " + req.params.idProva}))
});

router.delete('/provas/:id', function(req, res, next){
    axios.delete(`http://localhost:7778/api/provas/${req.params.id}`).then(resp => {
      res.status(200).json(resp.data)
    })
    .catch(erro => res.status(528).json({erro: erro, mensagem: "Erro na eliminação da prova com o id " + req.params.idProva}))
});





router.get('/questoes', async (req, res) => {
  axios.get(`http://localhost:7778/api/questoes`).then(resp => {
    res.status(200).json(resp.data)
  })
  .catch(erro => res.status(528).json({erro: erro, mensagem: "Erro na pedir as questoes"}))
});

router.get('/questoes/:id', async (req, res) => {
  axios.get(`http://localhost:7778/api/questoes/${req.params.id}`).then(resp => {
    res.status(200).json(resp.data)
  })
  .catch(erro => res.status(528).json({erro: erro, mensagem: "Erro na pedir a questao com id " + req.params.id}))
});

router.put('/questoes/:id', function(req, res, next){
  axios.put(`http://localhost:7778/api/questoes/${req.params.id}`, req.body).then(resp => {
    res.status(200).json(resp.data)
  })
  .catch(erro => res.status(526).json({erro: erro, mensagem: "Erro na edição da questao com id " + req.params.idProva}))
});

router.delete('/questoes', function(req, res, next){
    axios.delete(`http://localhost:7778/api/questoes/${req.params.id}`).then(resp => {
      res.status(200).json(resp.data)
    })
    .catch(erro => res.status(528).json({erro: erro, mensagem: "Erro na eliminação da questao com o id " + req.params.idProva}))
});






router.get('/see/listprovas/:id/ready', async (req, res) => {
  axios.get(`http://localhost:7778/api/see/listprovas/${req.params.id}/ready`).then(resp => {
    res.status(200).json(resp.data)
  })
  .catch(erro => res.status(528).json({erro: erro, mensagem: " "+req.params.id}))
});

router.get('/see/prova/:id', async (req, res) => {
  axios.get(`http://localhost:7778/api/see/prova/${req.params.id}`).then(resp => {
    res.status(200).json(resp.data)
  })
  .catch(erro => res.status(528).json({erro: erro, mensagem: " "+req.params.id}))
});

router.get('/see/alunoready/:id', function(req, res, next){ 
    axios.get(`http://localhost:7778/api/see/alunoready/${req.params.id}`).then(resp => {
      res.status(200).json(resp.data)
    })
    .catch(erro => res.status(526).json({erro: erro, mensagem: " "+req.params.id}))
});

router.get('/see/alunoready/:id/:id2', function(req, res, next){ 
  axios.get(`http://localhost:7778/api/see/alunoready/${req.params.id}/${req.params.id2}`).then(resp => {
    res.status(200).json(resp.data)
  })
  .catch(erro => res.status(526).json({erro: erro, mensagem: " "+req.params.id+" "+req.params.id2}))
});

router.post('/see/prova/:id/recorrect', function(req, res, next){ 
  axios.post(`http://localhost:7778/api/see/prova/${req.params.id}/recorrect`, req.body).then(resp => {
    res.status(200).json(resp.data)
  })
  .catch(erro => res.status(526).json({erro: erro, mensagem: " " +req.params.id}))
});




router.get('/tipoquestoes', function(req, res, next){ 
  axios.get(`http://localhost:7778/api/tipoquestoes`).then(resp => {
    res.status(200).json(resp.data)
  })
  .catch(erro => res.status(526).json({erro: erro, mensagem: " "}))
});

router.post('/tipoquestoes', function(req, res, next){ 
  axios.post(`http://localhost:7778/api/tipoquestoes`, req.body).then(resp => {
    res.status(200).json(resp.data)
  })
  .catch(erro => res.status(526).json({erro: erro, mensagem: " "}))
});

router.delete('/tipoquestoes/:id', function(req, res, next){
  axios.delete(`http://localhost:7778/api/tipoquestoes/${req.params.id}`).then(resp => {
    res.status(200).json(resp.data)
  })
  .catch(erro => res.status(528).json({erro: erro, mensagem: " " + req.params.idProva}))
});


module.exports = router;
