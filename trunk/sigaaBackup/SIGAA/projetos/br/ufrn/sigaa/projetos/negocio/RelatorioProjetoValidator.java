/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Validator com m�todos respons�veis pela valida��o das opera��es realizadas com
 * relat�rios parciais e finais de projetos.
 * 
 * @author geyson
 * @throws ArqException
 * @throws DAOException
 * 
 */
public class RelatorioProjetoValidator {
	
	/**
	 * Verifica se j� tem um relat�rio cadastrado do tipo informado
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
			lista.addErro("A a��o de extens�o selecionada j� possui "
					+ dao.findByPrimaryKey(tipoRelatorio,
							TipoRelatorioExtensao.class).getDescricao()
							+ " cadastrado.");
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
	public static void validaDadosGeraisRelatorioProjeto(
			RelatorioAcaoAssociada relatorio, ListaMensagens lista)
	throws DAOException, ArqException {

		ValidatorUtil.validateRequired(relatorio.getProjeto(), "Projeto", lista);
		ValidatorUtil.validaData(Formatador.getInstance().formatarData(relatorio.getDataCadastro()), "Data Cadastro",	lista);
		ValidatorUtil.validateRequired(relatorio.getRelacaoPropostaCurso(), "Informa��es relacionadas � Proposta Pedag�gica", lista);
		ValidatorUtil.validateRequired(relatorio.getAtividadesRealizadas(), "Atividades Realizadas", lista);
		ValidatorUtil.validateRequired(relatorio.getResultadosQualitativos(), "Resultados Qualitativos", lista);
		ValidatorUtil.validateRequired(relatorio.getResultadosQuantitativos(), "Resultados Quantitativos", lista);
		ValidatorUtil.validateRequired(relatorio.getDificuldadesEncontradas(), "Dificuldades Encontradas", lista);
		ValidatorUtil.validateRequired(relatorio.getAjustesDuranteExecucao(), "Ajustes Durante a Execu��o da A��o", lista);
		ValidatorUtil.validateRequired(relatorio.getPublicoRealAtingido(), "P�blico Real Atingido", lista);

	}


}
