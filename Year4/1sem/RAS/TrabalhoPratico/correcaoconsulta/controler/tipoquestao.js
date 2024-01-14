var TipoQuestao = require('../models/tipoquestao')

module.exports.list = () =>{
    return TipoQuestao.find()
                .then(dados=>{
                    return dados
                }
                )
                .catch(erro=>{
                   return erro
                })
}

module.exports.getTipoQuestao = id =>{
    return TipoQuestao.findOne({_id:id})
                .then(dados=>{
                    return dados
                }
                )
                .catch(erro=>{
                   return erro
                })
}

module.exports.getUserTipoQuestaos = idUser => {
    return TipoQuestao.find({ idUser: idUser })
                .then(dados => {
                    return dados;
                })
                .catch(erro => {
                    return erro;
                });
  };

module.exports.addTipoQuestao = (TipoQuestao) => {
    return TipoQuestao.collection.insertOne(TipoQuestao)
                .then(dados=>{
                    return dados
                }
                )
                .catch(erro=>{
                   return erro
                })
  }

module.exports.editTipoQuestao = (id,TipoQuestao)=>{
    return TipoQuestao.updateOne({_id:id},TipoQuestao)
                .then(dados=>{
                    return dados
                }
                )
                .catch(erro=>{
                   return erro
                })
}

module.exports.deleteTipoQuestao = id =>{
    return TipoQuestao.deleteOne({_id:id})
                .then(dados=>{
                    return dados
                }
                )
                .catch(erro=>{
                   return erro
                })
}
