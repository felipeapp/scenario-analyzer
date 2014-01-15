/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 20/09/2010
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ArrayUtils;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioTotalPorCNPqDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;

/**
 * Bean que controla o relatório de total de títulos, exemplares e
 * fascículos por área do CNPq.
 *
 * @author Bráulio (refactoring)
 */
@Component("relatorioTotalPorCNPqMBean")
@Scope("request")
public class RelatorioTotalPorCNPqMBean extends AbstractRelatorioBibliotecaMBean {

	/**
	 * A página do relatório.
	 */
	private static final String PAGINA_RELATORIO = "/biblioteca/controle_estatistico/relatorioTotalCNPQ.jsp";

	/**
	 * Lista de Resultados sem agrupamento do relatório que será mostrada aos usuários.
	 */
	private List<Object[]> resultadosNaoAgrupado;
	
	/**
	 * Lista que guarda os resultados de materiais digitais, se o relatório não está agrupado, esses 
	 * resultados são mostrados na mesma tabela onde mostra os outros materiais, se o usuário escolher 
	 * agrupar os resultados, não dá para realizar o agrupamento, tem que mostrar em uma lista 
	 * separada igual a lista de Títulos.
	 */
	private List<Object[]> resultadosMateriaisDigitais; 
	
	/** 
	 * Resultados do relatório com um agrupamento. 
	 */
	private Map<String, Map<String, Object[]>> resultados;
	
	/** 
	 * Totalização de cada tabela do relatório com um agrupamento. 
	 */
	private Map<String, int[]> total;
	
	/**
	 * O resultado do relatório retornado pela consulta para o total de Títulos. Usa-se a data de cadastro dos Títulos no acervo.
	 */
	private Map<String, Object[]> resultadosTotalTitulos;
	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 *  <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	@Override
	public void configurar() {
		setTitulo("Relatório de Total por Área do CNPq");
		setDescricao("<p>Este relatório lista a quantidade total de títulos, exemplares, fascículos e materiais digitais por área do CNPq.</p>" +
				"<br/>"+
				"<p>São considerados materiais digitais neste relatório os Títulos que não possuem materiais físicos e possuem o campo de endereço eletrônico preenchido. " +
				" Consequentemente os filtros sobre as informações dos materiais: Bibliotecas, Coleções, entre outros, não influenciam na contagem de materiais digitais desse relatório.</p>" +
				"<br/>"+
				"<br/>"+
				"<p><strong> Observação: </strong> O filtro de <strong>Tipo de Classificação</strong> é utilizado para recuperar as áreas CNPq que foram geradas " +
				"a partir do tipo de classificação escolhida. </p>");
		
		filtradoPorVariasBibliotecas = true;
		filtradoPorPeriodo = true;
		filtradoPorColecoes = true;
		filtradoPorTiposDeMaterial = true;
		filtradoPorSituacoesDoMaterial = true;
		filtradoPorFormasDocumento = true;
		filtradoPorCtgMaterial = true;
		
		permitirDigitalCtgMaterial = true;
		
		campoPeriodoObrigatorio = false;
		campoBibliotecaObrigatorio = false;
		
		filtradoPorAgrupamento1 = true;
		
		filtradoPorTipoClassificacao = true; // CDU ou BLACK (utilizando para recuperar as áreas referentes a cada classificação)

		fimPeriodo = null;
		inicioPeriodo = null;
	}

	public RelatorioTotalPorCNPqMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 *  <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO);
		
		RelatorioTotalPorCNPqDAO dao = getDAO(RelatorioTotalPorCNPqDAO.class);
		super.configuraDaoRelatorio(dao); // fecha o dao depois da consulta do relatório automaticamente.
		
		if (ctgMaterial == CTGMAT_DIGITAIS) { // Se o usuário escolher apenas materiais digitais, não há porque agrupar já que não retorna nenhum material físico
			agrupamento1 = AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO;
		}
		
		boolean semAgrupamento = agrupamento1 == AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO;
		
		boolean buscaSoOsMateriasDigitais = this.ctgMaterial == CTGMAT_DIGITAIS;
		
		
		if(!buscaSoOsMateriasDigitais){
			/**
			 * Retorna a quantidade de materiais (exemplares e fascículos) agrupados por área CNPq com a quantidade dos respectivos Títulos
			 */
			resultadosNaoAgrupado = dao.findTotalTitulosMateriaisByAreasCNPQ(
					UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
					UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial), 
					UFRNUtils.toInteger(situacoesMaterial), UFRNUtils.toInteger(formasDocumento),
					classificacaoEscolhida,
					agrupamento1,
					ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES,
					ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS,
					ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_DIGITAIS);
			
		}
		
		/**
		 * Retorna em uma consulta separada o resultado dos digitais agrupados e ordenados pela área CNPq
		 */
		resultadosMateriaisDigitais = dao.findTotalMateriaisDigitais(inicioPeriodo, fimPeriodo, classificacaoEscolhida);
		
		
		/** 
		 * Retorna a quantidade de Títulos mostrada em um lista separada   
		 */
		List<Object[]> resultadosTotalTitulosNaoAgrupado = dao.findTotalTitulosCNPq(
				UFRNUtils.toInteger(variasBibliotecas), inicioPeriodo, fimPeriodo, 
				UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposDeMaterial), 
				UFRNUtils.toInteger(situacoesMaterial), UFRNUtils.toInteger(formasDocumento),
				classificacaoEscolhida,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS,
				ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_DIGITAIS);
		
		resultadosTotalTitulos = ArrayUtils.agrupar(resultadosTotalTitulosNaoAgrupado, String.class, Object[].class);
	
		
		
		if (semAgrupamento) {	
			
			/*
			 *  Popula a primeira lista com a quantidade de materiais digitais encontrados na segunda. 
			 *  Não a problema fazer dois for alinhados porque a quantidade de resultados é pequena.
			 *  
			 *  Se o usuário escolher agrupar os resultados não pode mostrar os materiais digitais na mesma tabela
			 *  tem que mostrar em uma tabela separada igual a de Títulos
			 */
			for (int index2 = 0; index2 < resultadosMateriaisDigitais.size(); index2++) {
				
				boolean achouResutado = false;
				
				String descricaoAreaAtual = (String) resultadosMateriaisDigitais.get(index2)[0];
				BigInteger qtdMateriaisDigitaisAtual = (BigInteger) resultadosMateriaisDigitais.get(index2)[1];
				
				forInterno:
					for (int index = 0; index < resultadosNaoAgrupado.size(); index++) {
					
					String descricaoAreaResultados = (String)  resultadosNaoAgrupado.get(index)[0];	
						
					if ( descricaoAreaResultados.equals(descricaoAreaAtual ) ){ // Se a descrição das áreas são iguais
						resultadosNaoAgrupado.get(index)[5] = qtdMateriaisDigitaisAtual; // a quantidade de materias digitais buscados na segunda consulta.
						achouResutado = true;
						break forInterno;
					}
					
				}
				
				if(! achouResutado){
						resultadosNaoAgrupado.add(new Object[]{descricaoAreaAtual, qtdMateriaisDigitaisAtual, 0, 0, 0, qtdMateriaisDigitaisAtual});
					
				}
				
			}
			
			// Realiza a totalização dos resultados que será uma nova linha a tabela dos dados do relatório //
			if(!buscaSoOsMateriasDigitais){
				Object [] total = new Object [6];
				total[0] = total[1] = total[2] = total[3] = total[4] = total[5] = 0;
				total[0] = "Total";
				
				for (Object [] r : resultadosNaoAgrupado) {
					total[1] = (Integer) total[1] + (r[1] instanceof BigInteger ? ((BigInteger) r[1]).intValue() : (Integer) r[1]);
					total[2] = (Integer) total[2] + (r[2] instanceof BigInteger ? ((BigInteger) r[2]).intValue() : (Integer) r[2]);
					total[3] = (Integer) total[3] + (r[3] instanceof BigInteger ? ((BigInteger) r[3]).intValue() : (Integer) r[3]);
					total[4] = (Integer) total[4] + (r[4] instanceof BigInteger ? ((BigInteger) r[4]).intValue() : (Integer) r[4]);
					total[5] = (Integer) total[5] + (r[5] instanceof BigInteger ? ((BigInteger) r[5]).intValue() : (Integer) r[5]);
				}
				
				resultadosNaoAgrupado.add(total);	
			
			}
			
		} else { // Se é com Agrupamento
			
			
			
			// Materiais
			resultados = ArrayUtils.agrupar(resultadosNaoAgrupado, String.class, String.class, Object[].class);
			
			// Totalização			
			total = new HashMap<String, int[]>();
			
			for ( Map.Entry<String, Map<String, Object[]>> item : resultados.entrySet() ) {
				total.put(item.getKey(), new int[]{ 0, 0, 0, 0, 0 });				
				
				for ( Object[] linha : item.getValue().values() ) {
					total.get(item.getKey())[0] += ((Number)linha[0]).intValue();
					total.get(item.getKey())[1] += ((Number)linha[1]).intValue();
					total.get(item.getKey())[2] += ((Number)linha[2]).intValue();
					total.get(item.getKey())[3] += ((Number)linha[3]).intValue();
					total.get(item.getKey())[4] += ((Number)linha[4]).intValue();
				}
			}
		}
		
		return forward(PAGINA_RELATORIO);
	}

	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		Collection<SelectItem> temp = new ArrayList<SelectItem>();

		temp.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO.valor, AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO.alias));
		temp.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.COLECAO.valor, AgrupamentoRelatoriosBiblioteca.COLECAO.alias));
		temp.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.valor, AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.alias));
		temp.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.SITUACAO_MATERIAL.valor, AgrupamentoRelatoriosBiblioteca.SITUACAO_MATERIAL.alias));
		temp.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.BIBLIOTECA.valor, AgrupamentoRelatoriosBiblioteca.BIBLIOTECA.alias));
		
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

	public List<Object[]> getResultadosNaoAgrupado() {
		return resultadosNaoAgrupado;
	}
	
	public Map<String, Map<String, Object[]>> getResultados() { 
		return resultados; 
	}

	public Map<String, int[]> getTotal() {
		return total;
	}

	public Map<String, Object[]> getResultadosTotalTitulos() {
		return resultadosTotalTitulos;
	}

	public List<Object[]> getResultadosMateriaisDigitais() {
		return resultadosMateriaisDigitais;
	}

	public void setResultadosMateriaisDigitais(List<Object[]> resultadosMateriaisDigitais) {
		this.resultadosMateriaisDigitais = resultadosMateriaisDigitais;
	}

	
	
}
