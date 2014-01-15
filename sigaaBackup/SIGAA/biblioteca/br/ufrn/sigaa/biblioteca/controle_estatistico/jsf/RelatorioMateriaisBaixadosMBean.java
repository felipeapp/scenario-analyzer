/*
 * RelatorioMateriaisBaixadosMBean.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 10/12/2009
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioMateriaisBaixadosDao;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosRelatorioMateriaisBaixados;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;

/**
 *    MBean que gerencia o relatório de materiais baixados do acervo.
 *
 * @author Jadson
 * @author Bráulio
 * @version 2.0 - Mudando para utilizar o AbstractRelatorioBibliotecaMBean, adicionando novos filtro, como classificação CDU
 */
@Component("relatorioMateriaisBaixadosMBean")
@Scope("request")
public class RelatorioMateriaisBaixadosMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** Os resultados do relatório analítico . */
	private List<DadosRelatorioMateriaisBaixados> dadosRelatorio;
	
	/** Os resultados do relatório sintético . */
	private List<Object[]> resultadoSintetico;
	
	
	/** O total geral de títulos para o relatório analítico. */
	private int totalTitulos;
	
	
	
	/** Página que gera o relatório analítico que lista os materiais baixados em detalhe. */
	private static final String PAG_RELATORIO_ANALITICO = "/biblioteca/controle_estatistico/relatorioMateriaisBaixadosAnalitico.jsp";
	
	/** Página que gera o relatório sintético dos outros tipos de agrupamento que não sejam por mês. */
	private static final String  PAG_RELATORIO_SINTETICO =
			"/biblioteca/controle_estatistico/relatorioMateriaisBaixadosSintetico.jsp";
	
	/**
	 * Construtor padrão
	 */
	public RelatorioMateriaisBaixadosMBean(){
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
		
		setTitulo( "Relatório de Materiais Baixados do Acervo" );
		setDescricao("<p> Neste relatório é possível consultar os materiais que foram baixados do acervo do sistema. </p>" +
					 "<p> O filtro \"Tipo de Classificação\" faz referência a classificação digitada nos campos \"Classe Inicial\" e \"Classe Final\" e também será a classificação mostrada no relatório analítico. </p>" +
				     "<p> <strong> Observação 1: </strong> O filtro de \"Modalidade de Aquisição\" as modalidade de aquisição \"Compra\" e \"Doação\" só se aplicam aos fascículos, haja visto que para exemplare ainda não é guardado essa informação. </p>" + 
				  	 "<p> <strong> Observação 2: </strong> O agrupamento só é considerado para o relatório do tipo sintético. </p>");
		
		
		filtradoPorVariasBibliotecas = true;
		campoBibliotecaObrigatorio = false;
		
		
		filtradoPorPeriodo = true;
		
		
		filtradoPorTipoClassificacao = true; // se CDU ou BACK ou outra
		
		filtradoPorIntervaloClassificacao = true; // Classe inicial e final
		campoIntervaloClassificacaoObrigatorio = false;
		
		filtradoPorModalidadeAquisicao = true; // compra, doação
		
		filtradoPorFormaRelatorio = true; // (analitico ou sintetico)
		filtradoPorCtgMaterial = true;
		
		filtradoPorAgrupamento1 = true;
		
		inicioPeriodo = CalendarUtils.adicionaMeses(inicioPeriodo, -1); // por padrão, recupera as transferências do último mês
		
	}
	
	
	/**
	 * Gera o relatório após o usuário preencher os filtros.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/formRelatorioMateriaisBaixados.jsp</li></ul>
	 */
	public String gerarRelatorio() throws DAOException {
		
		RelatorioMateriaisBaixadosDao dao = getDAO(RelatorioMateriaisBaixadosDao.class);
		configuraDaoRelatorio(dao);
		
		if ( formatoRelatorio == SINTETICO )
			return gerarRelatorioSintetico(dao);
		else
			return gerarRelatorioAnalitico(dao);
	}
	
	
	/**
	 * Gera o relatório analítico: um relatório com todos os materiais que
	 * foram baixados no período especificado.
	 */
	private String gerarRelatorioAnalitico(RelatorioMateriaisBaixadosDao dao) throws DAOException {
		
		dadosRelatorio = new ArrayList<DadosRelatorioMateriaisBaixados>();
		
		/* Utilizando para contar os títulos dos materiais baixados de forma não repetida */
		Set<Integer> titulosDosMateriaisBaixados = new HashSet<Integer>();
		
		String sqlModalidadeAquisicaoExemplares = "";
		String sqlModalidadeAquisicaoFasciculos = "";
		
		switch(tipoModalidadeEscolhida){
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_TODAS:
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_INDEFINIDA:
				sqlModalidadeAquisicaoExemplares = " AND 1 = 1 ";
				sqlModalidadeAquisicaoFasciculos = " AND a.modalidade_aquisicao IS NULL ";
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_COMPRA:
				sqlModalidadeAquisicaoExemplares = " AND 1 = 0 ";
				sqlModalidadeAquisicaoFasciculos = " AND a.modalidade_aquisicao = "+Assinatura.MODALIDADE_COMPRA;
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_DOACAO:
				sqlModalidadeAquisicaoExemplares = " AND 1 = 0 ";
				sqlModalidadeAquisicaoFasciculos = " AND a.modalidade_aquisicao = "+Assinatura.MODALIDADE_DOACAO;
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_SUBSITITUICAO:
				sqlModalidadeAquisicaoExemplares = " AND e.id_exemplar_que_eu_substituo IS NOT NULL ";
				sqlModalidadeAquisicaoFasciculos = " AND f.id_fasciculo_que_eu_substituo IS NOT NULL ";
				break;
		}
		
		List<Object[]> lista = dao.findMateriaisBaixadosPorPeriodoAnalitico(
					UFRNUtils.toInteger(variasBibliotecas) , inicioPeriodo, fimPeriodo,
					( ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES ),
					( ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS ),
					classificacaoEscolhida, classeInicial, classeFinal, sqlModalidadeAquisicaoExemplares, sqlModalidadeAquisicaoFasciculos);
		
		if ( lista.isEmpty() ) {
			erros.addMensagem( MensagensArquitetura.BUSCA_SEM_RESULTADOS );
			validacaoDados(erros);
			return null;
		}
		
			
		for (Object[] array : lista) {
			dadosRelatorio.add(
					new DadosRelatorioMateriaisBaixados(
					(Integer)array[0],  // id material
					(String)array[1],   // código barras
					(array[8] != null ? array[8].toString() : null), //numero patrimonio
					(String)array[2],   // motivo baixa
					(Integer) array[3], // id biblioteca 
					(String) array[4], (String) array[5], (Date)array[6], // descrição biblioteca, nome usuário e data baixa
					(String)array[7], // informações do Título
					classificacaoEscolhida == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO1 ? (String) array[9] : null, // as classificações bibliográficas
					classificacaoEscolhida == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO2 ? (String) array[10] : null, // as classificações bibliográficas
					classificacaoEscolhida == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO3 ? (String) array[11] : null, // as classificações bibliográficas
					(String) array[12], (String) array[13], // tipo de material e coleção
					(Integer) array[14]) );  // o id do título
			
			titulosDosMateriaisBaixados.add(   (Integer) array[14]  );
		}
		
		totalTitulos = titulosDosMateriaisBaixados.size();
		
		Collections.sort(dadosRelatorio);
		
		return forward(PAG_RELATORIO_ANALITICO);
	}
	
	
	/**
	 * Gera o relatório sintético: um relatório com o total de materiais baixados
	 * num determinado ano, com os dados agrupados por mês, classe CDU, classe
	 * Black, tipo de material ou coleção.
	 */
	private String gerarRelatorioSintetico(RelatorioMateriaisBaixadosDao dao) throws DAOException {
		
		String sqlModalidadeAquisicaoExemplares = "";
		String sqlModalidadeAquisicaoFasciculos = "";
		
		switch(tipoModalidadeEscolhida){
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_TODAS:
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_INDEFINIDA:
				sqlModalidadeAquisicaoExemplares = " AND 1 = 1 ";
				sqlModalidadeAquisicaoFasciculos = " AND a.modalidade_aquisicao IS NULL ";
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_COMPRA:
				sqlModalidadeAquisicaoExemplares = " AND 1 = 0 ";
				sqlModalidadeAquisicaoFasciculos = " AND a.modalidade_aquisicao = "+Assinatura.MODALIDADE_COMPRA;
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_DOACAO:
				sqlModalidadeAquisicaoExemplares = " AND 1 = 0 ";
				sqlModalidadeAquisicaoFasciculos = " AND a.modalidade_aquisicao = "+Assinatura.MODALIDADE_DOACAO;
				break;
			case AbstractRelatorioBibliotecaMBean.MODALIDADE_AQUISICAO_SUBSITITUICAO:
				sqlModalidadeAquisicaoExemplares = " AND e.id_exemplar_que_eu_substituo IS NOT NULL ";
				sqlModalidadeAquisicaoFasciculos = " AND f.id_fasciculo_que_eu_substituo IS NOT NULL ";
				break;
		}
		
		resultadoSintetico = dao.findQtdMateriaisBaixadosSintetico(UFRNUtils.toInteger(variasBibliotecas) , inicioPeriodo, fimPeriodo,
				( ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_EXEMPLARES ),
				( ctgMaterial == CTGMAT_TODOS || ctgMaterial == CTGMAT_FASCICULOS ),
				classificacaoEscolhida, classeInicial, classeFinal, tipoModalidadeEscolhida, agrupamento1
				, sqlModalidadeAquisicaoExemplares, sqlModalidadeAquisicaoFasciculos);		
		
		if ( resultadoSintetico == null || resultadoSintetico.size() == 0 ) {
			addMensagem( MensagensArquitetura.BUSCA_SEM_RESULTADOS );
			return null;
		}
		
		AgrupamentoRelatoriosBiblioteca agrupamentoUtilizado = agrupamento1;
		
		if( agrupamentoUtilizado == AgrupamentoRelatoriosBiblioteca.MES){
			
			for (Object[] objects : resultadoSintetico) {
				objects[0] = CalendarUtils.getNomeMes( ((Double)objects[0]).intValue() );   // troca o número pele nome do mês
			}
		} 
			
		return forward(PAG_RELATORIO_SINTETICO);
		
	}
	

	
	
	// GETs e SETs
	
	public List<DadosRelatorioMateriaisBaixados> getDadosRelatorio() {
		return dadosRelatorio;
	}
	
	public int getTotalTitulos() {
		return totalTitulos;
	}
	
	public String getClasseInicial() {
		return classeInicial;
	}

	public void setClasseInicial(String classeInicial) {
		this.classeInicial = classeInicial;
	}

	public String getClasseFinal() {
		return classeFinal;
	}

	public void setClasseFinal(String classeFinal) {
		this.classeFinal = classeFinal;
	}
	
	public List<Object[]> getResultadoSintetico() {
		return resultadoSintetico;
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
			
			agrupamentos1.add( new SelectItem(AgrupamentoRelatoriosBiblioteca.MES.valor, AgrupamentoRelatoriosBiblioteca.MES.alias));
			
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
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		// Não usado nesse relatório //
		return null;
	}


}
