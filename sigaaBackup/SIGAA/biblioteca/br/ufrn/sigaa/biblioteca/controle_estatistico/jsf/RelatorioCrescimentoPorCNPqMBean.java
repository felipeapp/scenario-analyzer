/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
import java.util.TreeMap;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioCrescimentoPorCNPqDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosAuxiliaresRelatoriosMatriciais;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorAreaCNPqMes;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosRelatorioCrescimentoPorAreaCNPq;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.AreaConhecimentoCNPqBibliotecaDao;
import br.ufrn.sigaa.biblioteca.util.FormataDadosRelatoriosUtils;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 * <p>MBean que gerencia a cria��o do relat�rio de crescimento de acervo por �rea do CNPq.</p>
 *
 * @author felipe
 */
@Component("relatorioCrescimentoPorCNPqMBean")
@Scope("request")
public class RelatorioCrescimentoPorCNPqMBean extends AbstractRelatorioBibliotecaMBean {
	
	/**
	 * A p�gina do relat�rio
	 */
	private static final String PAGINA_RELATORIO = "/biblioteca/controle_estatistico/relatorioCrescimentoPorCNPq.jsp";
	
	/**
	 * A p�gina do relat�rio b�sico
	 */
	private static final String PAGINA_RELATORIO_BASICO = "/biblioteca/controle_estatistico/relatorioCrescimentoPorCNPqBasico.jsp";
	
	
	/**
	 * O resultado do relat�rio retornado pela consultas para o crescimento do T�tulos. Usam a data de cadastro dos Titulos no acervo
	 */
	private List<DadosRelatorioCrescimentoPorAreaCNPq> resultadoCrescimentoTitulo;
	
	/**
	 * O resultado do relat�rio retornado pela consultas para o crescimento do T�tulos. Usam a data de cadastro dos Titulos no acervo. Agrupado pelas inforama��es dos materiais
	 */
	private List<DadosRelatorioCrescimentoPorAreaCNPq> resultadoCrescimentoTituloAgrupado;
	
	/**
	 * O resultado do relat�rio retornado pela consultas para o crescimento do Exemplares ou Fasc�culos. Usam a data de cadastro dos materiais no acervo.
	 */
	private List<DadosRelatorioCrescimentoPorAreaCNPq> resultadoCrescimentoMaterial;

	/**
	 * Guarda a quantidade anterior de T�tulos separados e ordenados pela �rea
	 */
	private Map<String, Long> quantidadeAnteriorTitulo = new TreeMap<String, Long>();
	
	/**
	 * Guarda a quantidade anterior de Materiais separados e ordenados pela �rea
	 */
	private Map<String, Long> quantidadeAnteriorMateriais = new TreeMap<String, Long>();

	
	///////////////////////// Dados usados para monta o gr�fico //////////////////////////
	
	/** Resultados do relat�rio: antes. */
	private Map<String, Long> antes;
	/** Resultados do relat�rio: depois. */
	private Map<String, Long> depois;
	/** �reas inclu�das no relat�rio. */
	private List<AreaConhecimentoCnpq> areas;
	
	///////////////////////////////////////////////////////////////////////////////////////
	
	
	/** Armazena temporariamente as grandes �reas do CNPq. */
	private Collection<AreaConhecimentoCnpq> grandesAreasConhecimentoCNPq = null;
	
	
	/////////////// Resultados para o relat�rio b�sico  //////////////////////////////
	
	/**  Guarda o crescimento dos materiais para o caso de relat�rio basico. */
	private List<Object[]> resultadosTitulosBasico;
	
	/**  Guarda o crescimento dos materiais para o caso de relat�rio basico. */
	private List<DadosAuxiliaresRelatoriosMatriciais> resultadosTitulosAgrupadoBasico;
	
	/**  Guarda o crescimento dos materiais para o caso de relat�rio basico. */
	private List<DadosAuxiliaresRelatoriosMatriciais> resultadosMateriaisBasico;
	
	/** O total geral de T�tulos para o relat�rio basico */
	private BigInteger totalGeralTitulos;
	
