/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 12/08/2010
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioCrescimentoPorClassificacaoDao;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosAuxiliaresRelatoriosMatriciais;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorAgrupamento;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorMes;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosRelatorioCrescimentoPorClassificacao;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.util.FormataDadosRelatoriosUtils;

/**
 * <p>MBean que gerencia a criação do relatório de crescimento de coleções por classe CDU ou Black.</p>
 *
 * @author Bráulio
 * @version 2.0 - Jadson - 16/06/2011 -: Adicionando os novos filtros. Juntando com o antigo relatorio de crescimento 
 * por tipo de material. já que a diferença era apenas em por qual informação os resulatados eram agrupados.
 * Separando o crescimento de dois grupos. Uma coisa é o crescimento de Títulos, outra o de materiais, antes mostra os
 * títulos cujos materiais tiveram crescimento. Mostrando a quantidade anteriormente mostrada. Mostrando um gráfico simples do crescimento de cada mes.
 * 
 * @version 3.0 - Jadson - 16/07/2012: O relatíorio vai virar dois relatórios diferentes. <br/> 
 * <strong>Básico : </strong> que vai agrupar apenas por Tipo de Material, Coleção e Classificação. <br/>
 * <strong>Detalhado: </strong> que vai ser o relatório que existia anteriormente. <br/>
 * 
 * 
 */
@Component("relatorioCrescimentoPorClassificacaoMBean")
@Scope("request")
public class RelatorioCrescimentoPorClassificacaoMBean extends AbstractRelatorioBibliotecaMBean {
	
	/**
	 * A página do relatório
	 */
	private static final String PAGINA_RELATORIO = "/biblioteca/controle_estatistico/relatorioCrescimentoPorClassificacao.jsp";
	
	/**
	 * A página do relatório básico
	 */
	private static final String PAGINA_RELATORIO_BASICO = "/biblioteca/controle_estatistico/relatorioCrescimentoPorClassificacaoBasico.jsp";
	
	/**
	 * O resultado do relatório retornado pela consultas para o crescimento dos Títulos. 
	 * Usam a data de cadastro dos Titulos no acervo.
	 */
	private List<DadosRelatorioCrescimentoPorClassificacao> resultadoCrescimentoTitulo;
	
	/**
	 * O resultado do relatório retornado pela consultas para o crescimento dos Títulos com agrupamento. 
	 * Usam a data de cadastro dos Titulos no acervo.
	 */
	private List<DadosRelatorioCrescimentoPorClassificacao> resultadoCrescimentoTituloAgrupado;
	
	/**
	 * O resultado do relatório retornado pela consultas para o crescimento do Exemplares ou Fascículos. Usam a data de cadastro dos materiais no acervo.
	 */
	private List<DadosRelatorioCrescimentoPorClassificacao> resultadoCrescimentoMaterial;
	
	
	/////////////// Resultados para o relatório básico  //////////////////////////////
	
	/**  Guarda o crescimento dos materiais para o caso de relatório basico. */
	private List<Object[]> resultadosTitulosBasico;
	
	/**  Guarda o crescimento dos materiais para o caso de relatório basico. */
	private List<DadosAuxiliaresRelatoriosMatriciais> resultadosTitulosAgrupadoBasico;
	
	/**  Guarda o crescimento dos materiais para o caso de relatório basico. */
	private List<DadosAuxiliaresRelatoriosMatriciais> resultadosMateriaisBasico;
	
	/** O total geral de Títulos para o relatório basico */
	private BigInteger totalGeralTitulos;
	
	/** O total geral de Títulos agrupados para o relatório basico */
	private BigInteger totalGeralTitulosAgrupados;
	
	/** O total geral de Materiais agrupados para o relatório basico */
	private BigInteger totalGeralMateriais;
	
	//////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	/**
	 * Guarda a quantidade anterior de Títulos separados e ordenados pela classificação
	 */
	private Map<String, Long> quantidadeAnteriorTitulo = new TreeMap<String, Long>();

	
	/**
	 * Guarda a quantidade anterior de Materiais separados e ordenados pela classificação
	 */
	private Map<String, Long> quantidadeAnteriorMateriais = new TreeMap<String, Long>();

	
	
