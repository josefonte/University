var ProvaDuplicada = require('../models/prova')

module.exports.list = () => {
    return ProvaDuplicada.find()
            .then(resposta => {
                return resposta
            })
            .catch(erro => {
                return erro
            })
}

module.exports.getById = (id_prova) => {
    return ProvaDuplicada.find({_id:id_prova})
            .then(resposta => {
                return resposta
            })
            .catch(erro => {
                return erro
            })
}

module.exports.addProva = p => {
    return ProvaDuplicada.create(p)
            .then(resposta => {
                return resposta
            })
            .catch(erro => {
                return erro
            })
}

module.exports.updateProva = p => {
    return ProvaDuplicada.updateOne({_id:p._id}, p)
            .then(resposta => {
                return resposta
            })
            .catch(erro => {
                return erro
            })
}

module.exports.deleteProva = id => {
    return ProvaDuplicada.deleteOne({_id:id})
            .then(resposta => {
                return resposta
            })
            .catch(erro => {
                return erro
            })
}

module.exports.updateRespostasInQuestao = (id_prova,id_questao,respostas) => {
    return ProvaDuplicada.updateOne({ 
                            _id: id_prova
                        },
                        { $set: { [`respostas.${id_questao}`]: respostas }}
                        )
            .then(resposta => {
                return resposta
            })
            .catch(erro => {
                return erro
            })
}
