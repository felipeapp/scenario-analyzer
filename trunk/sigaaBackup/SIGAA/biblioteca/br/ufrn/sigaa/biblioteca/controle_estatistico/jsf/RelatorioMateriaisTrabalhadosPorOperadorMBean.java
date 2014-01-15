/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 12/11/2009
 *
 */

package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ArrayUtils;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioMateriaisTrabalhadosPorOperadorDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.ConstantesRelatorioBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DataRelatorioMateriaisTrabalhadosPorOperador;

/**
 * MBean que gerencia o relat�rio de materiais trabalhados por operador.
 * 
 * @author Br�ulio Bezerra
 * @since 12/11/2009
 * @version 1.0 Cria��o e implementa��o da classe
 */
@Component("relatorioMateriaisTrabalhadosPorOperadorMBean")
@Scope("request")
public class RelatorioMateriaisTrabalhadosPorOperadorMBean extends AbstractRelatorioBibliotecaMBean {

	/** JSP do relat�rio sint�tico */
	private static final String PAGINA_RELATORIO_SINTETICO = "/biblioteca/controle_estatistico/relatorioDeMateriaisTrabalhadosPorOperadorSintetico.jsp";
	
	/** JSP do relat�rio anal�tico */
	private static final String PAGINA_RELATORIO_ANALITICO = "/biblioteca/controle_estatistico/relatorioDeMateriaisTrabalhadosPorOperadorAnalitico.jsp";

	/** A lista com os dados do relat�rio. */
	private List<DataRelatorioMateriaisTrabalhadosPorOperador> linhas;

	/** Guarda os resultados do relat�rio da parte referente a t�tulos. */
	private List <Object []> resultadoTitulos;

	/** Guarda os resultados do relat�rio da parte referente a materiais. */
	private List <Object []> resultadoMateriais;

	/** Guarda os resultados do relat�rio da parte referente a materiais em um map. */
	private Map<String, Map<String, Object[]>> resultadoTitulosMap;

	/** Guarda os resultados do relat�rio da parte referente a t�tulos em um map. */
	private Map<String, Map<String, Map<String, Object[]>>> resultadoMateriaisMap;
	
	/** Totaliza��o do relat�rio da parte referente a t�tulos. */
	private int[] totalTitulos;
	
	/** Totaliza��o do relat�rio da parte referente a materiais. */
	private int[] totalMateriais;
	
	public RelatorioMateriaisTrabalhadosPorOperadorMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relat�rio.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo( "Relat�rio de Materiais Trabalhados por Operador" );
		setDescricao(
				"<p> Neste relat�rio � poss�vel consultar os T�tulos e Materiais inclu�dos no acervo por cada operador.</p> " +
				"<br/>"+
				"<p> <strong>Observa��o:</strong> O filtro \"Tipo de Classifica��o\" � utilizado apenas para agrupar os resultados no relat�rio sint�tico e exibir a classifica��o escolhida no relat�rio anal�tico. </p>" );

		setFiltradoPorVariasBibliotecas( true );
		setFiltradoPorVariasPessoas( true );
		setFiltradoPorPeriodo( true );
		setFiltradoPorTipoClassificacao( true );
		setFiltradoPorTipoDeAcervo( true );
		setFiltradoPorTiposDeMaterial( true );
		setFiltradoPorFormaRelatorio( true );
		filtradoPorColecoes = true;
		
		setCampoPessoaObrigatorio( true );
		setCampoBibliotecaObrigatorio(false);
		
