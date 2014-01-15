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
 * Realiza valida��es relativas aos discentes de a��es de extens�o.
 * 
 * @author ilueny
 *
 */
public class DiscenteProjetoValidator {
	/**
	 * Valida a inscri��o do discente em uma a��o de extens�o
	 *
	 * @param discente
	 * @param listaSelecao
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaInscricaoSelecao(DiscenteAdapter discente, Projeto atividade, ListaMensagens lista) throws ArqException {

		//verificando se o discente est� concluinte ou afastado
		if(	(discente.getStatus() == StatusDiscente.CONCLUIDO)
				|| (discente.getStatus() == StatusDiscente.GRADUANDO)
				|| (discente.getStatus() == StatusDiscente.AFASTADO)
				|| (discente.getStatus() == StatusDiscente.CANCELADO)
				|| (discente.getStatus() == StatusDiscente.EXCLUIDO)
				|| (discente.getStatus() == StatusDiscente.JUBILADO)
				|| (discente.getStatus() == StatusDiscente.TRANCADO)
				|| (discente.getStatus() == StatusDiscente.CADASTRADO))
			lista.addErro("Discentes concluintes ou com algum tipo de afastamento n�o podem participar de a��es de extens�o.");

		/** verificando se o discente j� se inscreveu para a sele��o desta a��o associada antes */
		InscricaoSelecaoProjetoDao dao = DAOFactory.getInstance().getDAO(InscricaoSelecaoProjetoDao.class);
		try {
			InscricaoSelecaoProjeto inscricao = dao.findByDiscenteAtividade( discente.getId(), atividade.getId());

			if (inscricao != null && inscricao.getId() != 0) {
				lista.addErro("Voc� j� realizou a inscri��o para esta a��o associada, portanto n�o pode realizar novamente.");
			}
		} finally {
			dao.close();
		}
		
	}
}
