/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Bean que controla o relat�rio de total de t�tulos, exemplares e
 * fasc�culos por �rea do CNPq.
 *
 * @author Br�ulio (refactoring)
 */
@Component("relatorioTotalPorCNPqMBean")
@Scope("request")
public class RelatorioTotalPorCNPqMBean extends AbstractRelatorioBibliotecaMBean {

	/**
	 * A p�gina do relat�rio.
	 */
	private static final String PAGINA_RELATORIO = "/biblioteca/controle_estatistico/relatorioTotalCNPQ.jsp";

	/**
	 * Lista de Resultados sem agrupamento do relat�rio que ser� mostrada aos usu�rios.
	 */
	private List<Object[]> resultadosNaoAgrupado;
	
	/**
	 * Lista que guarda os resultados de materiais digitais, se o relat�rio n�o est� agrupado, esses 
	 * resultados s�o mostrados na mesma tabela onde mostra os outros materiais, se o usu�rio escolher 
	 * agrupar os resultados, n�o d� para realizar o agrupamento, tem que mostrar em uma lista 
	 * separada igual a lista de T�tulos.
	 */
	private List<Object[]> resultadosMateriaisDigitais; 
	
	/** 
	 * Resultados do relat�rio com um agrupamento. 
	 */
	private Map<String, Map<String, Object[]>> resultados;
	
	/** 
	 * Totaliza��o de cada tabela do relat�rio com um agrupamento. 
	 */
	private Map<String, int[]> total;
	
	/**
	 * O resultado do relat�rio retornado pela consulta para o total de T�tulos. Usa-se a data de cadastro dos T�tulos no acervo.
	 */
	private Map<String, Object[]> resultadosTotalTitulos;
	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 *  <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	@Override
	public void configurar() {
		setTitulo("Relat�rio de Total por �rea do CNPq");
		setDescricao("<p>Este relat�rio lista a quantidade total de t�tulos, exemplares, fasc�culos e materiais digitais por �rea do CNPq.</p>" +
				"<br/>"+
				"<p>S�o considerados materiais digitais neste relat�rio os T�tulos que n�o possuem materiais f�sicos e possuem o campo de endere�o eletr�nico preenchido. " +
				" Consequentemente os filtros sobre as informa��es dos materiais: Bibliotecas, Cole��es, entre outros, n�o influenciam na contagem de materiais digitais desse relat�rio.</p>" +
				"<br/>"+
				"<br/>"+
				"<p><strong> Observa��o: </strong> O filtro de <strong>Tipo de Classifica��o</strong> � utilizado para recuperar as �reas CNPq que foram geradas " +
				"a partir do tipo de classifica��o escolhida. </p>");
		
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
		
		filtradoPorTipoClassificacao = true; // CDU ou BLACK (utilizando para recuperar as �reas referentes a cada classifica��o)

		fimPeriodo = null;
		inicioPeriodo = null;
	}

	public RelatorioTotalPorCNPqMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 *  <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO);
		
		RelatorioTotalPorCNPqDAO dao = getDAO(RelatorioTotalPorCNPqDAO.class);
		super.configuraDaoRelatorio(dao); // fecha o dao depois da consulta do relat�rio automaticamente.
		
		if (ctgMaterial == CTGMAT_DIGITAIS) { // Se o usu�rio escolher apenas materiais digitais, n�o h� porque agrupar j� que n�o retorna nenhum material f�sico
			agrupamento1 = AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO;
		}
		
		boolean semAgrupamento = agrupamento1 == AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO;
		
		boolean buscaSoOsMateriasDigitais = this.ctgMaterial == CTGMAT_DIGITAIS;
		
		
		if(!buscaSoOsMateriasDigitais){
			/**
			 * Retorna a quantidade de materiais (exemplares e fasc�culos) agrupados por �rea CNPq com a quantidade dos respectivos T�tulos
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
		 * Retorna em uma consulta separada o resultado dos digitais agrupados e ordenados pela �rea CNPq
		 */
		resultadosMateriaisDigitais = dao.findTotalMateriaisDigitais(inicioPeriodo, fimPeriodo, classificacaoEscolhida);
		
		
		/** 
		 * Retorna a quantidade de T�tulos mostrada em um lista separada   
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
			 *  N�o a problema fazer dois for alinhados porque a quantidade de resultados � pequena.
			 *  
			 *  Se o usu�rio escolher agrupar os resultados n�o pode mostrar os materiais digitais na mesma tabela
			 *  tem que mostrar em uma tabela separada igual a de T�tulos
			 */
			for (int index2 = 0; index2 < resultadosMateriaisDigitais.size(); index2++) {
				
				boolean achouResutado = false;
				
				String descricaoAreaAtual = (String) resultadosMateriaisDigitais.get(index2)[0];
				BigInteger qtdMateriaisDigitaisAtual = (BigInteger) resultadosMateriaisDigitais.get(index2)[1];
				
				forInterno:
					for (int index = 0; index < resultadosNaoAgrupado.size(); index++) {
					
					String descricaoAreaResultados = (String)  resultadosNaoAgrupado.get(index)[0];	
						
					if ( descricaoAreaResultados.equals(descricaoAreaAtual ) ){ // Se a descri��o das �reas s�o iguais
						resultadosNaoAgrupado.get(index)[5] = qtdMateriaisDigitaisAtual; // a quantidade de materias digitais buscados na segunda consulta.
						achouResutado = true;
						break forInterno;
					}
					
				}
				
				if(! achouResutado){
						resultadosNaoAgrupado.add(new Object[]{descricaoAreaAtual, qtdMateriaisDigitaisAtual, 0, 0, 0, qtdMateriaisDigitaisAtual});
					
				}
				
			}
			
			// Realiza a totaliza��o dos resultados que ser� uma nova linha a tabela dos dados do relat�rio //
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
			
		} else { // Se � com Agrupamento
			
			
			
			// Materiais
			resultados = ArrayUtils.agrupar(resultadosNaoAgrupado, String.class, String.class, Object[].class);
			
			// Totaliza��o			
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
	 * Ver coment�rios da classe pai.<br/>
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
	 * Ver coment�rios da classe pai.<br/>
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
