package br.ufrn.sigaa.extensao.negocio;

import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioAcaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioBolsistaExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.AndamentoAtividade;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioAcaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioBolsistaExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioCursoEvento;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioProjetoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.TipoRelatorioExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;

/**
 * Classe com métodos responsáveis pela validação das operações realizadas com
 * relatórios parciais e finais de extensão.
 * 
 * @author ilueny santos
 * @throws ArqException
 * @throws DAOException
 * 
 */
public class RelatorioExtensaoValidator {

	/**
	 * Verifica se já tem um relatório cadastrado do tipo informado
	 * 
	 * @param idAtividade
	 * @param tipoRelatorio
	 * @param lista
	 * @throws DAOException
	 * @throws ArqException
	 */
	public static void validaNovoRelatorioBolsista(Integer idDiscenteExtensao,
			Integer tipoRelatorio, ListaMensagens lista) throws DAOException, ArqException {

		RelatorioBolsistaExtensaoDao dao = DAOFactory.getInstance().getDAO(RelatorioBolsistaExtensaoDao.class);

		/*
		 * Um plano de trabalho pode ter mais de um relatório do mesmo tipo uma
		 * vez que um plano pode ter mais de um discentes associado a ele.
		 *  
		 * TODO: ver com a PROEX qual seria a regra de validação mais apropriada para
		 * este caso.
		 * 
		 */
		Collection<RelatorioBolsistaExtensao> relatorios = dao.findByDiscenteExtensaoTipoRelatorio(idDiscenteExtensao, tipoRelatorio);

		//um discente pode ter vários relatórios parciais, mas somente 1 relatório final
		if ((relatorios != null) && (!relatorios.isEmpty()) && (tipoRelatorio == TipoRelatorioExtensao.RELATORIO_FINAL)) {
			lista.addErro("A ação de extensão selecionada já possui "
					+ dao.findByPrimaryKey(tipoRelatorio,
							TipoRelatorioExtensao.class).getDescricao()
							+ " cadastrado.");
		}

		//evitando cadastro de relatórios por discentes inativos em projetos inativos
		DiscenteExtensao discente = dao.findByPrimaryKey(idDiscenteExtensao,DiscenteExtensao.class);
		if ( (discente == null) || (!discente.isAtivo()) || 
				(discente.getAtividade() == null) || (!discente.getAtividade().isAtivo()) 
				|| (!discente.getAtividade().isValida()) )
			lista.addErro("A discente ou ação de extensão selecionada não estão ativos.");

	}

	/**
	 * Verifica se já tem um relatório cadastrado do tipo informado
	 * 
	 * @param idAtividade
	 * @param tipoRelatorio
	 * @param lista
	 * @throws DAOException
	 * @throws ArqException
	 */
	public static void validaNovoRelatorio(Integer idAtividade,
			Integer tipoRelatorio, ListaMensagens lista)
	throws DAOException, ArqException {

		RelatorioAcaoExtensaoDao dao = DAOFactory.getInstance().getDAO(RelatorioAcaoExtensaoDao.class);
		try{
			Collection<RelatorioAcaoExtensao> relatorios = dao.findByAtividadeTipoRelatorio(idAtividade, tipoRelatorio);

			if ((relatorios != null) && (!relatorios.isEmpty())) {
				lista.addErro("A ação de extensão selecionada já possui "
						+ dao.findByPrimaryKey(tipoRelatorio,
								TipoRelatorioExtensao.class).getDescricao()
								+ " cadastrado.");
			}
		}finally{
			dao.close();
		}
	}

	/**
	 * Valida dados gerais de para o cadastro de relatórios parciais e finais de
	 * projetos de extensão.
	 * 
	 * @param relatorio Relatório para validação
	 * @param lista Lista de erros reportados
	 * @throws DAOException
	 * @throws ArqException
	 */
	public static void validaDadosGeraisRelatorioAcaoExtensao(RelatorioAcaoExtensao relatorio, ListaMensagens lista) throws DAOException, ArqException {

		Date dateLimite = ParametroHelper.getInstance()
				.getParametroDate(ParametrosExtensao.DATA_LIMITE_ALTERACAO_CH_EQUIPE_EXECUTORA);

		if ( relatorio.getAtividade().getProjeto().getDataCadastro().before(dateLimite) ) {
			validaDadosGeraisRelatorio(relatorio, lista);		
		} else {
			validaDadosRelatorioExtensao(relatorio, lista);
		}
		
	}


