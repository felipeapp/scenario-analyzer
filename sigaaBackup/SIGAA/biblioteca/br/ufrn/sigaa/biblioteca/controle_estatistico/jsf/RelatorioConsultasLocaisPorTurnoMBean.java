/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em: 03/12/2009
 */

package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioConsultasLocaisPorTurnoDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosAuxiliaresRelatoriosMatriciais;
import br.ufrn.sigaa.biblioteca.util.FormataDadosRelatoriosUtils;

/**
 * Managed Bean respons�vel pela gera��o do relat�rio quantitativo de
 * consultas por classe CDU. 
 * 
 * @author Jean Guerethes
 */
@Component("relatorioConsultasLocaisPorTurnoMBean")
@Scope("request")
public class RelatorioConsultasLocaisPorTurnoMBean extends AbstractRelatorioBibliotecaMBean{

	/** A p�gina JSP do relat�rio. */
	private static final String PAGINA_DO_RELATORIO = "/biblioteca/controle_estatistico/relatorioConsultasLocaisPorTurno.jsp";

	/** Resultados do relat�rio com dois agrupamentos.
	 * 
	 *  Se o usu�rio n�o escolher o agrupamento cont�m:
	 *  	Turno, Classifica��o e o Object[] a quantidade
	 *  
	 *  Se o usu�rio escolher o agrupamento cont�m:
	 *  	Turno, Classifica��o e o Object[0] o agrupamento e Object[1] a quantidade
	 *  
	 */
	private List<DadosAuxiliaresRelatoriosMatriciais> resultados;
	
	/**
	 * Guarda a totaliza��o por classifica��o do relat�rio. (S� utilizado caso o usu�rio escolha um 3� agrupamento, sen�o os dados j� est�o totalizados)
	 */
	private Map<String, Long> totalPorClasse;
	
	/**
	 * Guarda a totaliza��o por agrupamento escolhido pelo usu�rio do relat�rio. (S� utilizado caso o usu�rio escolha um 3� agrupamento, sen�o os dados j� est�o totalizados)
	 */
	private Map<String, Long> totalPorAgrupamento;
	
	
	/**
	 * Guarda a totaliza��o geral do relat�rio
	 */
	private int totalGeral;
	
	
	public RelatorioConsultasLocaisPorTurnoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relat�rio.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo("Relat�rio de Consultas Locais por Turno");
		
		setDescricao("Este relat�rio mostra a quantidade de materiais consultados nas bibliotecas do sistema, " +
				"separados pela classe do material e pelo turno no qual ocorreu a consulta.</p>"
				+"<p> <strong> Consultas locais s�o as consultas realizadas nas estantes das bibliotecas e que s�o registradas posteriormente no sistema. </strong>");
		
		setFiltradoPorVariasBibliotecas(true);
		setFiltradoPorPeriodo(true);
		setFiltradoPorTiposDeMaterial(true);
		setFiltradoPorColecoes(true);
		setFiltradoPorTipoClassificacao(true);
		setFiltradoPorAgrupamento1(true);
		
