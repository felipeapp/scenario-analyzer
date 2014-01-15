package br.ufrn.sigaa.pesquisa.negocio;

import java.util.TreeSet;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.integracao.interfaces.FormacaoAcademicaRemoteService;
import br.ufrn.sigaa.pesquisa.dominio.CotaDocente;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ClassificacaoRelatorio;
import br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorio;

/**
 * Interface que define os cálculos que são realizados no sistema da pesquisa.
 * Esta interface deve ser implementada por cada instituição a fim de personalizar 
 * comportamentos específicos do subsistema da pesquisa 
 * @author Victor Hugo
 *
 */
public interface CalculosPesquisa {

	/**
	 * Este método cria a grade de distribuição de cotas para a classificação passada no parâmetro. 
	 * Esta grade é uma coleção de CotaDocente que é criada, sendo uma entrada para cada docente com base da emissão do seu relatório de produtividade. 
	 * Associado a esta CotaDocente é criado ainda uma registro Cota para cada tipo de cota que é oferecido pelo edital em questão, porém com a quantidade 
	 * de cotas zero para todas elas   
	 * @param classificacao
	 * @param edital
	 * @return
	 * @throws NegocioException 
	 * @throws DAOException
	 */
	public TreeSet<CotaDocente> criarGradeDistribuicaoCotas(ClassificacaoRelatorio classificacao, EditalPesquisa edital) throws DAOException, NegocioException;
	
	/**
	 * Este método é responsável por distribuir as cotas automáticamente de acordo com o regulamento da instituição.
	 * @param grade
	 * @param edital
	 * @throws DAOException
	 */
	public void distribuirCotas(TreeSet<CotaDocente> grade, EditalPesquisa edital) throws DAOException;
	
	/**
	 * Calcula o IPI do docente
	 * @param emissaoRelatorio
	 * @throws ArqException 
	 */
	public void calcularIpiDocente(EmissaoRelatorio emissaoRelatorio, FormacaoAcademicaRemoteService serviceFormacao) throws ArqException;
	
	/**
	 * Calcula a média do projeto 
	 * @param avaliacao
	 */
	//public void calcularMediaProjeto(AvaliacaoProjeto avaliacao);
	
	
	/**
	 * Calcula o FPPI de todos os docentes que estão associados a classificação informada
	 * @param classificacaoRelatorio
	 */
	public void calcularFPPI(ClassificacaoRelatorio classificacaoRelatorio) throws ArqException;
	
}
