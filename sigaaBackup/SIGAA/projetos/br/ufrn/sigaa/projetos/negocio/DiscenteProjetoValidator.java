package br.ufrn.sigaa.projetos.negocio;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.projetos.InscricaoSelecaoProjetoDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.projetos.dominio.InscricaoSelecaoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Realiza validações relativas aos discentes de ações de extensão.
 * 
 * @author ilueny
 *
 */
public class DiscenteProjetoValidator {
	/**
	 * Valida a inscrição do discente em uma ação de extensão
	 *
	 * @param discente
	 * @param listaSelecao
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaInscricaoSelecao(DiscenteAdapter discente, Projeto atividade, ListaMensagens lista) throws ArqException {

		//verificando se o discente está concluinte ou afastado
		if(	(discente.getStatus() == StatusDiscente.CONCLUIDO)
				|| (discente.getStatus() == StatusDiscente.GRADUANDO)
				|| (discente.getStatus() == StatusDiscente.AFASTADO)
				|| (discente.getStatus() == StatusDiscente.CANCELADO)
				|| (discente.getStatus() == StatusDiscente.EXCLUIDO)
				|| (discente.getStatus() == StatusDiscente.JUBILADO)
				|| (discente.getStatus() == StatusDiscente.TRANCADO)
				|| (discente.getStatus() == StatusDiscente.CADASTRADO))
			lista.addErro("Discentes concluintes ou com algum tipo de afastamento não podem participar de ações de extensão.");

		/** verificando se o discente já se inscreveu para a seleção desta ação associada antes */
		InscricaoSelecaoProjetoDao dao = DAOFactory.getInstance().getDAO(InscricaoSelecaoProjetoDao.class);
		try {
			InscricaoSelecaoProjeto inscricao = dao.findByDiscenteAtividade( discente.getId(), atividade.getId());

			if (inscricao != null && inscricao.getId() != 0) {
				lista.addErro("Você já realizou a inscrição para esta ação associada, portanto não pode realizar novamente.");
			}
		} finally {
			dao.close();
		}
		
	}
}
