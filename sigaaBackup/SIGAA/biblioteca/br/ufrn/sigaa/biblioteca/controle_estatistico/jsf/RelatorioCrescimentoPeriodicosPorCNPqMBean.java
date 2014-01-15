/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 21/09/2010
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioCrescimentoPeriodicosPorCNPqDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorAreaCNPqMes;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosRelatorioCrescimentoPorAreaCNPq;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.AreaConhecimentoCNPqBibliotecaDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 * Bean que gerencia o relatório de crescimento dos periódicos por área do CNPq.
 *
 * @author Bráulio
 */
@Component("relatorioCrescimentoPeriodicosPorCNPqMBean")
@Scope("request")
public class RelatorioCrescimentoPeriodicosPorCNPqMBean extends AbstractRelatorioBibliotecaMBean {
	

	/** Relatório num período dado. */
	private static final String PAGINA_RELATORIO_PERIODO = "/biblioteca/controle_estatistico/relatorioPeriodicosCNPqCrescimentoPeriodo.jsp";
	
	/**
	 * O resultado do relatório retornado pela consultas para o crescimento do Títulos. Usam a data de cadastro dos Titulos no acervo
	 */
	private List<DadosRelatorioCrescimentoPorAreaCNPq> resultadoCrescimentoTitulo;
	
	/**
	 * O resultado do relatório retornado pela consultas para o crescimento do Exemplares ou Fascículos. Usam a data de cadastro dos materiais no acervo.
	 */
	private List<DadosRelatorioCrescimentoPorAreaCNPq> resultadoCrescimentoMaterial;

	/**
	 * Guarda a quantidade anterior de Títulos separados e ordenados pela área
	 */
	private Map<String, Long> quantidadeAnteriorTitulo = new TreeMap<String, Long>();
	
	/**
	 * Guarda a quantidade anterior de Materiais separados e ordenados pela área
	 */
	private Map<String, Long> quantidadeAnteriorMateriais = new TreeMap<String, Long>();

	/** Resultados do relatório: antes. */
	private Map<String, Long> antes;
	/** Resultados do relatório: depois. */
	private Map<String, Long> depois;
	/** Áreas incluídas no relatório. */
	private List<AreaConhecimentoCnpq> areas;
	
	/** Armazena temporariamente as grandes áreas do CNPq. */
	private Collection<AreaConhecimentoCnpq> grandesAreasConhecimentoCNPq = null;
	
