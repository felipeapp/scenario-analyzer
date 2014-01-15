/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 21/09/2010
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioCrescimentoPeriodicosPorClasseDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorAgrupamento;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorMes;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosRelatorioCrescimentoPorClassificacao;

/**
 * Bean que gerencia o relat�rio de crescimento dos peri�dicos por classe CDU ou Black.
 *
 * @author Br�ulio
 */
@Component("relatorioCrescimentoPeriodicosPorClasseMBean")
@Scope("request")
public class RelatorioCrescimentoPeriodicosPorClasseMBean extends AbstractRelatorioBibliotecaMBean {
	

	/** Relat�rio num per�odo dado. */
	private static final String PAGINA_RELATORIO_PERIODO = "/biblioteca/controle_estatistico/relatorioPeriodicosClasseCrescimentoPeriodo.jsp";
	
	/**
	 * O resultado do relat�rio retornado pela consultas para o crescimento do T�tulos. Usam a data de cadastro dos Titulos no acervo
	 */
	private List<DadosRelatorioCrescimentoPorClassificacao> resultadoCrescimentoTitulo;
	
	/**
	 * O resultado do relat�rio retornado pela consultas para o crescimento do Exemplares ou Fasc�culos. Usam a data de cadastro dos materiais no acervo.
	 */
	private List<DadosRelatorioCrescimentoPorClassificacao> resultadoCrescimentoMaterial;

	/**
	 * Guarda a quantidade anterior de T�tulos separados e ordenados pela classifica��o
	 */
	private Map<String, Long> quantidadeAnteriorTitulo = new TreeMap<String, Long>();
	
	/**
	 * Guarda a quantidade anterior de Materiais separados e ordenados pela classifica��o
	 */
	private Map<String, Long> quantidadeAnteriorMateriais = new TreeMap<String, Long>();

	/** Resultados do relat�rio: antes. */
	private Map<String, Long> antes;
	/** Resultados do relat�rio: depois. */
	private Map<String, Long> depois;
	/** Classes inclu�das no relat�rio. */
	private Set<String> classes;
	
