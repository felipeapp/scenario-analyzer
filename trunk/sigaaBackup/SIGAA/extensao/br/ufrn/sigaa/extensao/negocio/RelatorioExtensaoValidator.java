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
 * Classe com m�todos respons�veis pela valida��o das opera��es realizadas com
 * relat�rios parciais e finais de extens�o.
 * 
 * @author ilueny santos
 * @throws ArqException
 * @throws DAOException
 * 
 */
public class RelatorioExtensaoValidator {

	/**
	 * Verifica se j� tem um relat�rio cadastrado do tipo informado
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
		 * Um plano de trabalho pode ter mais de um relat�rio do mesmo tipo uma
		 * vez que um plano pode ter mais de um discentes associado a ele.
		 *  
		 * TODO: ver com a PROEX qual seria a regra de valida��o mais apropriada para
		 * este caso.
		 * 
		 */
		Collection<RelatorioBolsistaExtensao> relatorios = dao.findByDiscenteExtensaoTipoRelatorio(idDiscenteExtensao, tipoRelatorio);

		//um discente pode ter v�rios relat�rios parciais, mas somente 1 relat�rio final
		if ((relatorios != null) && (!relatorios.isEmpty()) && (tipoRelatorio == TipoRelatorioExtensao.RELATORIO_FINAL)) {
			lista.addErro("A a��o de extens�o selecionada j� possui "
					+ dao.findByPrimaryKey(tipoRelatorio,
							TipoRelatorioExtensao.class).getDescricao()
							+ " cadastrado.");
		}

