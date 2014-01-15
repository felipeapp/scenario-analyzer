/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 21/06/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioListagemPorFaixaCodigoBarraDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;

/**
 *
 * <p>Relatório para realizar buscas por faixa de Código de Barras. </p>
 *
 * <p> <i> O relatório pode ser sintético ou analítico, listando todos os materiais encontrados.</i> </p>
 * 
 * @author felipe
 *
 */
@Component("relatorioListagemPorFaixaCodigoBarraAcervoMBean")
@Scope("request")
public class RelatorioListagemPorFaixaCodigoBarraAcervoMBean extends AbstractRelatorioBibliotecaMBean {

	
	/**
	 * A página do relatório que mostra apenas a totalização.
	 */
	private static final String PAGINA_RELATORIO_SINTETICO = "/biblioteca/controle_estatistico/relatorioListagemPorFaixaCodigoBarraSintetico.jsp";
	
	/**
	 * A página do relatório que mostra todos os materiais encontrados
	 */
	private static final String PAGINA_RELATORIO_ANALITICO = "/biblioteca/controle_estatistico/relatorioListagemPorFaixaCodigoBarraAnalitico.jsp";
	
	/** Guarda os resultados do relatório. */
	private List <Object []> resultado;
	
	/** Guarda os resultados da contagem de títulos para o relatório sintético. */
	private List <Object []> resultadoTitulos;
	
	/** Indica o total de títulos retornados na consulta analítica. */
	protected int totalTitulos = 0;
	
	/** Indica o total de materiais retornados na consulta analítica. */
	protected int totalMateriais = 0;
	
