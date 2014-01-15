/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 06/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioEmprestimosPorTipoEmprestimoDao;

/**
 * <p>MBean que gerencia o relat�rio de empr�stimos por tipo de empr�stimo</p>
 * 
 * <pre>
 * Par�metros de entrada:
 * Filtros:
 * Biblioteca (podendo escolher todas);
 * Tipo de empr�stimo;
 * Per�odo in�cio e fim;
 * 
 * Sa�da:
 * Quantitativos de empr�stimos, separados por TURNO.
 * 
 * PS: lembrar de colocar os totais gerais;
 * 
 * </pre>
 * 
 * @author jadson
 *
 */
@Component("relatorioEmprestimosPorTipoEmprestimoMBean")
@Scope("request")
public class RelatorioEmprestimosPorTipoEmprestimoMBean extends AbstractRelatorioBibliotecaMBean {
	
	/**
	 * A p�gina do relat�rio propriamente dito, onde os dados s�o mostrados
	 */
	public static final String PAGINA_RELATORIO_SINTETICO  = "/biblioteca/controle_estatistico/relatorioEmprestimosPorTipoEmprestimoSintetico.jsp";
	
	/**
	 * A p�gina do relat�rio propriamente dito, onde os dados s�o mostrados
	 */
	public static final String PAGINA_RELATORIO_ANALITICO  = "/biblioteca/controle_estatistico/relatorioEmprestimosPorTipoEmprestimoAnalitico.jsp";
	
	/**
	 * Quantidade m�xima de registros por p�gina do relat�rio
	 */
	private static final int QUANTIDADE_REGISTROS_POR_PAGINA = 500;
	
	/**
	 * O resultado da consulta.
	 */
	private List<Object> resultado = new ArrayList<Object>();
	
	
	/**
	 *  A totaliza��o do relat�rio por biblioteca e turno
	 *  
	 *  chave : idBiblioteca
	 *  valor: 
	 *           chave:  idTurno;
	 *           valor: total por turno e biblioteca; 
	 */
	private Map<String, TreeMap<String, Long>> totalizacaoPorTurno; 
	
	/**
	 *   A totaliza��o do relat�rio por biblioteca e tipo de emprestimo
	 *  
	 *  chave : idBiblioteca
	 *  valor: 
	 *           chave:  idTipoEmprestimo;
	 *           valor: total por tipo empr�stimos e biblioteca; 
	 *   
	 */
	private Map<String, TreeMap<String, Long>> totalizacaoPorTipoEmprestimo;
	
	
	
	/**
	 * Totaliza��o utilizada penas caso n�o se agrupe por turno
	 * 
	 * chave: descri��o tipo de empr�stimo
	 * valor: quantidade por tipo de empr�timos
	 */
	private Map<String, Long> totalPorTipoEmpretimo;
	
	
	/**
	 * Construtor obrigat�rio para que usa o abstract relat�rio da biblioteca.
	 */
	public RelatorioEmprestimosPorTipoEmprestimoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	
	@Override
	public void configurar() {
		setTitulo("Relat�rio de Empr�stimos por Tipo de Empr�stimo");
		setDescricao("<p> Nesta p�gina � poss�vel emitir o relat�rio quantitativo de empr�stimos de um determinado tipo realizados dentro de um per�odo. O resultado desse relat�rio pode ser agrupado por <strong>turno</strong>. </p> "
				+" <p> <strong>Observa��o 1:</strong> A quantidade reflete a <strong> quantidade de empr�stimos mais a quantidade de renova��es </strong>.    </p> "
				+" <p> <strong>Observa��o 2:</strong> Para o per�odo do relat�rio � considerada a data em que o empr�stimo foi realizado. </p>"
				+" <p> <strong>Observa��o 3:</strong> Caso se escolha agrupar os resultados por turno, s� ser� contabilizado a quantidade de renova��es presenciais, para verificar a utiliza��o do setor de circula��o da biblioteca. </p>"
				+" <p> <strong>Observa��o 4:</strong> Devido � grande carga de dados envolvida, o relat�rio anal�tico � restrito a um per�odo de 30 dias. </p>");
		
		setFiltradoPorVariasBibliotecas(true);
		setCampoBibliotecaObrigatorio(false);
		
		setFiltradoPorTipoDeEmprestimo(true);
		setFiltradoPorPeriodo(true);
		
		setFiltradoPorBooleano(true);
		setDescricaoDadoBooleano("Agrupar por turno ?");
		dadoBooleano = false;
		
		filtradoPorFormaRelatorio = true;
		
		inicioPeriodo = CalendarUtils.adicionaMeses(new Date(), -1);
		
	}
	