	/** Resultados do relatório: antes. */
	private Map<String, Long> antes;
	/** Resultados do relatório: depois. */
	private Map<String, Long> depois;
	/** Classes incluídas no relatório. */
	private Set<String> classes;
	
	public RelatorioCrescimentoPorClassificacaoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	@Override
	public void configurar() {
		setTitulo("Relatório de Crescimento por Classificação Bibliográfica");
		setDescricao(
				"<p>Este relatório lista o crescimento ocorrido do acervo dentro do período selecionado. São mostrados os crescimentos de Títulos e Exemplares ou Fascículos.<br/> " +
				"<p>O crescimento conta a quantidade de Título e Materiais que foram criados no período informado. </p>"+
				"<p>Escolhendo-se a opção de \"mostrar a quantidade anterior\" é visualizado a quantidade " +
				" de Títulos e Materiais existentes antes do período em que ocorreu o crescimento, somando-se com a quantidade criada no período informado. Para relatório básico essa opção é desconsiderada. </p>"+
				"<p>Selecionando-se uma biblioteca específica, a data de inclusão do Título no acervo será considerada a data de cadastro do <strong>primeiro</strong> material do Título na biblioteca escolhida.</p>"+
				"<br/><br/>"+
				"<p>No relatório detalhado os dados são agrupados pelo período de crescimento (ano/mês), pela classificação escolhida e por um dos agrupamentos selecionados nos filtros do relatório. </p>"+
				"<p>No relatório básico os dados são agrupados pela coleção, tipo de material e pela classificação. </p>"+
				"<br/>"+
				"<p> <strong> <i> Relatórios de crescimento verificam também os materiais baixados do acervo, se já existiram algum dia no acervo "+ 
				" também devem ser contados, senão pode gerar erros ao emitir o mesmo relatório tempos depois. </strong> </i> </p>");
		
		filtradoPorVariasBibliotecas = true;
		setCampoBibliotecaObrigatorio(false);
		filtradoPorPeriodo = true;
		filtradoPorColecoes = true;
		filtradoPorTiposDeMaterial = true;
		filtradoPorSituacoesDoMaterial = true;
		
		filtradoPorTipoClassificacao = true;
		filtradoPorFormasDocumento = true;
		
		filtradoPorAgrupamento1 = true;
		
		filtradoPorCtgMaterial = true;
		permitirTodasCtgMaterial = false;
		
		/* 
		 * Caso o usuário queira ver a quantidade que existia antes no acervo. 
		 * Tinha x e cresceu y.  Por padrão mostra apenas a quantidade do crescimento 
		 */
		filtradoPorBooleano = true;
		dadoBooleano = false;
		descricaoDadoBooleano = "Mostrar a quantidade anterior ? ";
		
		/* O básico vai ser o relatório agrupado apenas por coleção, tipo de material e CDU. 
		 * O detalhado vai ser o grupamento atual, por ano, mês, tipo de material ou coleção e classificação. */
		filtradoPorBasicoVsDetalhado = true;
		
		inicioPeriodo = CalendarUtils.adicionaMeses(inicioPeriodo, -6); // por padrão recupera o crescimento dos últimos 6 meses
		
		ctgMaterial = CTGMAT_EXEMPLARES; // Configura a catalogaria padrão que vem selecionado
	}
	
	
	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		RelatorioCrescimentoPorClassificacaoDao dao = getDAO(RelatorioCrescimentoPorClassificacaoDao.class);
		super.configuraDaoRelatorio(dao); // fecha o dao depois de relatório automaticamente.
		