	/** O total geral de T�tulos agrupados para o relat�rio basico */
	private BigInteger totalGeralTitulosAgrupados;
	
	/** O total geral de Materiais agrupados para o relat�rio basico */
	private BigInteger totalGeralMateriais;
	
	//////////////////////////////////////////////////////////////////////////////////
	
	
	
	public RelatorioCrescimentoPorCNPqMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	@Override
	public void configurar() {
		setTitulo("Relat�rio de Crescimento por �rea CNPq");
		setDescricao(
				"<p>Este relat�rio lista o crescimento ocorrido do acervo dentro do per�odo selecionado. S�o mostrados os crescimentos de T�tulos e Exemplares ou Fasc�culos.<br/> " +
				"<p>O crescimento conta a quantidade de T�tulo e Materiais que foram criados no per�odo informado. </p> " +
				"<p>Escolhendo-se a op��o de \"mostrar a quantidade anterior\" � visualizado a quantidade " +
				" de T�tulos e Materiais existentes antes do per�odo em que ocorreu o crescimento, somando-se com a quantidade criada no per�odo informado. Para relat�rio b�sico essa op��o � desconsiderada. </p>"+
				"<p>Selecionando-se uma biblioteca espec�fica, a data de inclus�o do T�tulo no acervo ser� considerada a data de cadastro do <strong>primeiro</strong> material do T�tulo na biblioteca escolhida.</p>"+
				"<br/><br/>"+
				"<p>No relat�rio detalhado os dados s�o agrupados pelo per�odo de crescimento (ano/m�s), pela �rea escolhida e por um dos agrupamentos selecionados nos filtros do relat�rio. </p>"+
				"<p>No relat�rio b�sico os dados s�o agrupados pela cole��o, tipo de material e pela �rea. </p>"+
				"<br/>"+
				"<p><strong> Observa��o: </strong> O filtro de <strong>Tipo de Classifica��o</strong>  � utilizado para recuperar as �reas CNPq que foram geradas " +
				"a partir do tipo de classifica��o escolhido. </p>"+
				"<p> <strong> <i> Relat�rios de crescimento verificam tamb�m os materiais baixados do acervo, se j� existiram algum dia no acervo "+ 
				" tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois. </strong> </i> </p>");
		
		
		filtradoPorVariasBibliotecas = true;
		setCampoBibliotecaObrigatorio(false);
		filtradoPorPeriodo = true;
		setCampoPeriodoObrigatorio(true);
		filtradoPorColecoes = true;
		filtradoPorTiposDeMaterial = true;
		filtradoPorSituacoesDoMaterial = true;
		
		filtradoPorTipoClassificacao = true; // CDU ou BLACK (utilizando para recuperar as �reas referentes a cada classifica��o)
		filtradoPorFormasDocumento = true;
		
		filtradoPorAgrupamento1 = true;
		
		filtradoPorCtgMaterial = true;
		permitirTodasCtgMaterial = false;
		
		/* 
		 * Caso o usu�rio queira ver a quantidade que existia antes no acervo. 
		 * Tinha x e cresceu y.  Por padr�o mostra apenas a quantidade do crescimento 
		 */
		filtradoPorBooleano = true;
		dadoBooleano = false;
		descricaoDadoBooleano = "Mostrar a quantidade anterior ? ";
		
		/* O b�sico vai ser o relat�rio agrupado apenas por cole��o, tipo de material e �rea do CNPq. 
		 * O detalhado vai ser o grupamento atual, por ano, m�s, tipo de material ou cole��o e classifica��o. */
		filtradoPorBasicoVsDetalhado = true;
		
		inicioPeriodo = CalendarUtils.adicionaMeses(inicioPeriodo, -6); // por padr�o recupera o crescimento dos �ltimos 6 meses
		
		ctgMaterial = CTGMAT_EXEMPLARES; // Configura a catalogaria padr�o que vem selecionado
		
	}
	
	
	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		
		RelatorioCrescimentoPorCNPqDAO dao = getDAO(RelatorioCrescimentoPorCNPqDAO.class);
		super.configuraDaoRelatorio(dao); // fecha o dao depois da consulta do relat�rio automaticamente.
		