	public RelatorioCrescimentoPeriodicosPorCNPqMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	@Override
	public void configurar() {
		titulo = "Relatório de Crescimento dos Periódicos por Área do CNPq";
		descricao = "Este relatório lista o crescimento dos periódicos " +
				"mês a mês, agrupados por área do CNPq."+
				"<p> <strong> <i> Relatórios de crescimento verificam também os materiais baixados do acervo, se já existiram algum dia no acervo "+ 
				" também devem ser contados, senão pode gerar erros ao emitir o mesmo relatório tempos depois. </strong> </i> </p>";

		filtradoPorVariasBibliotecas = true;
		filtradoPorTipoClassificacao = true;
		filtradoPorTipoDeTombamento = true;		
		filtradoPorPeriodo = true;
		
		permitirTodosTiposDeTombamento = true;

		campoBibliotecaObrigatorio = false;
		
		/* 
		 * Caso o usuário queira ver a quantidade que existia antes no acervo. 
		 * Tinha x e cresceu y.  Por padrão mostra apenas a quantidade do crescimento 
		 */
		filtradoPorBooleano = true;
		dadoBooleano = false;
		descricaoDadoBooleano = "Mostrar a quantidade anterior ? ";
		
		inicioPeriodo = CalendarUtils.adicionaMeses(inicioPeriodo, -6); // por padrão recupera o crescimento dos últimos 6 meses
		
	}

	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO);

		RelatorioCrescimentoPeriodicosPorCNPqDAO dao = getDAO(RelatorioCrescimentoPeriodicosPorCNPqDAO.class);
		super.configuraDaoRelatorio(dao); // fecha o dao depois de relatório automaticamente.
		
		resultadoCrescimentoTitulo = dao.findCrescimentoTitulosByPeriodo(
				UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
				tipoDeTombamento, classificacaoEscolhida);
		
		
		resultadoCrescimentoMaterial = dao.findCrescimentoFasciculosByPeriodo(
				UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
				tipoDeTombamento, classificacaoEscolhida);
		
		 /*
		  *  Se o usuário quer ver a quantidade que tinha antes, precisar fazer uma nova consulta e contar tudo 
		  *  antes do período escolhido. <br/>
		  *  O relatório fica bem mais lento. Por isso o padrão é mostrar apenas o crescimento.
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
			 *  Realiza o somatório de todas as clases com a quantidade anterior existente         *
			 * *************************************************************************************/

			Long dados = null;
			
			if(quantidadeAnteriorTitulo.size() > 0){
				Map<String, Long> quantidadeAnteriorTituloTemp = new TreeMap<String, Long>();
				quantidadeAnteriorTituloTemp.putAll(quantidadeAnteriorTitulo);
				
				for (DadosRelatorioCrescimentoPorAreaCNPq ano : resultadoCrescimentoTitulo) {
					
					ano.setContandoResultadosAnteriores();
					
					for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes mes : ano.getDadosInternos()) {
						
						mes.setContandoResultadosAnteriores();
						
						for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento agruapamento : mes.getDadosInternos()) {
							for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento.DadosInternos area : agruapamento.getDadosInternos()) {

								dados = quantidadeAnteriorTituloTemp.get(area.getDescricaoArea());
								Long quantidadeAnterior = dados != null ? dados : 0l;
								
								area.setQuantidadeAnterior(quantidadeAnterior);
								quantidadeAnteriorTituloTemp.put(area.getDescricaoArea(), (quantidadeAnterior + area.getQuantidade()) );
								
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

								dados = quantidadeAnteriorMateriaisTemp.get(area.getDescricaoArea());
								Long quantidadeAnterior = dados != null ? dados : 0l;
								
								area.setQuantidadeAnterior(quantidadeAnterior);
								quantidadeAnteriorMateriaisTemp.put(area.getDescricaoArea(), (quantidadeAnterior + area.getQuantidade()) );
								depois.put(area.getDescricaoArea(), ( depois.get(area.getDescricaoArea()) != null ? depois.get(area.getDescricaoArea()) : 0) + area.getQuantidade() );
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
							depois.put(area.getDescricaoArea(),(depois.get(area.getDescricaoArea()) != null ? depois.get(area.getDescricaoArea()) : 0) + area.getQuantidade() );
						}
					}
				}
			}
		}
		
		/**
		 * Realiza a totalização por área, somente no final depois que adicionar os dados anteriores. 
		 */
		for (DadosRelatorioCrescimentoPorAreaCNPq ano : resultadoCrescimentoMaterial) {
			for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes mes : ano.getDadosInternos()) {
				mes.totalizaPorArea();
			}
		}

		areas = new ArrayList<AreaConhecimentoCnpq>(getGrandesAreasConhecimentoCNPq());

		return forward(PAGINA_RELATORIO_PERIODO);
	}

	
	/**
     * Gera um gráfico simples com o crescimento ocorrido em cadas mês do relatório.
     * 
     * 
	 * @throws IOException 
     */
    public String getGerarGraficoCrescimento() throws IOException {
    	
    	
    	TimeSeriesCollection dataset = new TimeSeriesCollection();
    	
    	TimeSeries serie = new TimeSeries("Fascículos Adicionados por Mês ao Acervo");
    	
    	for (DadosRelatorioCrescimentoPorAreaCNPq resultadoPorAno : resultadoCrescimentoMaterial) {	
    		
    		for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes resultadoPorMes : resultadoPorAno.getDadosInternos()) {
				long totalMes = 0l;
				for(String area : resultadoPorMes.getTotaisPorArea().keySet()){	
					totalMes += resultadoPorMes.getTotaisPorArea().get(area);
				}
				serie.add( new Month(resultadoPorMes.getMes(), resultadoPorAno.getAno()), totalMes);
			}
		}
    	
    	dataset.addSeries(serie);
    	
    	JFreeChart chart = org.jfree.chart.ChartFactory.createTimeSeriesChart("Variação do Crescimento de Fascículos Mensalmente",
    			"Meses",
    			"Quantidade de Fascículos",
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
	public List<AreaConhecimentoCnpq> getAreas() { return areas; }
	
	/**
	 * Retorna as grandes de conhecimento do CNPq.
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/controle_estatistico/relatorioPeriodicosCNPqCrescimentoPeriodo.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<AreaConhecimentoCnpq> getGrandesAreasConhecimentoCNPq() throws DAOException {
		if (grandesAreasConhecimentoCNPq == null) {
			grandesAreasConhecimentoCNPq = new AreaConhecimentoCNPqBibliotecaDao().findGrandeAreasConhecimentoCnpq();
			
			grandesAreasConhecimentoCNPq.add(new AreaConhecimentoCnpq(99999999, "Sem Área", "Sem Área"));
		}
		
		return grandesAreasConhecimentoCNPq;
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		return null;
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

	public List<DadosRelatorioCrescimentoPorAreaCNPq> getResultadoCrescimentoTitulo() {
		return resultadoCrescimentoTitulo;
	}

	public List<DadosRelatorioCrescimentoPorAreaCNPq> getResultadoCrescimentoMaterial() {
		return resultadoCrescimentoMaterial;
	}

	public Map<String, Long> getQuantidadeAnteriorTitulo() {
		return quantidadeAnteriorTitulo;
	}

	public Map<String, Long> getQuantidadeAnteriorMateriais() {
		return quantidadeAnteriorMateriais;
	}
	
}