		if(mostrarDetalhado){
			gerarRelatorioDetalhado(dao);
			return forward(PAGINA_RELATORIO);
		}else{
			gerarRelatorioBasico(dao);
			return forward(PAGINA_RELATORIO_BASICO);
		}
	}

	
	/**
	 * O básico vai ser grupado por: tipo de material e coleção e classificação.
	 *
	 * @param dao
	 * @throws DAOException 
	 */
	private void gerarRelatorioBasico(RelatorioCrescimentoPorClassificacaoDao dao) throws DAOException {
		
		classes = new TreeSet<String>();
		antes = new TreeMap<String, Long>();
		depois = new TreeMap<String, Long>();
		
		List<Object[]> resultadoTituloTemp = dao.findCrescimentoTitulosByPeriodoBasico(
				UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
				UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial), 
				UFRNUtils.toInteger(situacoesMaterial), UFRNUtils.toInteger(formasDocumento),
				classificacaoEscolhida,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS);
		
		List<Object[]> resultadoTituloAgrupadoTemp = dao.findCrescimentoTitulosAgrupadosByPeriodoBasico(
				UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
				UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial), 
				UFRNUtils.toInteger(situacoesMaterial), UFRNUtils.toInteger(formasDocumento),
				classificacaoEscolhida,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS);
		
		
		List<Object[]> resultadosMateriaisTemp = dao.findCrescimentoMaterialByPeriodoBasico(
				UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
				UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial), 
				UFRNUtils.toInteger(situacoesMaterial), UFRNUtils.toInteger(formasDocumento),
				classificacaoEscolhida,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS);
		
		resultadosTitulosBasico = resultadoTituloTemp;
		
		resultadosTitulosAgrupadoBasico = FormataDadosRelatoriosUtils.formataMatrizDadosApartirDeLista("Classificação", "Tipo de Material", "Coleção", resultadoTituloAgrupadoTemp);
		
		resultadosMateriaisBasico = FormataDadosRelatoriosUtils.formataMatrizDadosApartirDeLista("Classificação", "Tipo de Material", "Coleção", resultadosMateriaisTemp);
		
		
		
		totalGeralTitulos = new BigInteger("0");
		
		for (Object[] dados : resultadosTitulosBasico) {
			
			// O valor da última linha e coluna sempre contem o total da matriz, sentão só precisa somer esse valor.
			BigInteger total = BigInteger.ZERO;
			if(dados.length > 0)
				total = (BigInteger) dados[1];
			
			totalGeralTitulos = totalGeralTitulos.add( total );
		}
		
		
		
		totalGeralTitulosAgrupados = new BigInteger("0");
		
		for (DadosAuxiliaresRelatoriosMatriciais dados : resultadosTitulosAgrupadoBasico) {
			
			Long totalMatriz = dados.getTotalMatrizResultados();
			
			totalGeralTitulosAgrupados = totalGeralTitulosAgrupados.add( BigInteger.valueOf(totalMatriz) );
		}
		
		
		
		
		totalGeralMateriais = new BigInteger("0");
		
		for (DadosAuxiliaresRelatoriosMatriciais dados : resultadosMateriaisBasico) {
			Long totalMatriz = dados.getTotalMatrizResultados();
			depois.put(dados.getValorAgrupamento1(), totalMatriz);  // Para gerar o gráfico
			
			totalGeralMateriais = totalGeralMateriais.add( BigInteger.valueOf(totalMatriz) );
		}
		
		// adiciona todas as classes que tiveram resultados //
		classes.addAll(antes.keySet());
		classes.addAll(depois.keySet());
		
	}

	
	
	
	
	
	
	
	/**
	 * O detalhado vai ser grupado por: ano, mês, tipo de material ou coleção e classificação.
	 * 
	 * @throws DAOException 
	 *
	 */
	private void gerarRelatorioDetalhado(RelatorioCrescimentoPorClassificacaoDao dao ) throws DAOException {
		
		resultadoCrescimentoTitulo = dao.findCrescimentoTitulosByPeriodo(
				UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
				UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial), 
				UFRNUtils.toInteger(situacoesMaterial), UFRNUtils.toInteger(formasDocumento),
				classificacaoEscolhida,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS);
		
		resultadoCrescimentoTituloAgrupado = dao.findCrescimentoTitulosAgrupadosByPeriodo(
				UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
				UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial), 
				UFRNUtils.toInteger(situacoesMaterial), UFRNUtils.toInteger(formasDocumento),
				classificacaoEscolhida,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS,
				agrupamento1);
		
		
		resultadoCrescimentoMaterial = dao.findCrescimentoMaterialByPeriodo(
				UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
				UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial), 
				UFRNUtils.toInteger(situacoesMaterial), UFRNUtils.toInteger(formasDocumento),
				classificacaoEscolhida,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS,
				agrupamento1);
		
		
		
		 /*
		  *  Se o usuário quer ver a quantidade que tinha antes, precisar fazer uma nova consulta e contar tudo 
		  *  antes do período escolhido. <br/>
		  *  O relatório fica bem mais lento. Por isso o padrão é mostrar apenas o crescimento.
		  */
		if(dadoBooleano){
			
			quantidadeAnteriorTitulo = dao.findQuantidadeAnteriorTitulosByPeriodo(
					UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
					UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial), 
					UFRNUtils.toInteger(situacoesMaterial), UFRNUtils.toInteger(formasDocumento),
					FiltroClassificacoesRelatoriosBiblioteca.getFiltroClassificacao(tipoclassificacaoEscolhida),
					ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES,
					ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS);
			
			
			quantidadeAnteriorMateriais = dao.findQuantidadeAnteriorMaterialByPeriodo(
					UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
					UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial), 
					UFRNUtils.toInteger(situacoesMaterial), UFRNUtils.toInteger(formasDocumento),
					FiltroClassificacoesRelatoriosBiblioteca.getFiltroClassificacao(tipoclassificacaoEscolhida),
					ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES,
					ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS,
					agrupamento1);

			antes = quantidadeAnteriorMateriais;
			depois = new TreeMap<String, Long>();
			
			depois.putAll(antes);
			
			/* *************************************************************************************
			 *  Realiza o somatório de todas as clases com a quantidade anterior existente         *
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
			
			if(quantidadeAnteriorTitulo.size() > 0){
				Map<String, Long> quantidadeAnteriorTituloTempAgrupado = new TreeMap<String, Long>();
				quantidadeAnteriorTituloTempAgrupado.putAll(quantidadeAnteriorTitulo);
				
				for (DadosRelatorioCrescimentoPorClassificacao ano : resultadoCrescimentoTitulo) {
					
					ano.setContandoResultadosAnteriores();
					
					for (DadosInternosRelatorioCrescimentoPorMes mes : ano.getDadosInternos()) {
						
						mes.setContandoResultadosAnteriores();
						
						for (DadosInternosRelatorioCrescimentoPorAgrupamento agruapamento : mes.getDadosInternos()) {
							for (DadosInternosRelatorioCrescimentoPorAgrupamento.DadosInternos classificacao : agruapamento.getDadosInternos()) {
								
								Long quantidadeAnterior = quantidadeAnteriorTituloTempAgrupado.get(classificacao.getClassificacao());
								
								if(quantidadeAnterior == null)
									quantidadeAnterior = 0l;
								
								classificacao.setQuantidadeAnterior(quantidadeAnterior);
								quantidadeAnteriorTituloTempAgrupado.put(classificacao.getClassificacao(), (quantidadeAnterior + classificacao.getQuantidade()) );
								
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
		} else { // se não é agrupado
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
		 * Realiza a totalização por classificação, somente no final depois que adicionar os dados anteriores. 
		 */
		for (DadosRelatorioCrescimentoPorClassificacao ano : resultadoCrescimentoTituloAgrupado) {
			for (DadosInternosRelatorioCrescimentoPorMes mes : ano.getDadosInternos()) {
				mes.totalizaPorClassificacao();
			}
		}
		
		/**
		 * Realiza a totalização por classificação, somente no final depois que adicionar os dados anteriores. 
		 */
		for (DadosRelatorioCrescimentoPorClassificacao ano : resultadoCrescimentoMaterial) {
			for (DadosInternosRelatorioCrescimentoPorMes mes : ano.getDadosInternos()) {
				mes.totalizaPorClassificacao();
			}
		}
		
		classes = new TreeSet<String>();
		
		// adiciona todas as classes que tiveram resultados
		classes.addAll(antes.keySet());
		classes.addAll(depois.keySet());
		
	}

	
