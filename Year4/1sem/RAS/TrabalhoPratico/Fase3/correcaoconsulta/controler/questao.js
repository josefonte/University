const QuestaoModel = require('../models/questao');

module.exports.getAllQuestoes = () => {
  return QuestaoModel.getAllQuestoes();
};

module.exports.getQuestaoById = (id) => {
  return QuestaoModel.getQuestaoById(id);
};

module.exports.createQuestao = (questao) => {
  return QuestaoModel.createQuestao(questao);
};

module.exports.updateQuestao = (id, updatedQuestao) => {
  return QuestaoModel.updateQuestao(id, updatedQuestao);
};

module.exports.deleteQuestao = (id) => {
  return QuestaoModel.deleteQuestao(id);
};
