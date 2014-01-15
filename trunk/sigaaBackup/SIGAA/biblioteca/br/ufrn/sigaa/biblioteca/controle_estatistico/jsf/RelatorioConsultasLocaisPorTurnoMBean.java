/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Managed Bean responsável pela geração do relatório quantitativo de
 * consultas por classe CDU. 
 * 
 * @author Jean Guerethes
 */
@Component("relatorioConsultasLocaisPorTurnoMBean")
@Scope("request")
public class RelatorioConsultasLocaisPorTurnoMBean extends AbstractRelatorioBibliotecaMBean{

	/** A página JSP do relatório. */
	private static final String PAGINA_DO_RELATORIO = "/biblioteca/controle_estatistico/relatorioConsultasLocaisPorTurno.jsp";

	/** Resultados do relatório com dois agrupamentos.
	 * 
	 *  Se o usuário não escolher o agrupamento contém:
	 *  	Turno, Classificação e o Object[] a quantidade
	 *  
	 *  Se o usuário escolher o agrupamento contém:
	 *  	Turno, Classificação e o Object[0] o agrupamento e Object[1] a quantidade
	 *  
	 */
	private List<DadosAuxiliaresRelatoriosMatriciais> resultados;
	
	/**
	 * Guarda a totalização por classificação do relatório. (Só utilizado caso o usuário escolha um 3º agrupamento, senão os dados já estão totalizados)
	 */
	private Map<String, Long> totalPorClasse;
	
	/**
	 * Guarda a totalização por agrupamento escolhido pelo usuário do relatório. (Só utilizado caso o usuário escolha um 3º agrupamento, senão os dados já estão totalizados)
	 */
	private Map<String, Long> totalPorAgrupamento;
	
	
	/**
	 * Guarda a totalização geral do relatório
	 */
	private int totalGeral;
	
	
	public RelatorioConsultasLocaisPorTurnoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relatório.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo("Relatório de Consultas Locais por Turno");
		
		setDescricao("Este relatório mostra a quantidade de materiais consultados nas bibliotecas do sistema, " +
				"separados pela classe do material e pelo turno no qual ocorreu a consulta.</p>"
				+"<p> <strong> Consultas locais são as consultas realizadas nas estantes das bibliotecas e que são registradas posteriormente no sistema. </strong>");
		
		setFiltradoPorVariasBibliotecas(true);
		setFiltradoPorPeriodo(true);
		setFiltradoPorTiposDeMaterial(true);
		setFiltradoPorColecoes(true);
		setFiltradoPorTipoClassificacao(true);
		setFiltradoPorAgrupamento1(true);
		
		inicioPeriodo = CalendarUtils.adicionaMeses(inicioPeriodo, -1); // por padrão recupera o último mês
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
			 *  Se o usuário não escolheu agrupamento, então os resultados já estão agrupados por apenas 2 agrupamentos
			 *  
			 *  Com isso retorna apenas 1 objeto contém a matriz de resultados
			 */
			if(agrupamento1 == AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO){
			
				resultados.add(FormataDadosRelatoriosUtils.formataMatrizDadosApartirDeLista("Turno", "Classificação", resultadosTemp) );
			
			}else{
				
				totalPorClasse = new TreeMap<String, Long>();
				totalPorAgrupamento = new TreeMap<String, Long>();
				
				
				resultados = FormataDadosRelatoriosUtils.formataMatrizDadosApartirDeLista("Turno", agrupamento1.alias, "Classificação", resultadosTemp);
				
				//// Contabiliza os totais das matrizes ///
				for (DadosAuxiliaresRelatoriosMatriciais dados : resultados) {
					
					Object[][] matriz = dados.getMatrizDeResultados();
					 int qtdCol = dados.getQtdColunasTotalMatriz();
					 int qtdLinhas= dados.getQtdLinhasTotalMatriz();
					
					/** Intera sobre as colunas da matriz, a partir da segunda, pois na primeira vem a descrição da linhas */ 
				    for (int col=1; col < qtdCol-1; col++){
				        String classificacao = (String)matriz[0][col]; // na primeira linha da matriz vêm as classificações
				        Long total =  (Long) matriz[qtdLinhas-1][col]; // na última linha vês os totais
				        contabilizaTotalPorClassificacao(classificacao, total);
				    }
				}
				
				for (DadosAuxiliaresRelatoriosMatriciais dados : resultados) {
					
					Object[][] matriz = dados.getMatrizDeResultados();
					int qtdCol = dados.getQtdColunasTotalMatriz();
					int qtdLinhas= dados.getQtdLinhasTotalMatriz();
					
					/** Intera sobre as colunas da matriz, a partir da segunda, pois na primeira vem a descrição da linhas */ 
				    for (int row=1; row < qtdLinhas-1; row++){
				        String agrupamento = (String)matriz[row][0]; // na primeira coluna da matriz vêm as classificações
				        Long total =  (Long) matriz[row][qtdCol-1]; // na última coluna vês os totais
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
	 * Totaliza a quantidade de consultas por classificação utilizada.
	 * 
	 * Só necessário se o usuário escolher o 3º agrupamento. 
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
	 * Só necessário se o usuário escolher o 3º agrupamento. 
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
	 * Totaliza a quantidade de consultas por classificação utilizada.
	 * 
	 * Só necessário se o usuário escolher o 3º agrupamento. 
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
	 * Ver comentários da classe pai.<br/>
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
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}

}
