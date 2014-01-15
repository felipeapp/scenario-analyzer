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
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioPorFaixaDeClassificacaoDao;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;

/**
 *
 * <p>Relatório para realizar buscas por faixa de classificação CDU ou Black </p>
 *
 * <p> <i> O relatório pode ser quantitativo ou sintético, listando todos os materiais encontrados.</i> </p>
 * 
 * @author jadson
 *
 */
@Component("relatorioPorFaixaDeClassificacaoMBean")
@Scope("request")
public class RelatorioPorFaixaDeClassificacaoMBean extends AbstractRelatorioBibliotecaMBean {

	
	/**
	 * A página do relatório que mostra apenas a totalização.
	 */
	private static final String PAGINA_RELATORIO_SINTETICO = "/biblioteca/controle_estatistico/relatorioPorFaixaClassificacaoSintetico.jsp";
	
	/**
	 * A página do relatório que mostra todos os materiais encontrados
	 */
	private static final String PAGINA_RELATORIO_ANALITICO = "/biblioteca/controle_estatistico/relatorioPorFaixaClassificacaoAnalitico.jsp";
	
	
	/** Guarda os resultados do relatório. */
	private List <Object []> resultado;
	
	
	/** Guarda os resultados da contagem de títulos para o relatório sintético. */
	private List <Object []> resultadoTitulos;
	
	/**
	 * Construtor
	 */
	public RelatorioPorFaixaDeClassificacaoMBean(){
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
		
		setTitulo("Materiais por Faixa de Classificação");
		setDescricao(
				"<p>Este relatório permite verificar a quantidade de Título com seus respectivos materiais (exemplares ou fascículos) existentes no acervo dentro " +
				" do intervalo de classificação informado. </p>" +
				"<p> É possível escolher a opção de se emitir o relatório analítico, neste caso serão mostrados todos os materiais encontrados dentro do intervalo de classificação informado. </p>"+ 
				"<p> Observação: O agrupamento é desconsiderado quando é emitido o relatório analítico, já que os dados são mostrados na sua totalidade, não podem ser agrupados. </p>");
		
		filtradoPorVariasBibliotecas = true;
		setCampoBibliotecaObrigatorio(false);
		
		filtradoPorSituacoesDoMaterial = true;
		filtradoPorFormasDocumento = true;
		filtradoPorTiposDeMaterial = true;
		filtradoPorColecoes = true;
		
		filtradoPorFormaRelatorio = true; // Analítico ou sintético
		filtradoPorTipoClassificacao = true; // CDU ou Black
		
		filtradoPorIntervaloClassificacao = true; // Classe inicial e final
		
		
		filtradoPorCtgMaterial = true; // Mostrar exemplar, fasciculo ou ambos
		permitirTodasCtgMaterial = true;
		ctgMaterial = CTGMAT_TODOS; // Configura a catalogaria padrão que vem selecionado
		
		filtradoPorAgrupamento1 = true;
		filtradoPorAgrupamento2 = true;
		
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
		
		RelatorioPorFaixaDeClassificacaoDao dao = getDAO(RelatorioPorFaixaDeClassificacaoDao.class);
		super.configuraDaoRelatorio(dao); // Para o abstract fechar o dao automaticamente
		
		
		
		
		if(formatoRelatorio == SINTETICO){
			
			// Só é possível configurar aqui depois que o usuário escolheu o tipo sintético
			utilizandoPaginacao = false; 
			filtradoPorAgrupamento1 = true;
			filtradoPorAgrupamento2 = true;
			super.gerarDescricaoDosParametros();
			
			resultadoTitulos = dao.countTotalTitulosPorFaixaAcervo(UFRNUtils.toInteger(variasBibliotecas)
					, UFRNUtils.toInteger(tiposDeMaterial)
					, UFRNUtils.toInteger(colecoes)
					, UFRNUtils.toInteger(situacoesMaterial)
					, UFRNUtils.toInteger(formasDocumento)
					, classeInicial, classeFinal
					, classificacaoEscolhida  
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS);
			
			resultado = dao.findPorFaixaDeClassificacaoSintetico(
					  UFRNUtils.toInteger(variasBibliotecas)
					, UFRNUtils.toInteger(tiposDeMaterial)
					, UFRNUtils.toInteger(colecoes)
					, UFRNUtils.toInteger(situacoesMaterial)
					, UFRNUtils.toInteger(formasDocumento)
					, classeInicial, classeFinal
					, classificacaoEscolhida  
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS
					, agrupamento1, agrupamento2);
			
			return forward(PAGINA_RELATORIO_SINTETICO);
			
		}else{
			// Só é possível configurar aqui depois que o usuário escolheu o tipo analítico.
			utilizandoPaginacao = true; 
			filtradoPorAgrupamento1 = false;
			filtradoPorAgrupamento2 = false;
			
			numeroTotalDeRegistros = dao.countPorFaixaDeClassificacaoAnalitico( 
					UFRNUtils.toInteger(variasBibliotecas)
					, UFRNUtils.toInteger(tiposDeMaterial)
					, UFRNUtils.toInteger(colecoes)
					, UFRNUtils.toInteger(situacoesMaterial)
					, UFRNUtils.toInteger(formasDocumento)
					, classeInicial, classeFinal
					, classificacaoEscolhida
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS);
			
			calculaValoresPaginacao();
			
			super.gerarDescricaoDosParametros();
			
			resultado = dao.findPorFaixaDeClassificacaoAnalitico( 
					UFRNUtils.toInteger(variasBibliotecas)
					, UFRNUtils.toInteger(tiposDeMaterial)
					, UFRNUtils.toInteger(colecoes)
					, UFRNUtils.toInteger(situacoesMaterial)
					, UFRNUtils.toInteger(formasDocumento)
					, classeInicial, classeFinal
					, classificacaoEscolhida 
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
	 *   	<li>/sigaa.war/biblioteca/controle_estatistico/relatorioPorFaixaClassificacaoAnalitico.jsp</li>
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
		
		RelatorioPorFaixaDeClassificacaoDao dao = null;
		
		try{
			dao = getDAO(RelatorioPorFaixaDeClassificacaoDao.class);
		
			resultado = dao.findPorFaixaDeClassificacaoAnalitico( 
					UFRNUtils.toInteger(variasBibliotecas)
					, UFRNUtils.toInteger(tiposDeMaterial)
					, UFRNUtils.toInteger(colecoes)
					, UFRNUtils.toInteger(situacoesMaterial)
					, UFRNUtils.toInteger(formasDocumento)
					, classeInicial, classeFinal
					, classificacaoEscolhida  
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS, paginaAtual, quantidadeRegistrosPorPagina);
	
		}finally{
			if(dao != null) dao.close();
		}
		
		return forward(PAGINA_RELATORIO_ANALITICO);
	}

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
		if(agrupamentos2 == null ){
			agrupamentos2 = new ArrayList<SelectItem>();
			agrupamentos2.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO.valor, AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO.alias));
			agrupamentos2.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.COLECAO.valor, AgrupamentoRelatoriosBiblioteca.COLECAO.alias));
			agrupamentos2.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.valor, AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.alias));
		}
		return agrupamentos2;
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
	
	

}
