/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 08/09/2010
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioConsultasLocaisPorAnoDAO;

/**
 * MBean que cuida do relat�rio de consultas locais por ano.
 *
 * @author Br�ulio
 */
@Component("relatorioConsultasLocaisPorAnoMBean")
@Scope("request")
public class RelatorioConsultasLocaisPorAnoMBean extends AbstractRelatorioBibliotecaMBean {

	/**
	 * A p�gina do relat�rio
	 */
	private static final String PAGINA_DO_RELATORIO = "/biblioteca/controle_estatistico/relatorioConsultasLocaisPorAno.jsp";
	
	/**
	 * Os resultados do relat�rio
	 */
	private Map<Integer, Map<String, Integer>> resultados;

	/**
	 * Guarda a totaliza��o por m�s do relat�rio
	 */
	private Map<Integer, Integer> totalPorMes;
	/**
	 * Guarda a totaliza��o por classe do relat�rio
	 */
	private Map<String, Integer> totalPorClasse;
	
	/**
	 * Guarda a totaliza��o geral do relat�rio
	 */
	private int totalGeral;
	
	public RelatorioConsultasLocaisPorAnoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/*
	 * Chamado, indiretamente, pela seguinte JSP:
	 * sigaa.war/biblioteca/controle_estatistico/controle_estatistico.jsp
	 */
	@Override
	public void configurar() {
		titulo = "Relat�rio de Consultas Locais por Ano";
		descricao = "Este relat�rio lista as consultas locais realizadas a materiais em uma biblioteca durante um ano. </p>"
			+"<p> <strong> Consultas locais s�o as consultas realizadas nas estantes das bibliotecas e que s�o registradas posteriormente no sistema. </strong>";
		
		filtradoPorVariasBibliotecas = true;
		filtradoPorAno = true;
		filtradoPorTipoClassificacao = true;
	}

	/*
	 * Chamado, indiretamente, pela seguinte JSP:
	 * sigaa.war/biblioteca/controle_estatistico/formGeral.jsp
	 */
	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		RelatorioConsultasLocaisPorAnoDAO dao = null;

		try{
			dao = getDAO(RelatorioConsultasLocaisPorAnoDAO.class);
			
			resultados = dao.findConsultas( UFRNUtils.toInteger(variasBibliotecas),
					ano, classificacaoEscolhida);
			
			totalPorMes = CollectionUtils.somaIntsAgrupandoPorS(resultados);
			totalPorClasse = CollectionUtils.somaIntsAgrupandoPorT(resultados);
			totalGeral = CollectionUtils.somaInts( totalPorMes.values() );
			
			return forward(PAGINA_DO_RELATORIO);
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	// GETs e SETs

	public Map<Integer, Map<String, Integer>> getResultados() { return resultados; }
	public Map<Integer, Integer> getTotalPorMes() { return totalPorMes; }
	public Map<String, Integer> getTotalPorClasse() { return totalPorClasse; }
	public int getTotalGeral() { return totalGeral; }
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		return null;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}

}