		if(mostrarDetalhado){
			gerarRelatorioDetalhado(dao);
			return forward(PAGINA_RELATORIO);
		}else{
			gerarRelatorioBasico(dao);
			return forward(PAGINA_RELATORIO_BASICO);
		}
	}

	
	/**
	 * O b�sico vai ser grupado por: tipo de material e cole��o e classifica��o.
	 *
	 * @param dao
	 * @throws DAOException 
	 */
	private void gerarRelatorioBasico(RelatorioCrescimentoPorCNPqDAO dao) throws DAOException {
		
		areas = new ArrayList<AreaConhecimentoCnpq>(getGrandesAreasConhecimentoCNPq());
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
		
		resultadosTitulosAgrupadoBasico = FormataDadosRelatoriosUtils.formataMatrizDadosApartirDeLista("�rea CNPq", "Tipo de Material", "Cole��o", resultadoTituloAgrupadoTemp);
		
		resultadosMateriaisBasico = FormataDadosRelatoriosUtils.formataMatrizDadosApartirDeLista("�rea CNPq", "Tipo de Material", "Cole��o", resultadosMateriaisTemp);
		
		
		
		totalGeralTitulos = new BigInteger("0");
		
		for (Object[] dados : resultadosTitulosBasico) {
			
			// O valor da �ltima linha e coluna sempre contem o total da matriz, sent�o s� precisa somer esse valor.
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
			
			depois.put(dados.getValorAgrupamento1(), totalMatriz);  // Para gerar o gr�fico
			
			totalGeralMateriais = totalGeralMateriais.add( BigInteger.valueOf(totalMatriz) );
		}
		
	}
	
	
	
	/**
	 * O detalhado vai ser grupado por: ano, m�s, tipo de material ou cole��o e classifica��o.
	 * 
	 * @throws DAOException 
	 *
	 */
	private void gerarRelatorioDetalhado(RelatorioCrescimentoPorCNPqDAO dao) throws DAOException {
	
		areas = new ArrayList<AreaConhecimentoCnpq>(getGrandesAreasConhecimentoCNPq());
		
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
		  *  Se o usu�rio quer ver a quantidade que tinha antes, precisar fazer uma nova consulta e contar tudo 
		  *  antes do per�odo escolhido. <br/>
		  *  O relat�rio fica bem mais lento. Por isso o padr�o � mostrar apenas o crescimento.
		  */
		if(dadoBooleano){
			
			quantidadeAnteriorTitulo = dao.findQuantidadeAnteriorTitulosByPeriodo(
					UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo,
					UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial), 
					UFRNUtils.toInteger(situacoesMaterial), UFRNUtils.toInteger(formasDocumento),
					classificacaoEscolhida,
					ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES,
					ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS);
			
			
			quantidadeAnteriorMateriais = dao.findQuantidadeAnteriorMaterialByPeriodo(
					UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo,
					UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial), 
					UFRNUtils.toInteger(situacoesMaterial), UFRNUtils.toInteger(formasDocumento),
					classificacaoEscolhida,
					ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES,
					ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS,
					agrupamento1);

			antes = quantidadeAnteriorMateriais;
			depois = new TreeMap<String, Long>();
			
			depois.putAll(antes);
			
			/* *************************************************************************************
			 *  Realiza o somat�rio de todas as clases com a quantidade anterior existente         *
			 * *************************************************************************************/

			Long quantidade = null;
			
			if(quantidadeAnteriorTitulo.size() > 0){
				Map<String, Long> quantidadeAnteriorTituloTemp = new TreeMap<String, Long>();
				quantidadeAnteriorTituloTemp.putAll(quantidadeAnteriorTitulo);
				
				for (DadosRelatorioCrescimentoPorAreaCNPq ano : resultadoCrescimentoTitulo) {
					
					ano.setContandoResultadosAnteriores();
					
					for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes mes : ano.getDadosInternos()) {
						
						mes.setContandoResultadosAnteriores();
						
						for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento agruapamento : mes.getDadosInternos()) {
							for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento.DadosInternos area : agruapamento.getDadosInternos()) {
								
								quantidade = quantidadeAnteriorTituloTemp.get(area.getDescricaoArea());
								Long quantidadeAnterior = quantidade != null ? quantidade : 0l;

								area.setQuantidadeAnterior(quantidadeAnterior);
								quantidadeAnteriorTituloTemp.put(area.getDescricaoArea(), ( quantidadeAnterior + area.getQuantidade() ) );
								
							}
						}
					}
				}

			}
			
			// A quantidade anterior tamb�m serve para os agrupados
			if(quantidadeAnteriorTitulo.size() > 0){
				Map<String, Long> quantidadeAnteriorTituloTemp = new TreeMap<String, Long>();
				quantidadeAnteriorTituloTemp.putAll(quantidadeAnteriorTitulo);
				
				for (DadosRelatorioCrescimentoPorAreaCNPq ano : resultadoCrescimentoTituloAgrupado) {
					
					ano.setContandoResultadosAnteriores();
					
					for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes mes : ano.getDadosInternos()) {
						
						mes.setContandoResultadosAnteriores();
						
						for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento agruapamento : mes.getDadosInternos()) {
							for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento.DadosInternos area : agruapamento.getDadosInternos()) {
								
								quantidade = quantidadeAnteriorTituloTemp.get(area.getDescricaoArea());
								Long quantidadeAnterior = quantidade != null ? quantidade : 0l;

								area.setQuantidadeAnterior(quantidadeAnterior);
								quantidadeAnteriorTituloTemp.put(area.getDescricaoArea(), ( quantidadeAnterior + area.getQuantidade() ) );
								
							}
						}
					}
				}

			}
			
			if(quantidadeAnteriorMateriais.size() > 0){
				Map<String, Long> quantidadeAnteriorMateriaisTemp = new TreeMap<String, Long>();
				quantidadeAnteriorMateriaisTemp.putAll(quantidadeAnteriorMateriais);
				
				for (DadosRelatorioCrescimentoPorAreaCNPq ano : resultadoCrescimentoMaterial) {
					
					ano.setContandoResultadosAnteriores();
					
					for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes mes : ano.getDadosInternos()) {
						
						mes.setContandoResultadosAnteriores();
						
						for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento agruapamento : mes.getDadosInternos()) {
							for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento.DadosInternos area : agruapamento.getDadosInternos()) {

								quantidade = quantidadeAnteriorMateriaisTemp.get(area.getDescricaoArea());
								Long quantidadeAnterior = quantidade != null ? quantidade : 0l;
								
								area.setQuantidadeAnterior(quantidadeAnterior);
								quantidadeAnteriorMateriaisTemp.put(area.getDescricaoArea(), (quantidadeAnterior + area.getQuantidade()) );
								depois.put( area.getDescricaoArea(), (depois.get(area.getDescricaoArea()) != null ? (Long) depois.get(area.getDescricaoArea()) : 0) + area.getQuantidade() );
								
							}
						}
					}
				}
			}
		} else {
			
			antes = new TreeMap<String, Long>();
			depois = new TreeMap<String, Long>();
			
			for (DadosRelatorioCrescimentoPorAreaCNPq ano : resultadoCrescimentoMaterial) {				
				for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes mes : ano.getDadosInternos()) {					
					for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento agruapamento : mes.getDadosInternos()) {
						for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento.DadosInternos area : agruapamento.getDadosInternos()) {
							depois.put(area.getDescricaoArea(), (depois.get(area.getDescricaoArea()) != null ? (Long) depois.get(area.getDescricaoArea()): 0) + area.getQuantidade() );								
						}
					}
				}
			}
		}

		
		/**
		 * Realiza a totaliza��o por classifica��o, somente no final depois que adicionar os dados anteriores. 
		 */
		for (DadosRelatorioCrescimentoPorAreaCNPq ano : resultadoCrescimentoTituloAgrupado) {
			for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes mes : ano.getDadosInternos()) {
				mes.totalizaPorArea();
			}
		}
		
		
		/**
		 * Realiza a totaliza��o por �rea, somente no final depois que adicionar os dados anteriores. 
		 */
		for (DadosRelatorioCrescimentoPorAreaCNPq ano : resultadoCrescimentoMaterial) {
			for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes mes : ano.getDadosInternos()) {
				mes.totalizaPorArea();
			}
		}
		
	}
	
	
	
