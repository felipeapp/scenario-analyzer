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
 * Interface que define os c�lculos que s�o realizados no sistema da pesquisa.
 * Esta interface deve ser implementada por cada institui��o a fim de personalizar 
 * comportamentos espec�ficos do subsistema da pesquisa 
 * @author Victor Hugo
 *
 */
public interface CalculosPesquisa {

	/**
	 * Este m�todo cria a grade de distribui��o de cotas para a classifica��o passada no par�metro. 
	 * Esta grade � uma cole��o de CotaDocente que � criada, sendo uma entrada para cada docente com base da emiss�o do seu relat�rio de produtividade. 
	 * Associado a esta CotaDocente � criado ainda uma registro Cota para cada tipo de cota que � oferecido pelo edital em quest�o, por�m com a quantidade 
	 * de cotas zero para todas elas   
	 * @param classificacao
	 * @param edital
	 * @return
	 * @throws NegocioException 
	 * @throws DAOException
	 */
	public TreeSet<CotaDocente> criarGradeDistribuicaoCotas(ClassificacaoRelatorio classificacao, EditalPesquisa edital) throws DAOException, NegocioException;
	
	/**
	 * Este m�todo � respons�vel por distribuir as cotas autom�ticamente de acordo com o regulamento da institui��o.
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
	 * Calcula a m�dia do projeto 
	 * @param avaliacao
	 */
	//public void calcularMediaProjeto(AvaliacaoProjeto avaliacao);
	
	
	/**
	 * Calcula o FPPI de todos os docentes que est�o associados a classifica��o informada
	 * @param classificacaoRelatorio
	 */
	public void calcularFPPI(ClassificacaoRelatorio classificacaoRelatorio) throws ArqException;
	
}
