/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 09/02/2011
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ArrayUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioTotalTitulosMateriaisDao;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;

/**
 * MBean que gerencia a gera��o do relat�rio de totaliza��o de t�tulos e materiais do acervo.
 *
 * @author Br�ulio
 * @version 1.5 - Jadson - Adicionando totaliza��o quando o relat�rio possui mais de um agrupamento. Adicionando novos filtros. 
 *          Fazendo o relat�rio extender de AbstractRelatorioBibliotecaMBean. 
 */
@Component("relatorioTotalTitulosMateriaisMBean")
@Scope("request")
public class RelatorioTotalTitulosMateriaisMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** P�gina que implementa o relat�rio. */
	private static final String PAGINA_RELATORIO = "/biblioteca/controle_estatistico/relatorioTotalTitulosMateriais.jsp";
	
	
	/** Resultados do relat�rio com um agrupamento. */
	private Map<String, Object[]> resultados1;
	
	/** Resultados do relat�rio com dois agrupamentos. */
	private Map<String, Map<String, Object[]> > resultados2;
	
	/** Resultado da totaliza��o de t�tulos por classifica��o, uma consulta serada para mostrar os resultados corretos 
	 * independente do agrupamento que se use.
	 */
	List<Object[]> resultadosTitulos;
	
	/** Totaliza��o do relat�rio com um agrupamento. */
	private int[] total1;
	
	/** Totaliza��o de cada tabela do relat�rio com dois agrupamentos. */
	private Map<String, int[]> total2;
	
	
	public RelatorioTotalTitulosMateriaisMBean(){
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
		
		titulo = "Relat�rio de Total de T�tulos e Materiais";
		descricao = " <p> Relat�rios para realizar o quantitativo de T�tulos e Materiais no acervos das bibliotecas. </p>"
			+ " <p> A maioria dos campos desse relat�rio s�o opcionais. N�o se escolhendo a biblioteca, o sistema recuperar� materiais de todas as bibliotecas; " +
					"n�o se escolhendo a situa��o, o sistema recuperar� materiais em todas as situa��es; e assim sucessivamente.  </p>"
			+ " <p> Observa��o 1: O per�odo informado leva em considera��o a data de cadastro dos materiais e T�tulos no acervo. </p>"
			+ " <p> Observa��o 2: A quantidade Total de T�tulos s� � mostrada caso o primeiro agrupamento seja uma classifica��o bibliogr�fica. </p>";
		
		
		
		filtradoPorVariasBibliotecas = true;
		setCampoBibliotecaObrigatorio(false); // se n�o selecionar a biblioteca traz todas por padr�o
		filtradoPorPeriodo = true;
		
		filtradoPorCtgMaterial = true;
		filtradoPorColecoes = true;
		filtradoPorTiposDeMaterial = true;
		filtradoPorSituacoesDoMaterial = true;
		filtradoPorFormasDocumento = true;
		filtradoPorAgrupamento1 = true;
		filtradoPorAgrupamento2 = true;
		
		campoPeriodoObrigatorio = false;
		
		possuiFiltrosObrigatorios = false;

		fimPeriodo = null;
		inicioPeriodo = null;
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
		
		
		RelatorioTotalTitulosMateriaisDao dao = getDAO(RelatorioTotalTitulosMateriaisDao.class);
		super.configuraDaoRelatorio(dao); // fecha o dao depois do relat�rio automaticamente.
	
		
		resultadosTitulos = dao.countTotalTitulosAcervo(UFRNUtils.toInteger(variasBibliotecas)
				, UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial)
				, UFRNUtils.toInteger(situacoesMaterial), UFRNUtils.toInteger(formasDocumento)
				, inicioPeriodo, fimPeriodo
				, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES
				, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS
				,agrupamento1, agrupamento2 );
		
		List<Object[]> r;
		
		if ( agrupamento2 == AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ) {
		
			r = dao.countTotalTitulosEMateriaisAcervo(
					UFRNUtils.toInteger(variasBibliotecas)
					, inicioPeriodo, fimPeriodo
					, UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial)
					, UFRNUtils.toInteger(situacoesMaterial),UFRNUtils.toInteger(formasDocumento)
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS
					, agrupamento1, agrupamento2);
			
			resultados1 = ArrayUtils.agrupar(r, String.class, Object[].class);
			
			// Totaliza��o
			
			total1 = new int[]{0, 0, 0};
			for ( Object[] linha : resultados1.values() ) {
				total1[0] += ((Number)linha[0]).intValue();
				total1[1] += ((Number)linha[1]).intValue();
				total1[2] += ((Number)linha[2]).intValue();
			}
			
			
		} else {
		
			r = dao.countTotalTitulosEMateriaisAcervo(
					UFRNUtils.toInteger(variasBibliotecas)
					, inicioPeriodo, fimPeriodo
					, UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial)
					, UFRNUtils.toInteger(situacoesMaterial),UFRNUtils.toInteger(formasDocumento)
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES
					, ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS
					, agrupamento1, agrupamento2);
			
			resultados2 = ArrayUtils.agrupar(r, String.class, String.class, Object[].class );
			
			// Totaliza��o
			
			total2 = new HashMap<String, int[]>();
			
			for ( Map.Entry<String, Map<String, Object[]>> item : resultados2.entrySet() ) {
				
				total2.put(item.getKey(), new int[]{0,0,0});
				
				
				for ( Object[] linha : item.getValue().values() ) {
					total2.get(item.getKey())[0] += ((Number)linha[0]).intValue();
					total2.get(item.getKey())[1] += ((Number)linha[1]).intValue();
					total2.get(item.getKey())[2] += ((Number)linha[2]).intValue();
				}
			}
		}
		
		return forward(PAGINA_RELATORIO);
	}
	
	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
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
	public Collection<SelectItem> getAgrupamentos2ComboBox(){
		if(agrupamentos2 == null ){
			agrupamentos2 = new ArrayList<SelectItem>();
			agrupamentos2.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO.valor, AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO.alias));
			
			if ( ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao1() )
				agrupamentos2.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_1.valor, ClassificacoesBibliograficasUtil.getDescricaoClassificacao1()));
			
			if ( ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao2() )
				agrupamentos2.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_2.valor, ClassificacoesBibliograficasUtil.getDescricaoClassificacao2()));
			
			if ( ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao3() )
				agrupamentos2.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_3.valor, ClassificacoesBibliograficasUtil.getDescricaoClassificacao3()));
			
			agrupamentos2.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.COLECAO.valor, AgrupamentoRelatoriosBiblioteca.COLECAO.alias));
			agrupamentos2.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.valor, AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.alias));
		}
		return agrupamentos2;
	}
	
	
	public Map<String, Object[]> getResultados1() {
		return resultados1;
	}
	
	public void setResultados1(Map<String, Object[]> resultados1) {
		this.resultados1 = resultados1;
	}

	public Map<String, Map<String, Object[]>> getResultados2() {
		return resultados2;
	}
	
	public void setResultados2(Map<String, Map<String, Object[]>> resultados2) {
		this.resultados2 = resultados2;
	}
	
	public int[] getTotal1() {
		return total1;
	}

	public void setTotal1(int[] total1) {
		this.total1 = total1;
	}

	public Map<String, int[]> getTotal2() {
		return total2;
	}
	
	public void setTotal2(Map<String, int[]> total2) {
		this.total2 = total2;
	}

	public List<Object[]> getResultadosTitulos() {
		return resultadosTitulos;
	}

	public void setResultadosTitulos(List<Object[]> resultadosTitulos) {
		this.resultadosTitulos = resultadosTitulos;
	}

	

}
