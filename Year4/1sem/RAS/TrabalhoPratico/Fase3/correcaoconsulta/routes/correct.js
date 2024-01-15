const express = require('express');
const router = express.Router();
const Prova = require('../models/prova');
const Questao = require('../models/questao');
const TipoQuestao = require('../models/tipoquestao');
const fs = require('fs');
const axios = require('axios');

/*Lista de provas
Recebe uma lista de to-
dos os IDs de prova
*/

router.get('/listaprova/:id', async (req, res) => {
    try {
      var provasId = req.params.id;
      var provas = await Prova.getProvasByDuplicateId(provasId);
      res.json(provas);
    } catch (error) {
      console.error('Error getting Provas:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });

router.get('/corrAUTOprovas/:id', async (req, res) => {
    try {
      var provadupId = req.params.id
      console.log(provadupId)
      var provaoriginal;

      // nao esta no sitio certo
      
      let resp = await axios.get(`http://localhost:8011/api/gestao/gestprovas/getprova/${provadupId}`)
      provaoriginal = resp.data;
      console.log(provaoriginal)
      
      /*
    //BUSCAR TODAS AS PROVAS QUE FORAM REALIZADAS
     const provas = await Prova.getProvasByDuplicateId(provadupId) 
      
     //TESTE DO EQUIVALENTE DE FAZER AXIOS.GET
      const fileContent = fs.readFileSync("test/criar.json", 'utf8');


      provaoriginal = JSON.parse(fileContent); 
      //FINAL DO CODIGO DE TESTE
      */

      //QUESTOES DA PROVA ORIGINAL
      questoes = provaoriginal.questoes
      let provas = await Prova.getProvasByDuplicateId(provadupId)
      console.log(provas)
      //PARA TODAS AS PROVAS
      for(const prova of provas)    {  
        //BUSCAR O ID DA PROVA REALIZADA
        const prova_id = prova.id_prova_realizada
        //INICIAR A COTACAO DO ALUNO A 0
        var cotacoes = 0
          for (const questao of questoes) {
            //BUSCAR O ID DA QUESTAO ORIGINAL
            const questaoId = questao._id;
            //VER A RESPOSTA DO ALUNO, POR DEFEITO É 0
            var cotacaodaquestao = 0
            //VER QUANTO A PERGUNTA VALE
            const cotacao = Number(questao.cotacaoTotal);

            //BUSCAR AS RESPOSTA PARA A PERGUNTA
            const respostas = await Questao.getQuestaoByProvaandQuestion(prova_id,questaoId)
            if (respostas){


                //VER AS OPCOES POSSÍVEIS PARA A RESPOSTA
                console.log(questao)
                var opcoes = questao.options
                console.log(opcoes)
                if (opcoes){
                    for (const option of opcoes){
                        //COMPARAR A RESPOSTA DO ALUNO COM A OPCAO
                        if (option._id == respostas.resposta){
                            cotacaodaquestao+= Number(option.cotacao)
                        }
                    }
                }
            }
   

            const cotacaoTotalValue = Number(cotacao * cotacaodaquestao);
            const jsonObject = {
              cotacaoTotal: cotacaoTotalValue
            };
            if(respostas){
                await Questao.updateQuestao(respostas.id_questao,jsonObject)
                cotacoes += Number(cotacaoTotalValue)
            }
        console.log("cotacoes:", cotacoes)
            
        }
        console.log("cotacoes:", cotacoes)
        if(cotacoes < 0) cotacoes=0;
        const jsonObject2 = {
            classificacao_final: cotacoes
          };

        await Prova.updateProva(prova_id, jsonObject2)
      }
      
      
      res.json("Cool");

    } catch (error) {
      console.error('Error getting Provas:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });

router.put('/corriMANUALprovas/:id', async (req, res) => {
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


//mesma coisa q o anterior ?!?!?!?!?!
router.put('/prova/:id', async (req, res) => {
    try {
      var provaId = req.params.id;
      var provaCorrigidaSubmetida;

      axios.get("http://localhost:porta_q_nao_sei/ frontend ou api do frontend manda as correçoes q o stor colocou na interface e eu ponho? n sei onde raiz isso acontece ").then(resp => {
        // pode ser necessario tratar a data ?
        provaCorrigidaSubmetida = resp.data;
      })

      var prova = await Prova.updateProva(provaId,provaCorrigidaSubmetida);
      res.json(prova);

    } catch (error) {
      console.error('Error getting Provas:', error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  });

  


module.exports = router;
