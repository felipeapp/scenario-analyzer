/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em: 08/12/2009
 */

package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioEmprestimosPorClassificacaoDao;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;

/**
 * Classe que controla a gera��o do relat�rio de empr�stimos por tipo de material x classe CDU.
 *
 * @author Br�ulio
 * @since 08/12/2009
 * @version 1.0 Cria��o
 */
@Component("relatorioEmprestimosPorClassificacaoMBean")
@Scope("request")
public class RelatorioEmprestimosPorClassificacaoMBean extends AbstractRelatorioBibliotecaMBean {
	
	/**
	 * As colunas que repesenta��o as classifica��es mostradas para o usu�rio
	 */
	private List<String> colunas = new ArrayList<String>();
	
	/**
	 * As colunas que repesenta��o os agrupamentos mostrados para o usu�rio
	 */
	private List<String> linhas = new ArrayList<String>();
	
	/**
	 * A matrix de resultados que cont�m em cada posi��o [linha][coluna]  a quantidade de empr�stimos
	 */
	private Long[][] matrixResultado;
	
	/**
	 * A lista de resultados para o relat�rio anal�tico.
	 */
	private List<Object[]> resultados;
	
	/**
	 * A p�gina onde � visualizado os dados do relat�rio
	 */
	private static final String PAGINA_DO_RELATORIO_SINTETICO = "/biblioteca/controle_estatistico/relatorioEmprestimosPorClassificacaoSintetico.jsp";

	
	/**
	 * A p�gina onde � visualizado os dados do relat�rio
	 */
	private static final String PAGINA_DO_RELATORIO_ANALITICO = "/biblioteca/controle_estatistico/relatorioEmprestimosPorClassificacaoAnalitico.jsp";
	
	
	public RelatorioEmprestimosPorClassificacaoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	
	
	/**
	 * Configura o relat�rio.
	 * 
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 */
	@Override
	public void configurar() {
		
		setTitulo("Relat�rio de Empr�stimos por Classifica��o");		
		setDescricao(" <p>Neste relat�rio � poss�vel consultar a quantidade de empr�stimos realizados por T�tulos do acervo</p> " +
				     " <p>No relat�rio <strong>sint�tico</strong> � poss�vel visualizar os resultados agrupados pela classifica��o dos materiais informacionais. </p>"+
					 " <p>No relat�rio <strong>anal�tico</strong> � apresentada uma listagem de T�tulos com seus respectivos empr�stimos. <i>Observa��o: O filtro de agrupamento � desconsiderado neste caso.</i></p>");
		
		setFiltradoPorVariasBibliotecas(true);
		campoBibliotecaObrigatorio = false;
		inicioPeriodo = CalendarUtils.adicionaMeses(new Date(), -1);
		setFiltradoPorPeriodo(true);
		
		filtradoPorTipoClassificacao = true;  // classe CDU e BLACK
		
		filtradoPorAgrupamento1 = true; // Tipo de Material ou Cole��o
		
		filtradoPorTiposDeMaterial = true;
		filtradoPorColecoes = true;
		
		filtradoPorFormaRelatorio = true;
		
		quantidadeRegistrosPorPagina = 100;
		
	}
	
	