//  Não usado, deixa aqui só para ver como é. Se precisar usar no futuro.	
//	
//	/**
//     * Gera um gráfico simples com o crescimento ocorrido em cadas mês do relatório.
//     * 
//     * 
//	 * @throws IOException 
//     */
//    public String getGerarGraficoCrescimento() throws IOException {
//    	
//    	if(mostrarDetalhado){
//			return getGerarGraficoCrescimentoDetalhado();
//		}else{
//			return getGerarGraficoCrescimentoBasico();
//		}
//        
//    }
//	
//    
//    /**
//     * Gera um gráfico simples com o crescimento ocorrido em cadas mês do relatório.
//     * 
//     * 
//	 * @throws IOException 
//     */
//    public String getGerarGraficoCrescimentoBasico() throws IOException {
//    	
//    	DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//    	
//    	
//    	for (DadosAuxiliaresRelatoriosMatriciais resultadoPorClassificacao : resultadosMateriaisBasico) {	
//			dataset.setValue(resultadoPorClassificacao.getTotalMatrizResultados(), "Classificação", "resultadoPorClassificacao.getValorAgrupamento1()");
//		}
//    	
//    	
//    	JFreeChart chart = org.jfree.chart.ChartFactory.createBarChart("Variação do Crescimento de Materiais Por Classficação",
//    			"Classificação",
//    			"Quantidade de Materiais",
//    			dataset,
//    			PlotOrientation.VERTICAL,
//    			true,
//    			true,
//    			false);
//    	
//    	String nomeArquivoTemp = ServletUtilities.saveChartAsPNG(chart, 900, 500, getCurrentSession());
//		
//		return nomeArquivoTemp;
//        
//    }
//    
//    
//    
//    
//    /**
//     * Gera um gráfico simples com o crescimento ocorrido em cadas mês do relatório.
//     * 
//     * 
//	 * @throws IOException 
//     */
//    public String getGerarGraficoCrescimentoDetalhado() throws IOException {
//    	
//    	TimeSeriesCollection dataset = new TimeSeriesCollection();
//    	
//    	TimeSeries serie = new TimeSeries("Materiais Adicionados por Mês ao Acervo");
//    	
//    	for (DadosRelatorioCrescimentoPorClassificacao resultadoPorAno : resultadoCrescimentoMaterial) {	
//    		
//    		for (DadosInternosRelatorioCrescimentoPorMes resultadoPorMes : resultadoPorAno.getDadosInternos()) {
//				long totalMes = 0l;
//				for(String classificacao : resultadoPorMes.getTotaisPorClasificacao().keySet()){	
//					totalMes += resultadoPorMes.getTotaisPorClasificacao().get(classificacao);
//				}
//				serie.add( new Month(resultadoPorMes.getMes(), resultadoPorAno.getAno()), totalMes);
//			}
//		}
//    	
//    	dataset.addSeries(serie);
//    	
//    	JFreeChart chart = org.jfree.chart.ChartFactory.createTimeSeriesChart("Variação do Crescimento de Materiais Mensalmente",
//    			"Meses",
//    			"Quantidade de Materiais",
//    			dataset,
//    			true,
//    			true,
//    			false);
//    	
//    	String nomeArquivoTemp = ServletUtilities.saveChartAsPNG(chart, 900, 500, getCurrentSession());
//		
//		return nomeArquivoTemp;
//        
//    }
    
    
    
	
	// Gets e Sets

	public Map<String, Long> getAntes() { return antes; }
	public Map<String, Long> getDepois() { return depois; }
	public Set<String> getClasses() { return classes; }

	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		if(agrupamentos1 == null ){
			agrupamentos1 = new ArrayList<SelectItem>();
			agrupamentos1.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.COLECAO.valor, AgrupamentoRelatoriosBiblioteca.COLECAO.alias));
			agrupamentos1.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.valor, AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.alias));
			agrupamentos1.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.SITUACAO_MATERIAL.valor, AgrupamentoRelatoriosBiblioteca.SITUACAO_MATERIAL.alias));
			agrupamentos1.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.BIBLIOTECA.valor, AgrupamentoRelatoriosBiblioteca.BIBLIOTECA.alias));
		}
		return agrupamentos1;
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

	
	
	public List<DadosRelatorioCrescimentoPorClassificacao> getResultadoCrescimentoTitulo() {
		return resultadoCrescimentoTitulo;
	}

	public void setResultadoCrescimentoTitulo(List<DadosRelatorioCrescimentoPorClassificacao> resultadoCrescimentoTitulo) {
		this.resultadoCrescimentoTitulo = resultadoCrescimentoTitulo;
	}

	public List<DadosRelatorioCrescimentoPorClassificacao> getResultadoCrescimentoMaterial() {
		return resultadoCrescimentoMaterial;
	}

	public void setResultadoCrescimentoMaterial(List<DadosRelatorioCrescimentoPorClassificacao> resultadoCrescimentoMaterial) {
		this.resultadoCrescimentoMaterial = resultadoCrescimentoMaterial;
	}

	public Map<String, Long> getQuantidadeAnteriorTitulo() {
		return quantidadeAnteriorTitulo;
	}

	public void setQuantidadeAnteriorTitulo(Map<String, Long> quantidadeAnteriorTitulo) {
		this.quantidadeAnteriorTitulo = quantidadeAnteriorTitulo;
	}

	public Map<String, Long> getQuantidadeAnteriorMateriais() {
		return quantidadeAnteriorMateriais;
	}

	public void setQuantidadeAnteriorMateriais(Map<String, Long> quantidadeAnteriorMateriais) {
		this.quantidadeAnteriorMateriais = quantidadeAnteriorMateriais;
	}

	public List<DadosRelatorioCrescimentoPorClassificacao> getResultadoCrescimentoTituloAgrupado() {
		return resultadoCrescimentoTituloAgrupado;
	}

	public List<Object[]> getResultadosTitulosBasico() {
		return resultadosTitulosBasico;
	}

	public void setResultadosTitulosBasico(List<Object[]> resultadosTitulosBasico) {
		this.resultadosTitulosBasico = resultadosTitulosBasico;
	}

	public List<DadosAuxiliaresRelatoriosMatriciais> getResultadosTitulosAgrupadoBasico() {
		return resultadosTitulosAgrupadoBasico;
	}

	public void setResultadosTitulosAgrupadoBasico(
			List<DadosAuxiliaresRelatoriosMatriciais> resultadosTitulosAgrupadoBasico) {
		this.resultadosTitulosAgrupadoBasico = resultadosTitulosAgrupadoBasico;
	}

	public List<DadosAuxiliaresRelatoriosMatriciais> getResultadosMateriaisBasico() {
		return resultadosMateriaisBasico;
	}

	public void setResultadosMateriaisBasico(
			List<DadosAuxiliaresRelatoriosMatriciais> resultadosMateriaisBasico) {
		this.resultadosMateriaisBasico = resultadosMateriaisBasico;
	}

	public BigInteger getTotalGeralTitulos() {
		return totalGeralTitulos;
	}

	public void setTotalGeralTitulos(BigInteger totalGeralTitulos) {
		this.totalGeralTitulos = totalGeralTitulos;
	}

	public BigInteger getTotalGeralTitulosAgrupados() {
		return totalGeralTitulosAgrupados;
	}

	public void setTotalGeralTitulosAgrupados(BigInteger totalGeralTitulosAgrupados) {
		this.totalGeralTitulosAgrupados = totalGeralTitulosAgrupados;
	}

	public BigInteger getTotalGeralMateriais() {
		return totalGeralMateriais;
	}

	public void setTotalGeralMateriais(BigInteger totalGeralMateriais) {
		this.totalGeralMateriais = totalGeralMateriais;
	}
	
	
	
}