	public RelatorioCrescimentoPeriodicosPorClasseMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	@Override
	public void configurar() {
		titulo = "Relat�rio de Crescimento dos Peri�dicos por Classifica��o";
		descricao = "Este relat�rio lista o crescimento dos peri�dicos " +
				"m�s a m�s, agrupados pela classifica��o bibliogr�fica escolhida."+
		"<br/>"+
		"<p><strong>Observa��o 1:</strong> Selecionando uma biblioteca espec�fica, a data de inclus�o do T�tulo no acervo ser� considerada a data de cadastro do primeiro material do T�tulo na biblioteca escolhida.</p>"+
		"<p> <strong> <i> Relat�rios de crescimento verificam tamb�m os materiais baixados do acervo, se j� existiram algum dia no acervo "+ 
		" tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois. </strong> </i> </p>";

		
		filtradoPorVariasBibliotecas = true;
		filtradoPorTipoClassificacao = true;
		filtradoPorTipoDeTombamento = true;		
		filtradoPorPeriodo = true;
		
		permitirTodosTiposDeTombamento = true;

		campoBibliotecaObrigatorio = false;
		
		/* 
		 * Caso o usu�rio queira ver a quantidade que existia antes no acervo. 
		 * Tinha x e cresceu y.  Por padr�o mostra apenas a quantidade do crescimento 
		 */
		filtradoPorBooleano = true;
		dadoBooleano = false;
		descricaoDadoBooleano = "Mostrar a quantidade anterior ? ";
		
		inicioPeriodo = CalendarUtils.adicionaMeses(inicioPeriodo, -6); // por padr�o recupera o crescimento dos �ltimos 6 meses
	}

	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO);

		RelatorioCrescimentoPeriodicosPorClasseDAO dao = getDAO(RelatorioCrescimentoPeriodicosPorClasseDAO.class);
		super.configuraDaoRelatorio(dao); // fecha o dao depois de relat�rio automaticamente.
		
		resultadoCrescimentoTitulo = dao.findCrescimentoTitulosByPeriodo(
				UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
				tipoDeTombamento, classificacaoEscolhida);
		
		
		resultadoCrescimentoMaterial = dao.findCrescimentoFasciculosByPeriodo(
				UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
				tipoDeTombamento, classificacaoEscolhida);
		
		 /*
		  *  Se o usu�rio quer ver a quantidade que tinha antes, precisar fazer uma nova consulta e contar tudo 
		  *  antes do per�odo escolhido. <br/>
		  *  O relat�rio fica bem mais lento. Por isso o padr�o � mostrar apenas o crescimento.
		  */
		if(dadoBooleano){
			
			quantidadeAnteriorTitulo = dao.findQuantidadeAnteriorTitulosByPeriodo(
					UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
					tipoDeTombamento, classificacaoEscolhida);
			
			
			quantidadeAnteriorMateriais = dao.findQuantidadeAnteriorFasciculosByPeriodo(
					UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
					tipoDeTombamento, classificacaoEscolhida);

			antes = quantidadeAnteriorMateriais;
			depois = new TreeMap<String, Long>();
			
			depois.putAll(antes);
			
			/* *************************************************************************************
			 *  Realiza o somat�rio de todas as clases com a quantidade anterior existente         *
			 * *************************************************************************************/
			
			if(quantidadeAnteriorTitulo.size() > 0){
				Map<String, Long> quantidadeAnteriorTituloTemp = new TreeMap<String, Long>();
				quantidadeAnteriorTituloTemp.putAll(quantidadeAnteriorTitulo);
				
				for (DadosRelatorioCrescimentoPorClassificacao ano : resultadoCrescimentoTitulo) {
					
					ano.setContandoResultadosAnteriores();
					
					for (DadosInternosRelatorioCrescimentoPorMes mes : ano.getDadosInternos()) {
						
						mes.setContandoResultadosAnteriores();
						
						for (DadosInternosRelatorioCrescimentoPorAgrupamento agruapamento : mes.getDadosInternos()) {
							for (DadosInternosRelatorioCrescimentoPorAgrupamento.DadosInternos classificacao : agruapamento.getDadosInternos()) {
								
								Long quantidadeAnterior = quantidadeAnteriorTituloTemp.get(classificacao.getClassificacao());
								
								if(quantidadeAnterior == null)
									quantidadeAnterior = 0l;
								
								classificacao.setQuantidadeAnterior(quantidadeAnterior);
								quantidadeAnteriorTituloTemp.put(classificacao.getClassificacao(), (quantidadeAnterior + classificacao.getQuantidade()) );
								
							}
						}
					}
				}

			}
			
			if(quantidadeAnteriorMateriais.size() > 0){
				Map<String, Long> quantidadeAnteriorMateriaisTemp = new TreeMap<String, Long>();
				quantidadeAnteriorMateriaisTemp.putAll(quantidadeAnteriorMateriais);
				
				for (DadosRelatorioCrescimentoPorClassificacao ano : resultadoCrescimentoMaterial) {
					
					ano.setContandoResultadosAnteriores();
					
					for (DadosInternosRelatorioCrescimentoPorMes mes : ano.getDadosInternos()) {
						
						mes.setContandoResultadosAnteriores();
						
						for (DadosInternosRelatorioCrescimentoPorAgrupamento agruapamento : mes.getDadosInternos()) {
							for (DadosInternosRelatorioCrescimentoPorAgrupamento.DadosInternos classificacao : agruapamento.getDadosInternos()) {
								
								Long quantidadeAnterior = quantidadeAnteriorMateriaisTemp.get(classificacao.getClassificacao());
								
								if(quantidadeAnterior == null)
									quantidadeAnterior = 0l;
								
								classificacao.setQuantidadeAnterior(quantidadeAnterior);
								quantidadeAnteriorMateriaisTemp.put(classificacao.getClassificacao(), (quantidadeAnterior + classificacao.getQuantidade()) );
								depois.put(classificacao.getClassificacao(), (depois.get(classificacao.getClassificacao()) != null ? depois.get(classificacao.getClassificacao()) : 0) + classificacao.getQuantidade() );								
							}
						}
					}
				}
			}
		} else {
			antes = new TreeMap<String, Long>();
			depois = new TreeMap<String, Long>();
			
			for (DadosRelatorioCrescimentoPorClassificacao ano : resultadoCrescimentoMaterial) {				
				for (DadosInternosRelatorioCrescimentoPorMes mes : ano.getDadosInternos()) {					
					for (DadosInternosRelatorioCrescimentoPorAgrupamento agruapamento : mes.getDadosInternos()) {
						for (DadosInternosRelatorioCrescimentoPorAgrupamento.DadosInternos classificacao : agruapamento.getDadosInternos()) {
							depois.put(classificacao.getClassificacao(), (depois.get(classificacao.getClassificacao()) != null ? depois.get(classificacao.getClassificacao()) : 0) + classificacao.getQuantidade() );								
						}
					}
				}
			}
		}
		
		/**
		 * Realiza a totaliza��o por classifica��o, somente no final depois que adicionar os dados anteriores. 
		 */
		for (DadosRelatorioCrescimentoPorClassificacao ano : resultadoCrescimentoMaterial) {
			for (DadosInternosRelatorioCrescimentoPorMes mes : ano.getDadosInternos()) {
				mes.totalizaPorClassificacao();
			}
		}
		
		classes = new TreeSet<String>();
		
		classes.addAll(antes.keySet());
		classes.addAll(depois.keySet());

		return forward(PAGINA_RELATORIO_PERIODO);
	}

	
	/**
     * Gera um gr�fico simples com o crescimento ocorrido em cadas m�s do relat�rio.
     * 
     * 
	 * @throws IOException 
     */
    public String getGerarGraficoCrescimento() throws IOException {
    	
    	
    	TimeSeriesCollection dataset = new TimeSeriesCollection();
    	
    	TimeSeries serie = new TimeSeries("Fasc�culos Adicionados por M�s ao Acervo");
    	
    	for (DadosRelatorioCrescimentoPorClassificacao resultadoPorAno : resultadoCrescimentoMaterial) {	
    		
    		for (DadosInternosRelatorioCrescimentoPorMes resultadoPorMes : resultadoPorAno.getDadosInternos()) {
				long totalMes = 0l;
				for(String classificacao : resultadoPorMes.getTotaisPorClasificacao().keySet()){	
					totalMes += resultadoPorMes.getTotaisPorClasificacao().get(classificacao);
				}
				serie.add( new Month(resultadoPorMes.getMes(), resultadoPorAno.getAno()), totalMes);
			}
		}
    	
    	dataset.addSeries(serie);
    	
    	JFreeChart chart = org.jfree.chart.ChartFactory.createTimeSeriesChart("Varia��o do Crescimento de Fasc�culos Mensalmente",
    			"Meses",
    			"Quantidade de Fasc�culos",
    			dataset,
    			true,
    			true,
    			false);
    	
    	String nomeArquivoTemp = ServletUtilities.saveChartAsPNG(chart, 900, 500, getCurrentSession());
		
		return nomeArquivoTemp;
        
    }
	
	// Gets e Sets

	public Map<String, Long> getAntes() { return antes; }
	public Map<String, Long> getDepois() { return depois; }
	public Set<String> getClasses() { return classes; }

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

	public List<DadosRelatorioCrescimentoPorClassificacao> getResultadoCrescimentoTitulo() {
		return resultadoCrescimentoTitulo;
	}

	public List<DadosRelatorioCrescimentoPorClassificacao> getResultadoCrescimentoMaterial() {
		return resultadoCrescimentoMaterial;
	}

	public Map<String, Long> getQuantidadeAnteriorTitulo() {
		return quantidadeAnteriorTitulo;
	}

	public Map<String, Long> getQuantidadeAnteriorMateriais() {
		return quantidadeAnteriorMateriais;
	}
	
}
