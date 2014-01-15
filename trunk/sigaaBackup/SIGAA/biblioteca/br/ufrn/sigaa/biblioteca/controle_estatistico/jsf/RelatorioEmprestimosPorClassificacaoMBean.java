/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Classe que controla a geração do relatório de empréstimos por tipo de material x classe CDU.
 *
 * @author Bráulio
 * @since 08/12/2009
 * @version 1.0 Criação
 */
@Component("relatorioEmprestimosPorClassificacaoMBean")
@Scope("request")
public class RelatorioEmprestimosPorClassificacaoMBean extends AbstractRelatorioBibliotecaMBean {
	
	/**
	 * As colunas que repesentação as classificações mostradas para o usuário
	 */
	private List<String> colunas = new ArrayList<String>();
	
	/**
	 * As colunas que repesentação os agrupamentos mostrados para o usuário
	 */
	private List<String> linhas = new ArrayList<String>();
	
	/**
	 * A matrix de resultados que contém em cada posição [linha][coluna]  a quantidade de empréstimos
	 */
	private Long[][] matrixResultado;
	
	/**
	 * A lista de resultados para o relatário analítico.
	 */
	private List<Object[]> resultados;
	
	/**
	 * A página onde é visualizado os dados do relatório
	 */
	private static final String PAGINA_DO_RELATORIO_SINTETICO = "/biblioteca/controle_estatistico/relatorioEmprestimosPorClassificacaoSintetico.jsp";

	
	/**
	 * A página onde é visualizado os dados do relatório
	 */
	private static final String PAGINA_DO_RELATORIO_ANALITICO = "/biblioteca/controle_estatistico/relatorioEmprestimosPorClassificacaoAnalitico.jsp";
	
	
	public RelatorioEmprestimosPorClassificacaoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	
	
	/**
	 * Configura o relatório.
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 */
	@Override
	public void configurar() {
		
		setTitulo("Relatório de Empréstimos por Classificação");		
		setDescricao(" <p>Neste relatório é possível consultar a quantidade de empréstimos realizados por Títulos do acervo</p> " +
				     " <p>No relatório <strong>sintético</strong> é possível visualizar os resultados agrupados pela classificação dos materiais informacionais. </p>"+
					 " <p>No relatório <strong>analítico</strong> é apresentada uma listagem de Títulos com seus respectivos empréstimos. <i>Observação: O filtro de agrupamento é desconsiderado neste caso.</i></p>");
		
		setFiltradoPorVariasBibliotecas(true);
		campoBibliotecaObrigatorio = false;
		inicioPeriodo = CalendarUtils.adicionaMeses(new Date(), -1);
		setFiltradoPorPeriodo(true);
		
		filtradoPorTipoClassificacao = true;  // classe CDU e BLACK
		
		filtradoPorAgrupamento1 = true; // Tipo de Material ou Coleção
		
		filtradoPorTiposDeMaterial = true;
		filtradoPorColecoes = true;
		
		filtradoPorFormaRelatorio = true;
		
		quantidadeRegistrosPorPagina = 100;
		
	}
	
	

	/**
	 * Realiza a consulta e organiza os resultados.
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		RelatorioEmprestimosPorClassificacaoDao dao = getDAO(RelatorioEmprestimosPorClassificacaoDao.class);
		configuraDaoRelatorio(dao); // fecha o dao automaticamente após a consulta
		
		
		if ( formatoRelatorio == SINTETICO ){
		
		
			// Só é possível configurar aqui depois que o usuário escolheu o tipo sintético
			utilizandoPaginacao = false; 
			filtradoPorAgrupamento1 = true;
			super.gerarDescricaoDosParametros();
			
			/**
			 * Object[0] = Classificação 
			 * Object[1] = Agrupamento (Coleção ou Tipo de Material)
			 * Object[2] = Quantidade de empréstimos
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
		
		// Só é possível configurar aqui depois que o usuário escolheu o tipo sintético //
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
					listaTitulosTemp.add(new TituloCatalografico(  (Integer) linha[1] ) );  // Guarda uma listagem de Títulos para gerar o formato de referência
					linha[5] = ((BigDecimal)linha[3]).longValue() + ((BigDecimal)linha[4]).longValue(); // Totaliza os resultados
				}
				
				// Gera o conjunto de formatos de referência  ///
				List<Object[]> formatosTemp = new FormatosBibliograficosUtil().gerarFormatosReferenciaSeparados( listaTitulosTemp , false);
				
				// Para cada resultado //
				for (Object[] linha : resultados) {
					
					int posicao = 0;
					
					forInterno:
					for (posicao = 0 ;  posicao < formatosTemp.size(); posicao ++ ) {
						if(   ((Integer) linha[1]).equals( formatosTemp.get(posicao)[0] )){ // se achou o formato de referência 
							linha[1] = formatosTemp.get(posicao)[1]; // troca o id do título pelo seu formato de referência
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
	 * Monta a matriz de resultados para mostra ao usuário na página.
	 *
	 * @param dados
	 */
	private void montaMatrixResultados(List<Object[]> dados ){
		
		/*
		 * Guarda os valores únicos das linhas e colunas do relatório.
		 */
		Set<String> colunasTemp = new TreeSet<String>();
		Set<String> linhasTemp = new TreeSet<String>();
		
		/*
		 * Mapea o par [linha, coluna] a uma quantidade de empréstimos
		 */
		Map<String, Long> valoresRetorndos = new HashMap<String, Long>();
		
		for (Object[] dado : dados) {
			colunasTemp.add((String) dado[0]); // classifação como coluna
			linhasTemp.add((String) dado[1]);  // agrupamento como linhasTemp 
			valoresRetorndos.put((String) dado[0]+(String) dado[1], ((BigDecimal)dado[2]).longValue());
		}
		
		colunas = new ArrayList<String>(colunasTemp);
		linhas = new ArrayList<String>(linhasTemp);
		
		// Cria uma matriz para guardar os dados , com uma linha a mais e uma coluna a mais para guardar a totalização.
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
					
					// Realiza a Totalização das colunas e linhas do relatório  //
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
					matrixResultado[linha][coluna] = 0L; // caso não existe o par [linha, coluna] colona o valor zero
				}
			}
		}
		
		linhas.add("Total"); // Adiciona mais uma linha para a totalização.
		
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
	 * Ver comentários da classe pai.<br/>
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
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}

}
