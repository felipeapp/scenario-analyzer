/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>Relat�rio para realizar buscas por faixa de classifica��o CDU ou Black </p>
 *
 * <p> <i> O relat�rio pode ser quantitativo ou sint�tico, listando todos os materiais encontrados.</i> </p>
 * 
 * @author jadson
 *
 */
@Component("relatorioPorFaixaDeClassificacaoMBean")
@Scope("request")
public class RelatorioPorFaixaDeClassificacaoMBean extends AbstractRelatorioBibliotecaMBean {

	
	/**
	 * A p�gina do relat�rio que mostra apenas a totaliza��o.
	 */
	private static final String PAGINA_RELATORIO_SINTETICO = "/biblioteca/controle_estatistico/relatorioPorFaixaClassificacaoSintetico.jsp";
	
	/**
	 * A p�gina do relat�rio que mostra todos os materiais encontrados
	 */
	private static final String PAGINA_RELATORIO_ANALITICO = "/biblioteca/controle_estatistico/relatorioPorFaixaClassificacaoAnalitico.jsp";
	
	
	/** Guarda os resultados do relat�rio. */
	private List <Object []> resultado;
	
	
	/** Guarda os resultados da contagem de t�tulos para o relat�rio sint�tico. */
	private List <Object []> resultadoTitulos;
	
	/**
	 * Construtor
	 */
	public RelatorioPorFaixaDeClassificacaoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	@Override
	public void configurar() {
		
		setTitulo("Materiais por Faixa de Classifica��o");
		setDescricao(
				"<p>Este relat�rio permite verificar a quantidade de T�tulo com seus respectivos materiais (exemplares ou fasc�culos) existentes no acervo dentro " +
				" do intervalo de classifica��o informado. </p>" +
				"<p> � poss�vel escolher a op��o de se emitir o relat�rio anal�tico, neste caso ser�o mostrados todos os materiais encontrados dentro do intervalo de classifica��o informado. </p>"+ 
				"<p> Observa��o: O agrupamento � desconsiderado quando � emitido o relat�rio anal�tico, j� que os dados s�o mostrados na sua totalidade, n�o podem ser agrupados. </p>");
		
		filtradoPorVariasBibliotecas = true;
		setCampoBibliotecaObrigatorio(false);
		
		filtradoPorSituacoesDoMaterial = true;
		filtradoPorFormasDocumento = true;
		filtradoPorTiposDeMaterial = true;
		filtradoPorColecoes = true;
		
		filtradoPorFormaRelatorio = true; // Anal�tico ou sint�tico
		filtradoPorTipoClassificacao = true; // CDU ou Black
		
		filtradoPorIntervaloClassificacao = true; // Classe inicial e final
		
		
		filtradoPorCtgMaterial = true; // Mostrar exemplar, fasciculo ou ambos
		permitirTodasCtgMaterial = true;
		ctgMaterial = CTGMAT_TODOS; // Configura a catalogaria padr�o que vem selecionado
		
		filtradoPorAgrupamento1 = true;
		filtradoPorAgrupamento2 = true;
		
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		
		RelatorioPorFaixaDeClassificacaoDao dao = getDAO(RelatorioPorFaixaDeClassificacaoDao.class);
		super.configuraDaoRelatorio(dao); // Para o abstract fechar o dao automaticamente
		
		
		
		
		if(formatoRelatorio == SINTETICO){
			
			// S� � poss�vel configurar aqui depois que o usu�rio escolheu o tipo sint�tico
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
			// S� � poss�vel configurar aqui depois que o usu�rio escolheu o tipo anal�tico.
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
	 * Ver coment�rios da classe pai.<br/>
	 *
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