	/**
	 * Valida dados gerais de para o cadastro de relatórios parciais e finais de
	 * cursos e eventos de extensão.
	 * 
	 * @param relatorio Relatório para validar
	 * @param lista Lista de erros reportados
	 * @throws DAOException
	 * @throws ArqException
	 */
	public static void validaDadosGeraisRelatorioCursoEvento(RelatorioCursoEvento relatorio, ListaMensagens lista) throws DAOException, ArqException {

		validaDadosGeraisRelatorio(relatorio, lista);

		if ((relatorio.getAtividade() == null)
				|| ((relatorio.getAtividade().getTipoAtividadeExtensao().getId() != TipoAtividadeExtensao.CURSO) 
						&& (relatorio.getAtividade().getTipoAtividadeExtensao().getId() != TipoAtividadeExtensao.EVENTO))) {
			lista.addErro("Selecione um Curso ou Evento de Extensão");
		}

	}

	/**
	 * Valida dados gerais de para o cadastro de relatórios parciais e finais de extensão.
	 * 
	 * @param relatorio
	 * @param lista
	 */
	public static void validaDadosGeraisRelatorio(	RelatorioAcaoExtensao relatorio, ListaMensagens lista) throws DAOException, ArqException {
		
		ValidatorUtil.validateRequired(relatorio.getAcaoRealizada(), "Esta Ação foi realizada", lista);
		ValidatorUtil.validateRequired(relatorio.getAtividade(), "Ação de Extensão", lista);
		ValidatorUtil.validaData(Formatador.getInstance().formatarData(relatorio.getDataCadastro()), "Data Cadastro", lista);
		ValidatorUtil.validateRequired(relatorio.getAtividadesRealizadas(), "Atividades Realizadas", lista);
		ValidatorUtil.validateRequired(relatorio.getResultadosQualitativos(), "Resultados Qualitativos", lista);
		ValidatorUtil.validateRequired(relatorio.getResultadosQuantitativos(), "Resultados Quantitativos", lista);
		ValidatorUtil.validateRequired(relatorio.getDificuldadesEncontradas(), "Dificuldades Encontradas", lista);
		ValidatorUtil.validateRequired(relatorio.getAjustesDuranteExecucao(), "Ajustes Durante a Execução da Ação", lista);
		ValidatorUtil.validateMinValue(relatorio.getPublicoRealAtingido(), 1, "Público Real Atingido", lista);
		
		if (relatorio.getAcaoRealizada() != null && !relatorio.getAcaoRealizada()) {
			ValidatorUtil.validateRequired(relatorio.getMotivoAcaoNaoRealizada(), "Motivo da não realização desta ação", lista);
		}
	}

	/**
	 * Valida dados gerais de para o cadastro dos novos relatórios parciais e finais de extensão.
	 * 
	 * @param relatorio
	 * @param lista
	 */
	public static void validaDadosRelatorioExtensao ( RelatorioAcaoExtensao relatorio, ListaMensagens lista) throws DAOException, ArqException {
		
		ValidatorUtil.validateRequired(relatorio.getAcaoRealizada(), "Esta Ação foi realizada", lista);
		
		if ( relatorio.getAndamento() != null ) {
			for (AndamentoAtividade andamento : relatorio.getAndamento()) {
				
				if ( andamento.getStatusAtividade() > 0 || andamento.getAndamentoAtividade() > 0) {
					
					if ( andamento.getStatusAtividade() == 2 && andamento.getAndamentoAtividade() == 0 ) {
						lista.addErro("A atividade " + andamento.getAtividade().getDescricao() + " está concluída com "+ andamento.getAndamentoAtividade() +"%");
					}
				
					if ( andamento.getStatusAtividade() != 3 && andamento.getAndamentoAtividade() == 0 ) {
						lista.addErro("A atividade " + andamento.getAtividade().getDescricao() + " está com "+ andamento.getAndamentoAtividade() +"% e não está cancelada.");
					}
	
					if ( andamento.getAndamentoAtividade() > 0 && andamento.getStatusAtividade() == 0  ) {
						lista.addErro("É necessário atualizar a situação da atividade " + andamento.getAtividade().getDescricao() + 
								", pois a mesma já apresenta andamento cadastrado.");
					}
	
				}
				
			}
		}
		
		ValidatorUtil.validateRequired(relatorio.getPublicoRealAtingido(), "Público Real Atingido", lista);
		ValidatorUtil.validateRequired(relatorio.getApresentacaoEventoCientifico(), "Apresentação em Eventos Científicos", lista);
		ValidatorUtil.validateRequired(relatorio.getArtigosEventoCientifico(), "Artigos Científicos produzidos a partir da ação de extensão", lista);
		ValidatorUtil.validateRequired(relatorio.getProducoesCientifico(), "Outras produções geradas a partir da ação de Extensão", lista);
		ValidatorUtil.validateRequired(relatorio.getDificuldadesEncontradas(), "Dificuldades Encontradas", lista);
		ValidatorUtil.validateRequired(relatorio.getObservacoesGerais(), "Observações Gerais", lista);
		
		if (relatorio.getAcaoRealizada() != null && !relatorio.getAcaoRealizada()) {
			ValidatorUtil.validateRequired(relatorio.getMotivoAcaoNaoRealizada(), "Motivo da não realização desta ação", lista);
		}
		
	}

}