	/**
	 * Realiza a consulta e organiza os resultados.
	 * 
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		RelatorioEmprestimosPorClassificacaoDao dao = getDAO(RelatorioEmprestimosPorClassificacaoDao.class);
		configuraDaoRelatorio(dao); // fecha o dao automaticamente ap�s a consulta
		
		
		if ( formatoRelatorio == SINTETICO ){
		
		
			// S� � poss�vel configurar aqui depois que o usu�rio escolheu o tipo sint�tico
			utilizandoPaginacao = false; 
			filtradoPorAgrupamento1 = true;
			super.gerarDescricaoDosParametros();
			
			/**
			 * Object[0] = Classifica��o 
			 * Object[1] = Agrupamento (Cole��o ou Tipo de Material)
			 * Object[2] = Quantidade de empr�stimos
			 */
			List<Object[]> dados = dao.countEmprestimosPorClassificacao(UFRNUtils.toInteger(variasBibliotecas)
					, UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial)
					, inicioPeriodo, fimPeriodo, classificacaoEscolhida  , agrupamento1);
		
			if(dados == null || dados.size() <= 0 ){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
			montaMatrixResultados(dados);
			
			
			return forward( PAGINA_DO_RELATORIO_SINTETICO );
		
		}else{
			
			
			numeroTotalDeRegistros =  dao.countEmprestimosAnaliticoPorClassificacao(UFRNUtils.toInteger(variasBibliotecas)
					, UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial), inicioPeriodo, fimPeriodo);
		
			
			if( numeroTotalDeRegistros == 0){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}else{
				
				return gerarProximosResultadosConsultaPaginada();
				
			}
		}
		
	}
	
	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/controle_estatistico/relatorioInventarioPorFaixaCodigoBarraAnalitico.jsp</li>
	 *   </ul>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarProximosResultadosConsultaPaginada()
	 */
	@Override
	public String gerarProximosResultadosConsultaPaginada() throws DAOException {
		
		calculaValoresPaginacao();
		
		// S� � poss�vel configurar aqui depois que o usu�rio escolheu o tipo sint�tico //
		utilizandoPaginacao = true; 
		filtradoPorAgrupamento1 = true;
		super.gerarDescricaoDosParametros();
		
		RelatorioEmprestimosPorClassificacaoDao dao = null;
		
		try{
			dao = getDAO(RelatorioEmprestimosPorClassificacaoDao.class);
		
			resultados =  dao.listaEmprestimosAnaliticoPorClassificacao(UFRNUtils.toInteger(variasBibliotecas)
					, UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial)
					, inicioPeriodo, fimPeriodo, classificacaoEscolhida,  paginaAtual, quantidadeRegistrosPorPagina);
	
			
			if(resultados.size() > 0 ){
			
				List<TituloCatalografico> listaTitulosTemp = new ArrayList<TituloCatalografico>();
				
				for (Object[] linha : resultados) {
					listaTitulosTemp.add(new TituloCatalografico(  (Integer) linha[1] ) );  // Guarda uma listagem de T�tulos para gerar o formato de refer�ncia
					linha[5] = ((BigDecimal)linha[3]).longValue() + ((BigDecimal)linha[4]).longValue(); // Totaliza os resultados
				}
				
				// Gera o conjunto de formatos de refer�ncia  ///
				List<Object[]> formatosTemp = new FormatosBibliograficosUtil().gerarFormatosReferenciaSeparados( listaTitulosTemp , false);
				
				// Para cada resultado //
				for (Object[] linha : resultados) {
					
					int posicao = 0;
					
					forInterno:
					for (posicao = 0 ;  posicao < formatosTemp.size(); posicao ++ ) {
						if(   ((Integer) linha[1]).equals( formatosTemp.get(posicao)[0] )){ // se achou o formato de refer�ncia 
							linha[1] = formatosTemp.get(posicao)[1]; // troca o id do t�tulo pelo seu formato de refer�ncia
							break forInterno;
						}
					}
					
					formatosTemp.remove(posicao);
				
				}
			
			}
		}finally{
			if(dao != null) dao.close();
		}
		
		return forward(PAGINA_DO_RELATORIO_ANALITICO);
	}
	
	
	/**
	 * Monta a matriz de resultados para mostra ao usu�rio na p�gina.
	 *
	 * @param dados
	 */
	private void montaMatrixResultados(List<Object[]> dados ){
		
		/*
		 * Guarda os valores �nicos das linhas e colunas do relat�rio.
		 */
		Set<String> colunasTemp = new TreeSet<String>();
		Set<String> linhasTemp = new TreeSet<String>();
		
		/*
		 * Mapea o par [linha, coluna] a uma quantidade de empr�stimos
		 */
		Map<String, Long> valoresRetorndos = new HashMap<String, Long>();
		
		for (Object[] dado : dados) {
			colunasTemp.add((String) dado[0]); // classifa��o como coluna
			linhasTemp.add((String) dado[1]);  // agrupamento como linhasTemp 
			valoresRetorndos.put((String) dado[0]+(String) dado[1], ((BigDecimal)dado[2]).longValue());
		}
		
		colunas = new ArrayList<String>(colunasTemp);
		linhas = new ArrayList<String>(linhasTemp);
		
		// Cria uma matriz para guardar os dados , com uma linha a mais e uma coluna a mais para guardar a totaliza��o.
		matrixResultado = new Long[linhasTemp.size()+1][colunasTemp.size()+1];
		
		/**
		 * Monta os valores da matrix
		 */
		for (int linha = 0; linha < linhasTemp.size(); linha++) {
			for (int coluna = 0; coluna < colunasTemp.size(); coluna++) {
				
				String chave = colunas.get(coluna) + linhas.get(linha);
				
				if( valoresRetorndos.containsKey(chave)){ // verifica se exite um valor para o par  [linha, coluna]
					Long valor = valoresRetorndos.get( chave );
					
					matrixResultado[linha][coluna] = valor;
					
					// Realiza a Totaliza��o das colunas e linhas do relat�rio  //
					if(matrixResultado[linha][colunasTemp.size()] == null){
						matrixResultado[linha][colunasTemp.size()] = valor;
					}else{
						matrixResultado[linha][colunasTemp.size()] += valor;
					}
					
					if(matrixResultado[linhasTemp.size()][coluna] == null){
						matrixResultado[linhasTemp.size()][coluna] = valor;
					}else{
						matrixResultado[linhasTemp.size()][coluna] += valor;
					}
					
					if(matrixResultado[linhasTemp.size()][colunasTemp.size()] == null){
						matrixResultado[linhasTemp.size()][colunasTemp.size()] = valor;
					}else{
						matrixResultado[linhasTemp.size()][colunasTemp.size()] += valor;
					}
					
					
				}else{
					matrixResultado[linha][coluna] = 0L; // caso n�o existe o par [linha, coluna] colona o valor zero
				}
			}
		}
		
		linhas.add("Total"); // Adiciona mais uma linha para a totaliza��o.
		
	}

	


	public List<String> getColunas() {
		return colunas;
	}

	public void setColunas(List<String> colunas) {
		this.colunas = colunas;
	}



	public List<String> getLinhas() {
		return linhas;
	}



	public void setLinhas(List<String> linhas) {
		this.linhas = linhas;
	}



	public Long[][] getMatrixResultado() {
		return matrixResultado;
	}
	
	public void setMatrixResultado(Long[][] matrixResultado) {
		this.matrixResultado = matrixResultado;
	}
	

	public List<Object[]> getResultados() {
		return resultados;
	}

	public void setResultados(List<Object[]> resultados) {
		this.resultados = resultados;
	}



	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		Collection<SelectItem> temp = new ArrayList<SelectItem>();
		temp.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.COLECAO.valor , AgrupamentoRelatoriosBiblioteca.COLECAO.alias));
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