		//evitando cadastro de relat�rios por discentes inativos em projetos inativos
		DiscenteExtensao discente = dao.findByPrimaryKey(idDiscenteExtensao,DiscenteExtensao.class);
		if ( (discente == null) || (!discente.isAtivo()) || 
				(discente.getAtividade() == null) || (!discente.getAtividade().isAtivo()) 
				|| (!discente.getAtividade().isValida()) )
			lista.addErro("A discente ou a��o de extens�o selecionada n�o est�o ativos.");

	}

	/**
	 * Verifica se j� tem um relat�rio cadastrado do tipo informado
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
				lista.addErro("A a��o de extens�o selecionada j� possui "
						+ dao.findByPrimaryKey(tipoRelatorio,
								TipoRelatorioExtensao.class).getDescricao()
								+ " cadastrado.");
			}
		}finally{
			dao.close();
		}
	}

	/**
	 * Valida dados gerais de para o cadastro de relat�rios parciais e finais de
	 * projetos de extens�o.
	 * 
	 * @param relatorio Relat�rio para valida��o
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
	 * Valida dados gerais de para o cadastro de relat�rios parciais e finais de
	 * cursos e eventos de extens�o.
	 * 
	 * @param relatorio Relat�rio para validar
	 * @param lista Lista de erros reportados
	 * @throws DAOException
	 * @throws ArqException
	 */
	public static void validaDadosGeraisRelatorioCursoEvento(RelatorioCursoEvento relatorio, ListaMensagens lista) throws DAOException, ArqException {

		validaDadosGeraisRelatorio(relatorio, lista);

		if ((relatorio.getAtividade() == null)
				|| ((relatorio.getAtividade().getTipoAtividadeExtensao().getId() != TipoAtividadeExtensao.CURSO) 
						&& (relatorio.getAtividade().getTipoAtividadeExtensao().getId() != TipoAtividadeExtensao.EVENTO))) {
			lista.addErro("Selecione um Curso ou Evento de Extens�o");
		}

	}

	/**
	 * Valida dados gerais de para o cadastro de relat�rios parciais e finais de extens�o.
	 * 
	 * @param relatorio
	 * @param lista
	 */
	public static void validaDadosGeraisRelatorio(	RelatorioAcaoExtensao relatorio, ListaMensagens lista) throws DAOException, ArqException {
		
		ValidatorUtil.validateRequired(relatorio.getAcaoRealizada(), "Esta A��o foi realizada", lista);
		ValidatorUtil.validateRequired(relatorio.getAtividade(), "A��o de Extens�o", lista);
		ValidatorUtil.validaData(Formatador.getInstance().formatarData(relatorio.getDataCadastro()), "Data Cadastro", lista);
		ValidatorUtil.validateRequired(relatorio.getAtividadesRealizadas(), "Atividades Realizadas", lista);
		ValidatorUtil.validateRequired(relatorio.getResultadosQualitativos(), "Resultados Qualitativos", lista);
		ValidatorUtil.validateRequired(relatorio.getResultadosQuantitativos(), "Resultados Quantitativos", lista);
		ValidatorUtil.validateRequired(relatorio.getDificuldadesEncontradas(), "Dificuldades Encontradas", lista);
		ValidatorUtil.validateRequired(relatorio.getAjustesDuranteExecucao(), "Ajustes Durante a Execu��o da A��o", lista);
		ValidatorUtil.validateMinValue(relatorio.getPublicoRealAtingido(), 1, "P�blico Real Atingido", lista);
		
		if (relatorio.getAcaoRealizada() != null && !relatorio.getAcaoRealizada()) {
			ValidatorUtil.validateRequired(relatorio.getMotivoAcaoNaoRealizada(), "Motivo da n�o realiza��o desta a��o", lista);
		}
	}

	/**
	 * Valida dados gerais de para o cadastro dos novos relat�rios parciais e finais de extens�o.
	 * 
	 * @param relatorio
	 * @param lista
	 */
	public static void validaDadosRelatorioExtensao ( RelatorioAcaoExtensao relatorio, ListaMensagens lista) throws DAOException, ArqException {
		
		ValidatorUtil.validateRequired(relatorio.getAcaoRealizada(), "Esta A��o foi realizada", lista);
		
		if ( relatorio.getAndamento() != null ) {
			for (AndamentoAtividade andamento : relatorio.getAndamento()) {
				
				if ( andamento.getStatusAtividade() > 0 || andamento.getAndamentoAtividade() > 0) {
					
					if ( andamento.getStatusAtividade() == 2 && andamento.getAndamentoAtividade() == 0 ) {
						lista.addErro("A atividade " + andamento.getAtividade().getDescricao() + " est� conclu�da com "+ andamento.getAndamentoAtividade() +"%");
					}
				
					if ( andamento.getStatusAtividade() != 3 && andamento.getAndamentoAtividade() == 0 ) {
						lista.addErro("A atividade " + andamento.getAtividade().getDescricao() + " est� com "+ andamento.getAndamentoAtividade() +"% e n�o est� cancelada.");
					}
	
					if ( andamento.getAndamentoAtividade() > 0 && andamento.getStatusAtividade() == 0  ) {
						lista.addErro("� necess�rio atualizar a situa��o da atividade " + andamento.getAtividade().getDescricao() + 
								", pois a mesma j� apresenta andamento cadastrado.");
					}
	
				}
				
			}
		}
		
		ValidatorUtil.validateRequired(relatorio.getPublicoRealAtingido(), "P�blico Real Atingido", lista);
		ValidatorUtil.validateRequired(relatorio.getApresentacaoEventoCientifico(), "Apresenta��o em Eventos Cient�ficos", lista);
		ValidatorUtil.validateRequired(relatorio.getArtigosEventoCientifico(), "Artigos Cient�ficos produzidos a partir da a��o de extens�o", lista);
		ValidatorUtil.validateRequired(relatorio.getProducoesCientifico(), "Outras produ��es geradas a partir da a��o de Extens�o", lista);
		ValidatorUtil.validateRequired(relatorio.getDificuldadesEncontradas(), "Dificuldades Encontradas", lista);
		ValidatorUtil.validateRequired(relatorio.getObservacoesGerais(), "Observa��es Gerais", lista);
		
		if (relatorio.getAcaoRealizada() != null && !relatorio.getAcaoRealizada()) {
			ValidatorUtil.validateRequired(relatorio.getMotivoAcaoNaoRealizada(), "Motivo da n�o realiza��o desta a��o", lista);
		}
		
	}

}