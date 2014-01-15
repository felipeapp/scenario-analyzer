package br.ufrn.sigaa.questionario.negocio;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.extensao.dao.QuestionarioProjetoExtensaoDao;
import br.ufrn.sigaa.questionario.dominio.Questionario;

public class QuestionarioValidation {

	public static void validacaoSolicitacaoBolsa(Questionario questionario, ListaMensagens lista) throws DAOException {
		if ( questionario.isAcaoExtensao() ) {
			QuestionarioProjetoExtensaoDao dao = DAOFactory.getInstance().getDAO(QuestionarioProjetoExtensaoDao.class);
			try {
				boolean questionarioTemResposta = dao.haRespostaQuestionario(questionario);
				if ( questionarioTemResposta) {
					lista.addErro("Não é possível alterar o questionário '" + questionario.getTitulo() + "' pois o mesmo já apresenta resposta(s) cadastrada(s).");
				}
			} finally {
				dao.close();
			}
		}
	}	
}