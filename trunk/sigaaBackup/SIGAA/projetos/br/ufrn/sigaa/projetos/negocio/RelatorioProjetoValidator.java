/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/01/2011
 *
 */
package br.ufrn.sigaa.projetos.negocio;

import java.util.Collection;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.RelatorioAcaoAssociadaDao;
import br.ufrn.sigaa.extensao.relatorio.dominio.TipoRelatorioExtensao;
import br.ufrn.sigaa.projetos.dominio.RelatorioAcaoAssociada;


/**
 * Validator com métodos responsáveis pela validação das operações realizadas com
 * relatórios parciais e finais de projetos.
 * 
 * @author geyson
 * @throws ArqException
 * @throws DAOException
 * 
 */
public class RelatorioProjetoValidator {
	
	/**
	 * Verifica se já tem um relatório cadastrado do tipo informado
	 * 
	 * @param idProjeto
	 * @param tipoRelatorio
	 * @param lista
	 * @throws DAOException
	 * @throws ArqException
	 */
	public static void validaNovoRelatorio(Integer idProjeto,
			Integer tipoRelatorio, ListaMensagens lista)
	throws DAOException, ArqException {

		RelatorioAcaoAssociadaDao dao = DAOFactory.getInstance().getDAO(RelatorioAcaoAssociadaDao.class);
		Collection<RelatorioAcaoAssociada> relatorios = dao.findByProjetoTipoRelatorio(idProjeto, tipoRelatorio);

		if ((relatorios != null) && (!relatorios.isEmpty())) {
			lista.addErro("A ação de extensão selecionada já possui "
					+ dao.findByPrimaryKey(tipoRelatorio,
							TipoRelatorioExtensao.class).getDescricao()
							+ " cadastrado.");
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
	public static void validaDadosGeraisRelatorioProjeto(
			RelatorioAcaoAssociada relatorio, ListaMensagens lista)
	throws DAOException, ArqException {

		ValidatorUtil.validateRequired(relatorio.getProjeto(), "Projeto", lista);
		ValidatorUtil.validaData(Formatador.getInstance().formatarData(relatorio.getDataCadastro()), "Data Cadastro",	lista);
		ValidatorUtil.validateRequired(relatorio.getRelacaoPropostaCurso(), "Informações relacionadas à Proposta Pedagógica", lista);
		ValidatorUtil.validateRequired(relatorio.getAtividadesRealizadas(), "Atividades Realizadas", lista);
		ValidatorUtil.validateRequired(relatorio.getResultadosQualitativos(), "Resultados Qualitativos", lista);
		ValidatorUtil.validateRequired(relatorio.getResultadosQuantitativos(), "Resultados Quantitativos", lista);
		ValidatorUtil.validateRequired(relatorio.getDificuldadesEncontradas(), "Dificuldades Encontradas", lista);
		ValidatorUtil.validateRequired(relatorio.getAjustesDuranteExecucao(), "Ajustes Durante a Execução da Ação", lista);
		ValidatorUtil.validateRequired(relatorio.getPublicoRealAtingido(), "Público Real Atingido", lista);

	}


}