		inicioPeriodo = CalendarUtils.adicionaMeses(new Date(), -1);
	}
	
	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		RelatorioMateriaisTrabalhadosPorOperadorDAO dao = getDAO( RelatorioMateriaisTrabalhadosPorOperadorDAO.class );
		super.configuraDaoRelatorio(dao);
		
		if(formatoRelatorio == SINTETICO){
			
			
			utilizandoPaginacao = false; // S� � poss�vel configurar aqui depois que o usu�rio escolheu o tipo sint�tico
			
			super.gerarDescricaoDosParametros(); 

			resultadoTitulosMap = null;
			resultadoMateriaisMap = null;
			
			
			// Realiza a contagem de T�tulos Inclu�dos pelo Operador //
			if (tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_TODOS || 
				tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_TITULOS) {
				resultadoTitulos = dao.findTitulosCadastradosPorOperadorSintetico(UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasPessoas)
						, inicioPeriodo, fimPeriodo
						, UFRNUtils.toInteger(tiposDeMaterial)
						, UFRNUtils.toInteger(colecoes)
						, classificacaoEscolhida);
				
				resultadoTitulosMap = ArrayUtils.agrupar(resultadoTitulos, String.class, String.class, Object[].class);
			}
			
			// Realiza a contagem de Materiais Inclu�dos pelo Operador //
			if (tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_TODOS || 
					tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_MATERIAIS) {
			
				resultadoMateriais = dao.findMateriaisCadastradosPorOperadorSintetico(UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasPessoas)
						, inicioPeriodo, fimPeriodo
						, UFRNUtils.toInteger(tiposDeMaterial)
						, UFRNUtils.toInteger(colecoes)
						, classificacaoEscolhida);
				
				resultadoMateriaisMap = ArrayUtils.agrupar(resultadoMateriais, String.class, String.class, String.class, Object[].class);
			}
			
			if ((resultadoTitulosMap == null || resultadoTitulosMap.size() == 0) && 
					(resultadoMateriaisMap == null || resultadoMateriaisMap.size() == 0)) {
				addMensagemWarning("N�o foram encontrados t�tulos/materiais com as caracter�sticas escolhidas");
				
				return null;
			}
			
			return forward(PAGINA_RELATORIO_SINTETICO);
			
			
		} else { // formatoRelatorio == ANALITICO
			
			// Como pode ser mostrado uma listagem muito grande, n�o h� como permitir a visualiza��o dos dois resulatdos ao mesmo tempo no anal�tico //
			if (tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_TODOS) {
				addMensagemErro("Para o relat�rio anal�tico, deve ser selecionado apenas um entre 'T�tulos' e 'Materiais' no campo Tipo de Acervo.");
				return null;
			}
			
			
			
			utilizandoPaginacao = true;   // S� � poss�vel configurar aqui depois que o usu�rio escolheu o tipo anal�tico.
			
			linhas = new ArrayList<DataRelatorioMateriaisTrabalhadosPorOperador>();
			
			
			/* *****************************************************************
			 *               Monta a listagem anal�tica de T�tulos
			 * *****************************************************************/
			if (tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_TITULOS) {				
				numeroTotalDeRegistros = dao.countTitulosCadastradosPorOperadorAnalitico(UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasPessoas)
						, inicioPeriodo, fimPeriodo
						, UFRNUtils.toInteger(tiposDeMaterial)
						, UFRNUtils.toInteger(colecoes));
				
				calculaValoresPaginacao();
				
				super.gerarDescricaoDosParametros();
				
				resultadoTitulos = dao.findTitulosCadastradosPorOperadorAnalitico(UFRNUtils.toInteger(variasPessoas)
						, inicioPeriodo, fimPeriodo
						, UFRNUtils.toInteger(tiposDeMaterial)
						, UFRNUtils.toInteger(colecoes), 
						paginaAtual, quantidadeRegistrosPorPagina);
				
				if (resultadoTitulos == null || resultadoTitulos.size() == 0) {
					addMensagemWarning("N�o foram encontrados t�tulos com as caracter�sticas escolhidas");
					return null;
				}
				
				for ( Object[] object : resultadoTitulos ) {
					linhas.add( new DataRelatorioMateriaisTrabalhadosPorOperador((String)object[0], null,
							(String)object[1], (String) object[2], (String) object[3],  // as classifica��es  
							(Integer) object[4], (String) object[5], (String) object[6], (String) object[7], 
							(String) object[8], (String) object[9], (String) object[10], (Date) object[11] ) );
				}
				
				Collections.sort(linhas);
				
			}

			
			/* *****************************************************************
			 *               Monta a listagem anal�tica de Materiais
			 * *****************************************************************/
			if (tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_MATERIAIS) {
				
				numeroTotalDeRegistros = dao.countMateriaisCadastradosPorOperadorAnalitico(UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasPessoas)
						, inicioPeriodo, fimPeriodo
						, UFRNUtils.toInteger(tiposDeMaterial)
						, UFRNUtils.toInteger(colecoes));
				
				calculaValoresPaginacao();
				
				super.gerarDescricaoDosParametros();

				resultadoMateriais = dao.findMateriaisCadastradosPorOperadorAnalitico(UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasPessoas)
						, inicioPeriodo, fimPeriodo
						, UFRNUtils.toInteger(tiposDeMaterial)
						, UFRNUtils.toInteger(colecoes), 
						paginaAtual, quantidadeRegistrosPorPagina);

				if (resultadoMateriais == null || resultadoMateriais.size() == 0) {
					addMensagemWarning("N�o foram encontrados materiais com as caracter�sticas escolhidas");
					return null;
				}
				
				
				for ( Object[] object : resultadoMateriais ) {
					linhas.add( new DataRelatorioMateriaisTrabalhadosPorOperador((String)object[0], (String)object[1]
					        , (String)object[2], (String) object[3], (String) object[4],
					        (Integer) object[5], (String) object[6], (String) object[7], (String) object[8], 
							(String) object[9], (String) object[10], (String) object[11], (Date) object[12] ) );
				}
				
				Collections.sort(linhas);
				
			}
			
			// verifica se houve resultados
			if ( linhas.size() == 0 ) {
				addMensagem( MensagensArquitetura.BUSCA_SEM_RESULTADOS );
				return null;
			}
			
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
	 *   	<li>/sigaa.war/biblioteca/controle_estatistico/relatorioDeMateriaisTrabalhadosPorOperadorAnalitico.jsp</li>
	 *   </ul>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarProximosResultadosConsultaPaginada()
	 */
	@Override
	public String gerarProximosResultadosConsultaPaginada() throws DAOException {
		calculaValoresPaginacao();
		super.gerarDescricaoDosParametros();
		
		utilizandoPaginacao = true;
		
		RelatorioMateriaisTrabalhadosPorOperadorDAO dao = null;
		
		// TEM que zerar o resultado anterior !!!! //
		resultadoTitulos = new ArrayList<Object[]>();
		resultadoMateriais = new ArrayList<Object[]>();
		
		try{
			dao = getDAO(RelatorioMateriaisTrabalhadosPorOperadorDAO.class);

			if (tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_TITULOS) {				
				numeroTotalDeRegistros = dao.countTitulosCadastradosPorOperadorAnalitico(UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasPessoas)
						, inicioPeriodo, fimPeriodo
						, UFRNUtils.toInteger(tiposDeMaterial)
						, UFRNUtils.toInteger(colecoes));
				
				resultadoTitulos = dao.findTitulosCadastradosPorOperadorAnalitico(UFRNUtils.toInteger(variasPessoas)
						, inicioPeriodo, fimPeriodo
						, UFRNUtils.toInteger(tiposDeMaterial)
						, UFRNUtils.toInteger(colecoes), 
						paginaAtual, quantidadeRegistrosPorPagina);
				
				linhas = new ArrayList<DataRelatorioMateriaisTrabalhadosPorOperador>();
				
				if (resultadoTitulos == null || resultadoTitulos.size() == 0) {
					addMensagemWarning("N�o foram encontrados t�tulos com as caracter�sticas escolhidas");
					return null;
				}
				
				for ( Object[] object : resultadoTitulos ) {
					linhas.add( new DataRelatorioMateriaisTrabalhadosPorOperador((String)object[0], null,
							(String)object[1], (String) object[2], (String) object[3],  // as classifica��es  
							(Integer) object[4], (String) object[5], (String) object[6], (String) object[7], 
							(String) object[8], (String) object[9], (String) object[10], (Date) object[11] ) );
				}
				
				Collections.sort(linhas);
				
			}

			if (tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_MATERIAIS) {
				
				numeroTotalDeRegistros = dao.countMateriaisCadastradosPorOperadorAnalitico(UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasPessoas)
						, inicioPeriodo, fimPeriodo
						, UFRNUtils.toInteger(tiposDeMaterial)
						, UFRNUtils.toInteger(colecoes));

				resultadoMateriais = dao.findMateriaisCadastradosPorOperadorAnalitico(UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasPessoas)
						, inicioPeriodo, fimPeriodo
						, UFRNUtils.toInteger(tiposDeMaterial)
						, UFRNUtils.toInteger(colecoes), 
						paginaAtual, quantidadeRegistrosPorPagina);
				
				if (resultadoMateriais == null || resultadoMateriais.size() == 0) {
					addMensagemWarning("N�o foram encontrados materiais com as caracter�sticas escolhidas");
					return null;
				}
				
				
				linhas = new ArrayList<DataRelatorioMateriaisTrabalhadosPorOperador>();
				
				for ( Object[] object : resultadoMateriais ) {
					
					linhas.add( new DataRelatorioMateriaisTrabalhadosPorOperador((String)object[0], (String)object[1],
							(String)object[2], 
							(String) object[3], 
							(String) object[4],
							(Integer) object[5], (String) object[6], (String) object[7], (String) object[8], 
							(String) object[9], (String) object[10], (String) object[11], (Date) object[12] ) );
				}
				
				Collections.sort(linhas);
				
				// verifica se houve resultados
				if ( linhas.size() == 0 ) {
					addMensagem( MensagensArquitetura.BUSCA_SEM_RESULTADOS );
					return null;
				}
				
			}
			
			
	
		}finally{
			if(dao != null) dao.close();
		}
		
		return forward(PAGINA_RELATORIO_ANALITICO);
	}
	
	public List<DataRelatorioMateriaisTrabalhadosPorOperador> getLinhas() {
		return linhas;
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

	public List<Object[]> getResultadoMateriais() {
		return resultadoMateriais;
	}

	public void setResultadoMateriais(List<Object[]> resultadoMateriais) {
		this.resultadoMateriais = resultadoMateriais;
	}

	public List<Object[]> getResultadoTitulos() {
		return resultadoTitulos;
	}

	public void setResultadoTitulos(List<Object[]> resultadoTitulos) {
		this.resultadoTitulos = resultadoTitulos;
	}

	public int[] getTotalTitulos() {
		return totalTitulos;
	}

	public void setTotalTitulos(int[] totalTitulos) {
		this.totalTitulos = totalTitulos;
	}

	public int[] getTotalMateriais() {
		return totalMateriais;
	}

	public void setTotalMateriais(int[] totalMateriais) {
		this.totalMateriais = totalMateriais;
	}

	public Map<String, Map<String, Map<String, Object[]>>> getResultadoMateriaisMap() {
		return resultadoMateriaisMap;
	}

	public void setResultadoMateriaisMap(
			Map<String, Map<String, Map<String, Object[]>>> resultadoMateriaisMap) {
		this.resultadoMateriaisMap = resultadoMateriaisMap;
	}

	public Map<String, Map<String, Object[]>> getResultadoTitulosMap() {
		return resultadoTitulosMap;
	}

	public void setResultadoTitulosMap(
			Map<String, Map<String, Object[]>> resultadoTitulosMap) {
		this.resultadoTitulosMap = resultadoTitulosMap;
	}
	
	/**
	 * <p>Retorna o resultado de acordo com o tipo de acervo selecionado.</p>
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/biblioteca/controle_estatistico/relatorioDeMateriaisTrabalhadosPorOperadorSintetico.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Object getResultadoMap() {
		Map<String, Object[]> resultadoMap = new TreeMap<String, Object[]>();
		
		if (tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_TODOS
				|| tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_TITULOS) {
			for (String key : resultadoTitulosMap.keySet()) {
				resultadoMap.put(key, new Object[]{ resultadoTitulosMap.get(key), null });
			}
		} 
		
		if (tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_TODOS
				|| tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_MATERIAIS) {
			for (String key : resultadoMateriaisMap.keySet()) {
				if (resultadoMap.containsKey(key)) {
					//resultadoMap.put(key, new Object[]{ resultadoMap.get(key)[0], resultadoMateriaisMap.get(key) });
					resultadoMap.get(key)[1] = resultadoMateriaisMap.get(key);
				} else {
					resultadoMap.put(key, new Object[]{ null, resultadoMateriaisMap.get(key) });
				}
			}
		}
		
		return resultadoMap;
	}

}