	/**
	 * Construtor
	 */
	public RelatorioListagemPorFaixaCodigoBarraAcervoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	@Override
	public void configurar() {
		
		setTitulo("Listagem por Faixa de Código de Barras");
		setDescricao(
				"<p>Este relatório permite verificar a quantidade de Título com seus respectivos materiais (exemplares ou fascículos) existentes no acervo dentro " +
				" da faixa de código de barras informada. </p>" +
				"<p> É possível escolher a opção de se emitir o relatório analítico, neste caso serão mostrados todos os materiais encontrados dentro da faixa de código de barras informada. </p>"+ 
				"<p> Observação 1: O agrupamento é desconsiderado quando é emitido o relatório analítico, já que os dados são mostrados na sua totalidade, não podem ser agrupados. </p>"+
				"<p> Observação 2: O período informado corresponde a data de cadastro do material no acervo. </p>");
		
		filtradoPorVariasBibliotecas = true;
		setCampoBibliotecaObrigatorio(false);
		
		filtradoPorPeriodo = true;
		filtradoPorCodigoDeBarras = true;
		
		filtradoPorFormaRelatorio = true; // Analítico ou sintético
		//filtradoPorTipoClassificacao = true; // CDU ou Black
				
		filtradoPorCtgMaterial = true; // Mostrar exemplar, fasciculo ou ambos
		permitirTodasCtgMaterial = true;
		ctgMaterial = CTGMAT_TODOS; // Configura a catalogaria padrão que vem selecionado
		
		filtradoPorAgrupamento1 = true;
		filtradoPorAgrupamento2 = false;
		
		inicioPeriodo = CalendarUtils.adicionarAnos(inicioPeriodo, -1); 
		
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		
		RelatorioListagemPorFaixaCodigoBarraDAO dao = getDAO(RelatorioListagemPorFaixaCodigoBarraDAO.class);
		super.configuraDaoRelatorio(dao); // Para o abstract fechar o dao automaticamente
		
		if(formatoRelatorio == SINTETICO){
			
			// Só é possível configurar aqui depois que o usuário escolheu o tipo sintético
			utilizandoPaginacao = false; 
			filtradoPorAgrupamento1 = true;
			filtradoPorAgrupamento2 = false;
			super.gerarDescricaoDosParametros();
			
			/*resultadoTitulos = dao.countTotalTitulosPorFaixaCodigoBarra(UFRNUtils.toInteger(variasBibliotecas)
					, inicioPeriodo, fimPeriodo
					, codigoDeBarras  
					, ctgMaterial == CTGMAT_AMBOS || ctgMaterial == CTGMAT_EXEMPLARES
					, ctgMaterial == CTGMAT_AMBOS || ctgMaterial == CTGMAT_FASCICULOS
					, getAgrupamento(agrupamento1));*/
			
			resultado = dao.findPorFaixaCodigoBarraSintetico(UFRNUtils.toInteger(variasBibliotecas)
					, inicioPeriodo, fimPeriodo
					, codigoDeBarras  
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS
					, agrupamento1 );
			
			if (resultado.size() == 0) {
				addMensagemWarning("Não foram encontrados materiais com as características escolhidas");
				
				return null;
			}
			
			return forward(PAGINA_RELATORIO_SINTETICO);
			
		} else {
			// Só é possível configurar aqui depois que o usuário escolheu o tipo analítico.
			utilizandoPaginacao = true; 
			filtradoPorAgrupamento1 = false;
			filtradoPorAgrupamento2 = false;
			
			numeroTotalDeRegistros = dao.countPorCodigoBarraAnalitico( 
					UFRNUtils.toInteger(variasBibliotecas)
					, inicioPeriodo, fimPeriodo, codigoDeBarras
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS);
			
			if (numeroTotalDeRegistros == 0) {
				addMensagemWarning("Não foram encontrados materiais com as características escolhidas");
				
				filtradoPorAgrupamento1 = true;
				
				return null;
			}
			
			totalTitulos = dao.countTitulosPorCodigoBarraAnalitico( 
					UFRNUtils.toInteger(variasBibliotecas)
					, inicioPeriodo, fimPeriodo, codigoDeBarras
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS);
			
			totalMateriais = numeroTotalDeRegistros;
			
			calculaValoresPaginacao();
			
			super.gerarDescricaoDosParametros();
			
			resultado = dao.findPorFaixaCodigoBarraAnalitico( 
					UFRNUtils.toInteger(variasBibliotecas)
					, inicioPeriodo, fimPeriodo, codigoDeBarras
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS, paginaAtual, quantidadeRegistrosPorPagina);
			
			return forward(PAGINA_RELATORIO_ANALITICO);
		}
	}


	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/controle_estatistico/relatorioInventarioPorFaixaCodigoBarraAnalitico.jsp</li>
	 *   </ul>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarProximosResultadosConsultaPaginada()
	 */
	@Override
	public String gerarProximosResultadosConsultaPaginada() throws DAOException {
		calculaValoresPaginacao();
		super.gerarDescricaoDosParametros();
		
		utilizandoPaginacao = true; 
		filtradoPorAgrupamento1 = false;
		filtradoPorAgrupamento2 = false;
		
		RelatorioListagemPorFaixaCodigoBarraDAO dao = null;
		
		try{
			dao = getDAO(RelatorioListagemPorFaixaCodigoBarraDAO.class);
		
			resultado = dao.findPorFaixaCodigoBarraAnalitico( 
					UFRNUtils.toInteger(variasBibliotecas)
					, inicioPeriodo, fimPeriodo, codigoDeBarras
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS, paginaAtual, quantidadeRegistrosPorPagina);
	
		}finally{
			if(dao != null) dao.close();
		}
		
		return forward(PAGINA_RELATORIO_ANALITICO);
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 * @throws DAOException 
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		
		if(agrupamentos1 == null ){
			agrupamentos1 = new ArrayList<SelectItem>();
			if ( ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao1() )
				agrupamentos1.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_1.valor, ClassificacoesBibliograficasUtil.getDescricaoClassificacao1()));
			
			if ( ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao2() )
				agrupamentos1.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_2.valor, ClassificacoesBibliograficasUtil.getDescricaoClassificacao2()));
			
			if ( ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao3() )
				agrupamentos1.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_3.valor, ClassificacoesBibliograficasUtil.getDescricaoClassificacao3()));
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

	public List<Object[]> getResultado() {
		return resultado;
	}

	public void setResultado(List<Object[]> resultado) {
		this.resultado = resultado;
	}

	public List<Object[]> getResultadoTitulos() {
		return resultadoTitulos;
	}

	public void setResultadoTitulos(List<Object[]> resultadoTitulos) {
		this.resultadoTitulos = resultadoTitulos;
	}

	public int getTotalTitulos() {
		return totalTitulos;
	}

	public int getTotalMateriais() {
		return totalMateriais;
	}
	
	

}
