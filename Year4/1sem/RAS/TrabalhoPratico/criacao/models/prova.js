const mongoose = require('mongoose')

var optionsSchema = new mongoose.Schema({
    _id : String,
    texto: String,
    cotacao : String,
    resolucao: String,
})


var questaoSchema = new mongoose.Schema({
    _id : String,
    enunciado : String,
    imagem : String,
    cotacaoTotal : String,
    tipo_Questao : String,
    options : [optionsSchema],
})


var criaProvaSchema = new mongoose.Schema({
    _id : String,
    uc : String,
    curso : String,
    cotacao : String,
    nome: String,
    id_docente : String,
    versao : Number,
    data : String,
    hora_preferencial : String,
    tempo_admissao : Number,
    duracao : Number,
    alunos : [String],
    acesso_autorizado : [String],
    questoes : [questaoSchema]
},
{
    versionKey: false
})


module.exports = mongoose.model('prova', criaProvaSchema)