/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * MBean que gerencia a geração do relatório de totalização de títulos e materiais do acervo.
 *
 * @author Bráulio
 * @version 1.5 - Jadson - Adicionando totalização quando o relatório possui mais de um agrupamento. Adicionando novos filtros. 
 *          Fazendo o relatório extender de AbstractRelatorioBibliotecaMBean. 
 */
@Component("relatorioTotalTitulosMateriaisMBean")
@Scope("request")
public class RelatorioTotalTitulosMateriaisMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** Página que implementa o relatório. */
	private static final String PAGINA_RELATORIO = "/biblioteca/controle_estatistico/relatorioTotalTitulosMateriais.jsp";
	
	
	/** Resultados do relatório com um agrupamento. */
	private Map<String, Object[]> resultados1;
	
	/** Resultados do relatório com dois agrupamentos. */
	private Map<String, Map<String, Object[]> > resultados2;
	
	/** Resultado da totalização de títulos por classificação, uma consulta serada para mostrar os resultados corretos 
	 * independente do agrupamento que se use.
	 */
	List<Object[]> resultadosTitulos;
	
	/** Totalização do relatório com um agrupamento. */
	private int[] total1;
	
	/** Totalização de cada tabela do relatório com dois agrupamentos. */
	private Map<String, int[]> total2;
	
	
	public RelatorioTotalTitulosMateriaisMBean(){
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
		
		titulo = "Relatório de Total de Títulos e Materiais";
		descricao = " <p> Relatórios para realizar o quantitativo de Títulos e Materiais no acervos das bibliotecas. </p>"
			+ " <p> A maioria dos campos desse relatório são opcionais. Não se escolhendo a biblioteca, o sistema recuperará materiais de todas as bibliotecas; " +
					"não se escolhendo a situação, o sistema recuperará materiais em todas as situações; e assim sucessivamente.  </p>"
			+ " <p> Observação 1: O período informado leva em consideração a data de cadastro dos materiais e Títulos no acervo. </p>"
			+ " <p> Observação 2: A quantidade Total de Títulos só é mostrada caso o primeiro agrupamento seja uma classificação bibliográfica. </p>";
		
		
		
		filtradoPorVariasBibliotecas = true;
		setCampoBibliotecaObrigatorio(false); // se não selecionar a biblioteca traz todas por padrão
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
	 * Ver comentários da classe pai.<br/>
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		
		RelatorioTotalTitulosMateriaisDao dao = getDAO(RelatorioTotalTitulosMateriaisDao.class);
		super.configuraDaoRelatorio(dao); // fecha o dao depois do relatório automaticamente.
	
		
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
			
			// Totalização
			
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
			
			// Totalização
			
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