	/**
	 * Chamado, indiretamente, por <em>sigaa.war/biblioteca/controle_estatistico/formGeral.jsp</em>
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		
		resultado = new ArrayList<Object>();
		totalizacaoPorTipoEmprestimo = new TreeMap<String, TreeMap<String, Long>>(); 
		totalizacaoPorTurno = new TreeMap<String, TreeMap<String, Long>>(); 
		totalPorTipoEmpretimo = new TreeMap<String, Long>(); 
		
		RelatorioEmprestimosPorTipoEmprestimoDao dao = getDAO( RelatorioEmprestimosPorTipoEmprestimoDao.class);
		
		configuraDaoRelatorio(dao);
		
		if (formatoRelatorio == SINTETICO) {
			/*
			 * [0] quantidade
			 * [1] id do tipo de empr�stimo
			 * [2] descria��o do tipo de empr�stimos
			 * [3] id da biblioteca
			 * [4] descri��o da biblioteca
			 * [5] turno  (apenas se for agrupado por turno)
			 * 
			 */
			resultado = dao.countEmprestimosPorTipoDeEmprestimoSintetico(inicioPeriodo, fimPeriodo, tipoDeEmprestimo, UFRNUtils.toInteger(variasBibliotecas) , dadoBooleano );
			setUtilizandoPaginacao(false);
			super.gerarDescricaoDosParametros();
		} else {
			if (CalendarUtils.calculoDias(inicioPeriodo, fimPeriodo) > 30) {
				addMensagemErro("O relat�rio anal�tico deve ser limitado a um per�odo de 30 dias.");
				
				return null;
			}
			
			// S� � poss�vel configurar aqui depois que o usu�rio escolheu o tipo anal�tico.
			utilizandoPaginacao = true;
			quantidadeRegistrosPorPagina = QUANTIDADE_REGISTROS_POR_PAGINA;
			
			numeroTotalDeRegistros = dao.countEmprestimosPorTipoDeEmprestimoAnalitico(
					inicioPeriodo, fimPeriodo, 
					tipoDeEmprestimo, UFRNUtils.toInteger(variasBibliotecas), 
					dadoBooleano);
			
			calculaValoresPaginacao();
			
			super.gerarDescricaoDosParametros();
			
			/*
			 * [0] quantidade
			 * [1] id do tipo de empr�stimo
			 * [2] descria��o do tipo de empr�stimos
			 * [3] id da biblioteca
			 * [4] descri��o da biblioteca
			 * [5] c�digo de barras do material
			 * [6] usu�rio que realizou o empr�stimo
			 * [7] usu�rio que recebeu o empr�stimo
			 * [8] turno  (apenas se for agrupado por turno)
			 * 
			 */
			resultado = dao.findEmprestimosPorTipoDeEmprestimoAnalitico(
					inicioPeriodo, fimPeriodo, 
					tipoDeEmprestimo, UFRNUtils.toInteger(variasBibliotecas), 
					dadoBooleano, paginaAtual, quantidadeRegistrosPorPagina);
		}
		
		if(resultado.size() == 0 ){
			addMensagemWarning("N�o foram encontrados resultados para o per�odo informado ");
			return null;
		}
		
		if (formatoRelatorio == SINTETICO) {
			/*
			 * Totaliza os resultados
			 */
			for (Object object : resultado) {
				
				Object[] objectArray = (Object[]) object;
				
				String descricaoBiblioteca = (String) objectArray[4];
				String descricaoTipoEmprestimo = (String) objectArray[2];
				long qtdTemp = ((BigDecimal)objectArray[0]).longValue();
				
				
				/* *******************************************************
				 *  TOTALIZA��O POR TURNO E TIPO DE EMPR�STIMO
				 * ********************************************************/
				if(dadoBooleano){  
					
					int turnoTemp = formatoRelatorio == SINTETICO ? (Integer) objectArray[5] : (Integer) objectArray[8];
					String descricaoturno = turnoTemp == getTurnoManha() ? "Manh�" : ( turnoTemp == getTurnoTarde() ? "Tarde" : (turnoTemp == getTurnoNoite() ?  "Noite" : "a" ) );
									
					if(totalizacaoPorTurno.containsKey(descricaoBiblioteca)){ // se j� tem a biblioteca 
					
						Map<String, Long> temp = totalizacaoPorTurno.get(descricaoBiblioteca);
						
						if(temp.containsKey(descricaoturno)){
							long qtdTurnoTemp = temp.get(descricaoturno);
							qtdTurnoTemp += qtdTemp;
							temp.put(descricaoturno, qtdTurnoTemp);
							
						}else{ // Se ainda n�o tem o turno dentro da biblioteca 
							temp.put(descricaoturno, qtdTemp);
						}
						
					}else{  // Se ainda n�o tem a biblioteca
						TreeMap<String, Long> temp = new TreeMap<String, Long>();
						temp.put(descricaoturno, qtdTemp);
						totalizacaoPorTurno.put(descricaoBiblioteca, temp);
					}
					
					if(totalizacaoPorTipoEmprestimo.containsKey(descricaoBiblioteca)){ // se j� tem a biblioteca 
						
						Map<String, Long> temp = totalizacaoPorTipoEmprestimo.get(descricaoBiblioteca);
						
						if(temp.containsKey(descricaoTipoEmprestimo)){
							long qtdTipoEmprestimoTemp = temp.get(descricaoTipoEmprestimo);
							qtdTipoEmprestimoTemp += qtdTemp;
							temp.put(descricaoTipoEmprestimo, qtdTipoEmprestimoTemp);
							
						}else{ // Se ainda n�o tem o turno dentro da biblioteca 
							temp.put(descricaoTipoEmprestimo, qtdTemp);
						}
						
					}else{  // Se ainda n�o tem a biblioteca
						TreeMap<String, Long> temp = new TreeMap<String, Long>();
						temp.put(descricaoTipoEmprestimo, qtdTemp);
						totalizacaoPorTipoEmprestimo.put(descricaoBiblioteca, temp);
					}
					
				}else{
					
					/* *******************************************************
					 *  TOTALIZA��O POR TIPO DE EMPR�STIMO
					 * ********************************************************/
					
					if(totalPorTipoEmpretimo.containsKey(descricaoTipoEmprestimo)){
					
						long qtdTipoEmprestimoTemp = totalPorTipoEmpretimo.get(descricaoTipoEmprestimo);
						qtdTipoEmprestimoTemp += qtdTemp;
						totalPorTipoEmpretimo.put(descricaoTipoEmprestimo, qtdTipoEmprestimoTemp);
						
					}else{
						totalPorTipoEmpretimo.put(descricaoTipoEmprestimo, qtdTemp);
					}
					
				}
				
			}
		
			/*
			 * Map ordenado por valores
			 */
			Map<String, Long> totalPorTipoEmpretimoOrdenado = new TreeMap<String, Long>(new MapaPorValorComparator(totalPorTipoEmpretimo, true));
			
			/*
			 * Copia os dados do map ordenado por chaves para map ordenados por valores
			 */
			for (Iterator<String> iter = totalPorTipoEmpretimo.keySet().iterator(); iter.hasNext();) {  
			    String key = iter.next();  
			    totalPorTipoEmpretimoOrdenado.put(key, totalPorTipoEmpretimo.get(key));  
			}
			
			totalPorTipoEmpretimo = totalPorTipoEmpretimoOrdenado;
			
			
			
			for (String descricaoBiblioteca : totalizacaoPorTipoEmprestimo.keySet()) {
			
				Map<String, Long> tempNaoOrdenado = totalizacaoPorTipoEmprestimo.get( descricaoBiblioteca);
				
				/*
				 * Map ordenado por valores
				 */
				TreeMap<String, Long> tempOrdenado = new TreeMap<String, Long>(new MapaPorValorComparator(tempNaoOrdenado, true));
				
				/*
				 * Copia os dados do map ordenado por chaves para map ordenados por valores
				 */
				for (Iterator<String> iter = tempNaoOrdenado.keySet().iterator(); iter.hasNext();) {  
				    String key = iter.next();  
				    tempOrdenado.put(key, tempNaoOrdenado.get(key));  
				}
				
				totalizacaoPorTipoEmprestimo.put(descricaoBiblioteca, tempOrdenado);
				
			}
			
			for (String descricaoBiblioteca : totalizacaoPorTurno.keySet()) {
				
				Map<String, Long> tempNaoOrdenado = totalizacaoPorTurno.get( descricaoBiblioteca);
				
				/*
				 * Map ordenado por valores
				 */
				TreeMap<String, Long> tempOrdenado = new TreeMap<String, Long>(new MapaPorValorComparator(tempNaoOrdenado, true));
				
				/*
				 * Copia os dados do map ordenado por chaves para map ordenados por valores
				 */
				for (Iterator<String> iter = tempNaoOrdenado.keySet().iterator(); iter.hasNext();) {  
				    String key = iter.next();  
				    tempOrdenado.put(key, tempNaoOrdenado.get(key));  
				}
				
				totalizacaoPorTurno.put(descricaoBiblioteca, tempOrdenado);
				
			}
		}
		
		if (formatoRelatorio == SINTETICO) {
			return forward(PAGINA_RELATORIO_SINTETICO);
		} else {
			return forward(PAGINA_RELATORIO_ANALITICO);
		}
	}


	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/controle_estatistico/relatorioEmprestimosPorTipoEmprestimoAnalitico.jsp</li>
	 *   </ul>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarProximosResultadosConsultaPaginada()
	 */
	@Override
	public String gerarProximosResultadosConsultaPaginada() throws DAOException {
		calculaValoresPaginacao();
		super.gerarDescricaoDosParametros();
		
		utilizandoPaginacao = true;
		quantidadeRegistrosPorPagina = QUANTIDADE_REGISTROS_POR_PAGINA;
		
		RelatorioEmprestimosPorTipoEmprestimoDao dao = null;
		
		try{
			dao = getDAO(RelatorioEmprestimosPorTipoEmprestimoDao.class);

			resultado = dao.findEmprestimosPorTipoDeEmprestimoAnalitico(
					inicioPeriodo, fimPeriodo, 
					tipoDeEmprestimo, UFRNUtils.toInteger(variasBibliotecas), 
					dadoBooleano, paginaAtual, quantidadeRegistrosPorPagina);
	
		}finally{
			if(dao != null) dao.close();
		}
		
		return forward(PAGINA_RELATORIO_ANALITICO);
	}
	

	
	/**
	 * <p>Compra um map pelo valor </p>
	 * 
	 * @author jadson
	 *
	 */
    public class MapaPorValorComparator implements java.util.Comparator<String> {  
    	/**
    	 * o mapa com os valores
    	 */
        private Map<String, Long> m = null; // the original map 
        /**
         * Indica se o sentido de ordena��o � decrescente ou n�o.
         */
        private boolean ordemDecrescente = false;
        
        
        public MapaPorValorComparator(Map<String, Long> m, boolean ordemDecrescente) {  
            this.m = m;  
            this.ordemDecrescente = ordemDecrescente;
        }  
      
        /**
         * Compara o map interna pelo valor <br/>
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(String chave1, String chave2) {  
        	Long valor1 = m.get(chave1);  
        	Long valor2 = m.get(chave2);  
            
        	// Se forem valores iguais, a compara��o retorna diferen�a de 1 (necess�rio para impedir que o map descarte o novo elementro)
        	if (valor1 == valor2) {
        		return ordemDecrescente ? -1 : 1;
        	} else {
        		return ordemDecrescente ? - valor1.compareTo(valor2) : valor1.compareTo(valor2);
        	}
        }
      
    }   
    
    
	
	
	///////////      sets e gets     //////////

	
	public List<Object> getResultado() {
		return resultado;
	}

	public void setResultado(List<Object> resultado) {
		this.resultado = resultado;
	}


	
	public Map<String, TreeMap<String, Long>> getTotalizacaoPorTurno() {
		return totalizacaoPorTurno;
	}


	public void setTotalizacaoPorTurno(	Map<String, TreeMap<String, Long>> totalizacaoPorTurno) {
		this.totalizacaoPorTurno = totalizacaoPorTurno;
	}


	public Map<String, TreeMap<String, Long>> getTotalizacaoPorTipoEmprestimo() {
		return totalizacaoPorTipoEmprestimo;
	}


	public void setTotalizacaoPorTipoEmprestimo(Map<String, TreeMap<String, Long>> totalizacaoPorTipoEmprestimo) {
		this.totalizacaoPorTipoEmprestimo = totalizacaoPorTipoEmprestimo;
	}

	

	public Map<String, Long> getTotalPorTipoEmpretimo() {
		return totalPorTipoEmpretimo;
	}


	public void setTotalPorTipoEmpretimo(Map<String, Long> totalPorTipoEmpretimo) {
		this.totalPorTipoEmpretimo = totalPorTipoEmpretimo;
	}


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
