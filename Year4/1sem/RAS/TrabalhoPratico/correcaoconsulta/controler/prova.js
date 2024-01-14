var Prova = require('../models/prova')

module.exports.list = () =>{
    return Prova.find()
                .then(dados=>{
                    return dados
                }
                )
                .catch(erro=>{
                   return erro
                })
}

module.exports.getProva = id =>{
    return Prova.findOne({_id:id})
                .then(dados=>{
                    return dados
                }
                )
                .catch(erro=>{
                   return erro
                })
}

module.exports.getUserProvas = idUser => {
    return Prova.find({ idUser: idUser })
                .then(dados => {
                    return dados;
                })
                .catch(erro => {
                    return erro;
                });
  };

module.exports.addProva = (Prova) => {
    return Prova.collection.insertOne(Prova)
                .then(dados=>{
                    return dados
                }
                )
                .catch(erro=>{
                   return erro
                })
  }

module.exports.editProva = (id,Prova)=>{
    return Prova.updateOne({_id:id},Prova)
                .then(dados=>{
                    return dados
                }
                )
                .catch(erro=>{
                   return erro
                })
}

module.exports.deleteProva = id =>{
    return Prova.deleteOne({_id:id})
                .then(dados=>{
                    return dados
                }
                )
                .catch(erro=>{
                   return erro
                })
}
