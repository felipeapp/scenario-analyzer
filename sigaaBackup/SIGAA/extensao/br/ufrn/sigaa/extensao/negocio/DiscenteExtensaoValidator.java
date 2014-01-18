package br.ufrn.sigaa.extensao.negocio;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.extensao.InscricaoSelecaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioBolsistaExtensaoDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoSelecaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.TipoRelatorioExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;

public class DiscenteExtensaoValidator {

	/**
	 * Valida a inscrição do discente em uma ação de extensão
	 *
	 * @param discente
	 * @param listaSelecao
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaInscricaoSelecao(DiscenteAdapter discente, AtividadeExtensao atividade, ListaMensagens lista) throws ArqException {


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


		/** verificando se o discente já se inscreveu para a seleção desta ação antes */
		InscricaoSelecaoExtensaoDao dao = DAOFactory.getInstance().getDAO(InscricaoSelecaoExtensaoDao.class);
		InscricaoSelecaoExtensao inscricao = dao.findByDiscenteAtividade( discente.getId(), atividade.getId());

		if (inscricao != null && inscricao.getId() != 0) {
			lista.addErro("Você já realizou a inscrição para esta ação de extensão, portanto não pode realizar novamente.");
		}

		//verifica se o aluno tem capacidade técnica de participar da seleção
		validaCapacidadeTecnicaDiscente(discente, atividade, lista);		

	}


	/**
	 * TODO adaptar ao processo seletivo de extensão
	 * 
	 * Valida a capacidade técnica do discente candidato a bolsa da ação de extensão.
	 * verifica se o discente já cursou as disciplinas obrigatórias do projeto e se o 
	 * projeto não possui disciplinas obrigatórias, verifica se o aluno, em todas as disciplinas
	 * do projeto que ele tenha pago, tem média igual ou superior a 7
	 * 
	 * @param discente
	 * @param projeto
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaCapacidadeTecnicaDiscente(DiscenteAdapter discente, AtividadeExtensao atividade, ListaMensagens lista) throws ArqException {		

	}

	/**
	 * Valida a emissão de certificado do discente de extensão.
	 * 
	 * @param discenteExtensao
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaEmissaoCertificado(DiscenteExtensao discenteExtensao, ListaMensagens lista) throws ArqException {		
		if ( ParametroHelper.getInstance().getParametroBoolean(ParametrosExtensao.NECESSARIO_ENVIAR_RELATORIO_FINAL) ) {
			RelatorioBolsistaExtensaoDao dao = DAOFactory.getInstance().getDAO(RelatorioBolsistaExtensaoDao.class);
			try {
				boolean enviouRelatorio  = dao.isDiscenteEnviouRelatorio(discenteExtensao.getId(), TipoRelatorioExtensao.RELATORIO_FINAL);
				if (!enviouRelatorio) {
					lista.addErro("O certificado não pode ser emitido porque o Relatório Final do discente não foi enviado." +
					"Cadastre o relatório solicitado e tente realizar esta operação novamente.");
				}
	
				if (!discenteExtensao.isPassivelEmissaoCertificado()) {
					lista.addErro("O certificado não pode ser emitido porque a Ação de Extensão ainda não foi concluída.");
				}
	
			}finally{
				dao.close();
			}
		}
	}

}