//
//	N�o usado, deixado o coment�rio apenas para documenta��o, caso seja necess�rio gerar o gr�fico 
//	novamente desse jeito.
//
//	/**
//     * Gera um gr�fico simples com o crescimento ocorrido em cadas m�s do relat�rio.
//     * 
//     * 
//	 * @throws IOException 
//     */
//    public String getGerarGraficoCrescimento() throws IOException {
//    	
//    	
//    	TimeSeriesCollection dataset = new TimeSeriesCollection();
//    	
//    	TimeSeries serie = new TimeSeries("Materiais Adicionados por M�s ao Acervo");
//    	
//    	for (DadosRelatorioCrescimentoPorAreaCNPq resultadoPorAno : resultadoCrescimentoMaterial) {	
//    		
//    		for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes resultadoPorMes : resultadoPorAno.getDadosInternos()) {
//				long totalMes = 0l;
//				for(String area : resultadoPorMes.getTotaisPorArea().keySet()){	
//					totalMes += resultadoPorMes.getTotaisPorArea().get(area);
//				}
//				serie.add( new Month(resultadoPorMes.getMes(), resultadoPorAno.getAno()), totalMes);
//			}
//		}
//    	
//    	dataset.addSeries(serie);
//    	
//    	JFreeChart chart = org.jfree.chart.ChartFactory.createTimeSeriesChart("Varia��o do Crescimento de Materiais Mensalmente",
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
	public List<AreaConhecimentoCnpq> getAreas() { return areas; }
	
	/**
	 * Retorna as grandes de conhecimento do CNPq.
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/controle_estatistico/relatorioCrescimentoPorCNPq.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<AreaConhecimentoCnpq> getGrandesAreasConhecimentoCNPq() throws DAOException {
		if (grandesAreasConhecimentoCNPq == null) {
			grandesAreasConhecimentoCNPq = new AreaConhecimentoCNPqBibliotecaDao().findGrandeAreasConhecimentoCnpq();
			
			grandesAreasConhecimentoCNPq.add(new AreaConhecimentoCnpq(99999999, "Sem �rea", "Sem �rea"));
		}
		
		return grandesAreasConhecimentoCNPq;
	}

	
	/**
	 * Ver coment�rios da classe pai.<br/>
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
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}

	
	
	public List<DadosRelatorioCrescimentoPorAreaCNPq> getResultadoCrescimentoTitulo() {
		return resultadoCrescimentoTitulo;
	}

	public void setResultadoCrescimentoTitulo(List<DadosRelatorioCrescimentoPorAreaCNPq> resultadoCrescimentoTitulo) {
		this.resultadoCrescimentoTitulo = resultadoCrescimentoTitulo;
	}

	public List<DadosRelatorioCrescimentoPorAreaCNPq> getResultadoCrescimentoMaterial() {
		return resultadoCrescimentoMaterial;
	}

	public void setResultadoCrescimentoMaterial(List<DadosRelatorioCrescimentoPorAreaCNPq> resultadoCrescimentoMaterial) {
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

	public void setResultadosMateriaisBasico(List<DadosAuxiliaresRelatoriosMatriciais> resultadosMateriaisBasico) {
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

	public List<DadosRelatorioCrescimentoPorAreaCNPq> getResultadoCrescimentoTituloAgrupado() {
		return resultadoCrescimentoTituloAgrupado;
	}

	public void setResultadoCrescimentoTituloAgrupado(List<DadosRelatorioCrescimentoPorAreaCNPq> resultadoCrescimentoTituloAgrupado) {
		this.resultadoCrescimentoTituloAgrupado = resultadoCrescimentoTituloAgrupado;
	}

	
}