		inicioPeriodo = CalendarUtils.adicionaMeses(inicioPeriodo, -1); // por padr�o recupera o �ltimo m�s
	}
	
	/**
	 * Chamado indiretamente por
	 * <ul>
	 *   <li>/sigaa.war/biblioteca/controle_estatistico/formGeral.jsp<li>
	 * </ul>
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		RelatorioConsultasLocaisPorTurnoDAO dao = null;
		
		try{
			dao = getDAO(RelatorioConsultasLocaisPorTurnoDAO.class);
			
			resultados = new ArrayList<DadosAuxiliaresRelatoriosMatriciais>();
			
			List<Object[]> resultadosTemp = dao.findConsultasLocaisRegistradasNoPeriodo(inicioPeriodo, fimPeriodo,
					UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial),
					classificacaoEscolhida, agrupamento1);
			
			/*
			 *  Se o usu�rio n�o escolheu agrupamento, ent�o os resultados j� est�o agrupados por apenas 2 agrupamentos
			 *  
			 *  Com isso retorna apenas 1 objeto cont�m a matriz de resultados
			 */
			if(agrupamento1 == AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO){
			
				resultados.add(FormataDadosRelatoriosUtils.formataMatrizDadosApartirDeLista("Turno", "Classifica��o", resultadosTemp) );
			
			}else{
				
				totalPorClasse = new TreeMap<String, Long>();
				totalPorAgrupamento = new TreeMap<String, Long>();
				
				
				resultados = FormataDadosRelatoriosUtils.formataMatrizDadosApartirDeLista("Turno", agrupamento1.alias, "Classifica��o", resultadosTemp);
				
				//// Contabiliza os totais das matrizes ///
				for (DadosAuxiliaresRelatoriosMatriciais dados : resultados) {
					
					Object[][] matriz = dados.getMatrizDeResultados();
					 int qtdCol = dados.getQtdColunasTotalMatriz();
					 int qtdLinhas= dados.getQtdLinhasTotalMatriz();
					
					/** Intera sobre as colunas da matriz, a partir da segunda, pois na primeira vem a descri��o da linhas */ 
				    for (int col=1; col < qtdCol-1; col++){
				        String classificacao = (String)matriz[0][col]; // na primeira linha da matriz v�m as classifica��es
				        Long total =  (Long) matriz[qtdLinhas-1][col]; // na �ltima linha v�s os totais
				        contabilizaTotalPorClassificacao(classificacao, total);
				    }
				}
				
				for (DadosAuxiliaresRelatoriosMatriciais dados : resultados) {
					
					Object[][] matriz = dados.getMatrizDeResultados();
					int qtdCol = dados.getQtdColunasTotalMatriz();
					int qtdLinhas= dados.getQtdLinhasTotalMatriz();
					
					/** Intera sobre as colunas da matriz, a partir da segunda, pois na primeira vem a descri��o da linhas */ 
				    for (int row=1; row < qtdLinhas-1; row++){
				        String agrupamento = (String)matriz[row][0]; // na primeira coluna da matriz v�m as classifica��es
				        Long total =  (Long) matriz[row][qtdCol-1]; // na �ltima coluna v�s os totais
				        contabilizaTotalPorAgrupamento(agrupamento, total);
				    }
				}
				
				
				contabilizaTotalGeral();
				
			}
			
			return forward(PAGINA_DO_RELATORIO);
			
		}finally{
			if(dao != null) dao.close();
		}
	}
	

	

	/**
	 * Totaliza a quantidade de consultas por classifica��o utilizada.
	 * 
	 * S� necess�rio se o usu�rio escolher o 3� agrupamento. 
	 *
	 * @param string
	 * @param integer
	 */
	private void contabilizaTotalPorClassificacao(String classificacao, Long quantidade) {
		
		if(totalPorClasse == null)
			totalPorClasse = new TreeMap<String, Long>();
		
		if(totalPorClasse.containsKey(classificacao)){
			Long totalAtual = totalPorClasse.get(classificacao);
			totalPorClasse.put(classificacao, totalAtual+quantidade);
		}else{
			totalPorClasse.put(classificacao, quantidade);
		}
	}

	
	/**
	 * Totaliza a quantidade de consultas por agrupamento utilizada.
	 * 
	 * S� necess�rio se o usu�rio escolher o 3� agrupamento. 
	 *
	 * @param string
	 * @param integer
	 */
	private void contabilizaTotalPorAgrupamento(String agrupamento, Long quantidade) {
		
		if(totalPorAgrupamento == null)
			totalPorAgrupamento = new TreeMap<String, Long>();
		
		if(totalPorAgrupamento.containsKey(agrupamento)){
			Long totalAtual = totalPorAgrupamento.get(agrupamento);
			totalPorAgrupamento.put(agrupamento, totalAtual+quantidade);
		}else{
			totalPorAgrupamento.put(agrupamento, quantidade);
		}
	}
	
	/**
	 * Totaliza a quantidade de consultas por classifica��o utilizada.
	 * 
	 * S� necess�rio se o usu�rio escolher o 3� agrupamento. 
	 *
	 * @param string
	 * @param integer
	 */
	private void contabilizaTotalGeral() {
		totalGeral = 0;
		for (String classificacao : totalPorClasse.keySet()) {
			totalGeral +=  totalPorClasse.get(classificacao);
		}
	}
	
	
	
	// GETs e SETs

	public List<DadosAuxiliaresRelatoriosMatriciais> getResultados() { return resultados; }
	public Map<String, Long> getTotalPorAgrupamento() {return totalPorAgrupamento;}
	public Map<String, Long> getTotalPorClasse() { return totalPorClasse; }
	public int getTotalGeral() { return totalGeral; }
	
	
	
	

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		Collection<SelectItem> temp = new ArrayList<SelectItem>();
		temp.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO.valor, AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO.alias));
		temp.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.COLECAO.valor, AgrupamentoRelatoriosBiblioteca.COLECAO.alias));
		temp.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.valor, AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.alias));
		return temp;
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
